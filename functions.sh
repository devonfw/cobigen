#!/usr/bin/env bash
SCRIPT_PATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
pushd "$SCRIPT_PATH"
trap popd EXIT ERR

echo ""
echo "##########################################"
echo ""

# all components
ALL_COMPONENTS=(core plugins templates maven eclipse cli)

# A POSIX variable
OPTIND=1         # Reset in case getopts has been used previously in the shell.

function show_help() {
cat << EOF  

Usage of CobiGen build & deploy script  

  -b | --batch             Batch mode to prevent from issues like https://stackoverflow.com/a/66801171
  -c | --coverage          Create code coverage report
  -C | --components=       Components to build/deploy. By default all possible values $( IFS=$','; echo "${ALL_COMPONENTS[*]}" ). Can be multiple, commaseparated
  -d | --dirty             Dirty execution running no maven clean before build
  -g | --gpgkey=           GPG key name to be passed for code signing
  -h | --help              Show help
  -p | --parallel          Parallelize execution (1 Thread per Core)
  -s | --ci-settings       Executing maven with .m2/ci-settings.xml
  -t | --test              Execute tests
  -x | --debug             Run debug mode (highly verbose logs)
  -y | --silent-confirm    Agree to all yes / no questions silently
  -z | --dryrun            Dry run (no git push will be executed) - needed to test the release script

EOF
}

# defaults
COMPONENTS_TO_BUILD=()
ENABLED_TEST="-DskipTests"
MVN_SETTINGS=""
BATCH_MODE=""
PARALLELIZED=""
DEBUG=""
DRYRUN=false
NO_CLEAN=false
GPG_KEYNAME=""
COVERAGE=""

echo "Configuration: "

# Automatically get GPG key from env
if [[ -n "$GPG_KEY" ]]
then
  GPG_KEYNAME=$GPG_KEY
  echo -e "\e[92m  > GPG Key set to $GPG_KEYNAME\e[39m"
fi

MVN_DEBUG="-DtrimStackTrace=false -Dtycho.debug.resolver=true -X"
# Automatically enable debug logging on github actions debug run
if [[ "$ACTIONS_STEP_DEBUG" == true ]]
then
  DEBUG="$MVN_DEBUG"
  echo -e "\e[92m  > Running in debug mode\e[39m"
fi

echo -e "\e[91m"

if ! options="$(getopt -l "batch,coverage,components:,dirty,gpgkey:,help,parallel,repo-settings,test,debug,silent-confirm,dryrun" -o "bcdg:hpstxyz" -- "$@")"; then
  echo -e "\e[39m"
  show_help
  exit 1
fi
eval set -- "$options"

#while getopts "h?ts" opt; do
while true
do
  case "$1" in
    -b|--batch)
      # the latter will remove maven download logs / might cause https://stackoverflow.com/a/66801171 issues
      BATCH_MODE="-Djansi.force=true -Djansi.passthrough=true -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
      echo -e "\e[92m  > Running in batch mode\e[39m"
      ;;
    -c|--coverage)
      COVERAGE="-Dskip.code.coverage=false"
      echo -e "\e[92m  > Creating Code Coverage report\e[39m"
      ;;
    -C|--components)
      shift
      IFS=',' read -r -a COMPONENTS <<< $1
      COMPONENTS_TO_BUILD+=(${COMPONENTS[@]})
      echo -e "\e[92m  > Build components $( IFS=$','; echo "${COMPONENTS[*]}" )\e[39m"
      ;;
    -d|--dirty)
      NO_CLEAN=true
      echo -e "\e[92m  > Skip mvn clean\e[39m"
      ;;
    -g|--gpgkey)
      shift
      GPG_KEYNAME=$1
      echo -e "\e[92m  > GPG Key set to $GPG_KEYNAME\e[39m"
      ;;
    -h|--help)
      show_help
      exit 0
      ;;
    -p|--parallel)
      PARALLELIZED="-T1C"
      echo -e "\e[92m  > Parallel execution of 1 thread per core\e[39m"
      ;;
    -s|--repo-settings) 
      MVN_SETTINGS="-s .mvn/ci-settings.xml"
      echo -e "\e[92m  > Executing maven with settings from .mvn/settings.xml \e[39m"
      ;;
    -t|--test) 
      ENABLED_TEST=""
      echo -e "\e[92m  > With test execution\e[39m"
      ;;
    -x|--debug)
      DEBUG="$MVN_DEBUG"
      echo -e "\e[92m  > Running in debug mode\e[39m"
      ;;
    -y|--silent-confirm)
      SILENT=true
      echo -e "\e[92m  > Silent confirmation \e[93m(accept all confirmations silently)\e[39m"
      ;;
    -z|--dryrun)
      DRYRUN=true
      echo -e "\e[92m  > Dryrun - No git push will be executed\e[39m"
      ;;
    --)
      shift
      break
    ;;
  esac
  shift
