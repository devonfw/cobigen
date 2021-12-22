#!/usr/bin/env bash
SCRIPT_PATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
pushd "$SCRIPT_PATH"
trap popd EXIT ERR

echo ""
echo "##########################################"
echo ""
echo "Script config: "
if [[ "$*" == *test* ]]
then
  ENABLED_TEST=""
  echo -e "\e[92m  > With test execution\e[39m"
else
  ENABLED_TEST="-DskipTests"
  echo "  * No test execution (pass 'test' as argument to enable)"
fi

if [[ "$*" == *repo-mvn-settings* ]]
then
  MVN_SETTINGS="-s .mvn/settings.xml"
  echo -e "\e[92m  > Executing with .mvn/settings.xml \e[39m"
else
  MVN_SETTINGS=""
  echo "  * Executing with individually configured settings.xml (pass 'repo-mvn-settings' as argument to enable execution with .mvn/settings.xml)"
fi

if [[ "$*" == *batch* ]]
then
  # https://stackoverflow.com/a/66801171 # the latter will remove maven download logs / might cause https://stackoverflow.com/a/66801171 issues
  BATCH_MODE="-Djansi.force=true -Djansi.passthrough=true -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
  echo -e "\e[92m  > Running in batch mode\e[39m"
else
  BATCH_MODE=""
  echo "  * No batch mode, showing all downloads + ascii colors (pass 'batch' as argument to enable)"
fi

if [[ "$*" == *parallel* ]]
then
  PARALLELIZED="-T1C"
  echo -e "\e[92m  > Parallel execution of 1 thread per core\e[39m"
else
  PARALLELIZED=""
  echo "  * No parallel execution (pass 'parallel' as argument to enable)"
fi

if [[ "$*" == *debug* ]]
then
  DEBUG="-DtrimStackTrace=false -Dtycho.debug.resolver=true -X" # set to false to see hidden exceptions
  echo -e "\e[92m  > Debug Mode\e[39m"
else
  DEBUG=""
  echo "  * No debug mode (pass 'debug' as argument to enable)"
fi

if [[ "$*" == *dryrun* ]]
then
  DRYRUN=true
  echo -e "\e[92m  > Dryrun - No git push will be executed\e[39m"
else
  DRYRUN=false
  echo "  * No dryrun (pass 'dryrun' as argument to enable)"
fi

if [[ "$*" == *silent* ]]
then
  SILENT=true
  echo -e "\e[92m  > Silent execution \e[93m(accept all confirmations silently)\e[39m"
else
  SILENT=false
  echo "  * No silent execution (pass 'silent' as argument to enable)"
fi

if [[ "$*" == *no-clean* ]]
then
  NO_CLEAN=true
  echo -e "\e[92m  > Skip mvn clean\e[39m"
else
  NO_CLEAN=false
  echo "  * Executing mvn clean before execution (pass 'no-clean' as argument to skip)"
fi

if [[ "$*" == *gpgkey=* ]]
then
  GPG_KEYNAME=$(echo "$*" | sed -r -E -n 's|.*gpgkey=([^ ]+).*|\1|p')
  echo -e "\e[92m  > GPG Key set to $GPG_KEYNAME\e[39m"
elif [[ -n "$GPG_KEY" ]]
then
  GPG_KEYNAME=$GPG_KEY
  echo -e "\e[92m  > GPG Key set to $GPG_KEYNAME\e[39m"
elif [[ $(basename $0) != "build.sh" ]]
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