done

if [[ ${#COMPONENTS_TO_BUILD[@]} = 0 ]]
then
  echo -e "\e[92m  > Build all components\e[39m"
  COMPONENTS_TO_BUILD=${ALL_COMPONENTS[@]}
fi

## VALIDATE

if [[ $(basename $0) != "build.sh" ]]
then
  echo -e "\e[91m  !ERR! Cannot sign artifacts without passing a gpg key for signing. Please pass gpgkey=<your key> as a parameter or GPG_KEY as secret.\e[39m"
  exit 1
fi

DEPLOY_SIGN="-Poss -Dgpg.keyname=$GPG_KEYNAME -Dgpg.executable=gpg"
if [[ "$SILENT" = true ]] && [[ -z "$GPG_PASSPHRASE" ]]
then
  echo -e "\e[91m  !ERR! to comply to 'silent' parameter semantics, you should pass GPG_PASSPHRASE as secret to be able to sign artifacts.\e[39m"
  exit 1
fi

log_step() {
  echo -e "\e[95m"
  echo -e ""
  echo -e "##########################################"
  echo -e "### $1"
  echo -e "##########################################"
  echo -e ""
  echo -e "\e[39m"
}

# $1: command
# $2: (optional) fail on error
function doRunCommand() {
  local cwd=${PWD}
  echo "Running command: ${1}"
  eval "${1}"
  result=${?}
  local message
  if [ -z "${2}" ]
  then
    message="run command ${1/ */}"
  else
    message="${2} (${1/ */})"
  fi
  if [ ${result} = 0 ]
  then
    echo "Succeeded to ${message}"
  else
    echo "Failed to run command: ${1}"
    if [ "$2" != false ]
    then
      exit ${result}
    fi
  fi
}

# $1: yes/no question
function doAskQuestion() {
	local question="${1}"
  
	local answer
	while true
	do
	  echo -e "${question}"
    if [ "$SILENT" = true ]
    then
      echo "(Yes) - Silent agreement by 'silent' argument."
      return
    fi

	  read -r -p "(yes/no): " answer
	  if [ "${answer}" = "yes" ] || [ -z "${answer}" ]
	  then
		  return
	  elif [ "${answer}" = "no" ]
	  then
		  echo "No..."
		  exit 255
	  else
		  echo "Please answer yes or no (or hit return for yes)."
	  fi
	done
}

function gitCleanup() {
  doRunCommand "git reset --hard HEAD"
  doRunCommand "git clean -xf"
  doRunCommand "git pull"
}

function trim()
{
    local trimmed="$1"

    # Strip leading spaces.
    while [[ $trimmed == ' '* ]]; do
       trimmed="${trimmed## }"
    done
    # Strip trailing spaces.
    while [[ $trimmed == *' ' ]]; do
        trimmed="${trimmed%% }"
    done

    echo "$trimmed"
}

function pauseUntilKeyPressed() {
  if [[ "$SILENT" = false ]]
  then
    echo ""
    read -p "Press any key to resume with cleanup after reviewing the deployments ..."  
  fi
}
