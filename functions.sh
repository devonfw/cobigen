#!/usr/bin/env bash
set -e

SCRIPT_PATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
pushd "$SCRIPT_PATH"
trap popd EXIT

echo ""
echo "##########################################"
echo ""
echo "Script config: "
if [[ "$*" == *test* ]]
then
  ENABLED_TEST=""
  echo "  > With test execution"
else
  ENABLED_TEST="-DskipTests"
  echo "  * No test execution (pass 'test' as argument to enable)"
fi

if [[ "$*" == *batch* ]]
then
  # https://stackoverflow.com/a/66801171 # the latter will remove maven download logs / might cause https://stackoverflow.com/a/66801171 issues
  BATCH_MODE="-Djansi.force=true -Djansi.passthrough=true -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
  echo "  > Running in batch mode"
else
  BATCH_MODE=""
  echo "  * No batch mode, showing all downloads + ascii colors (pass 'batch' as argument to enable)"
fi

if [[ "$*" == *parallel* ]]
then
  PARALLELIZED="-T1C"
  echo "  > Parallel execution of 1 thread per core"
else
  PARALLELIZED=""
  echo "  * No parallel execution (pass 'parallel' as argument to enable)"
fi

if [[ "$*" == *debug* ]]
then
  DEBUG="-DtrimStackTrace=false -Dtycho.debug.resolver=true -X" # set to false to see hidden exceptions
  echo "  > Debug Mode"
else
  DEBUG=""
  echo "  * No debug mode (pass 'debug' as argument to enable)"
fi

if [[ "$*" == *dryrun* ]]
then
  DRYRUN=false
  echo "  > Dryrun - No git push will be executed"
else
  DRYRUN=true
  echo "  * No dryrun (pass 'dryrun' as argument to enable)"
fi

if [[ "$*" == *silent* ]]
then
  SILENT=true
  echo "  > Silent execution (accept all confirmations silently)"
else
  SILENT=false
  echo "  * No silent execution (pass 'silent' as argument to enable)"
fi

log_step() {
  echo ""
  echo ""
  echo "##########################################"
  echo "### $1"
  echo "##########################################"
  echo ""
  echo ""
}

# $1: command
# $2: message
# $3: optional working directory
function doRunCommand() {
  local cwd=${PWD}
  if [ -n "${3}" ]
  then
    if [ -d "${3}" ]
    then
      cd "${3}" || exit 1
    else
      exit 255
    fi
  fi
  echo "Running command: ${1}"
  eval "${1}"
  result=${?}
  if [ -n "${3}" ]
  then
    cd "${cwd}" || exit 1
  fi
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
    exit ${result}
  fi
}

# $1: yes/no question
function doAskQuestion() {
	local question="${1}"
  
	local answer
	while true
	do
	  echo -e "${question}"
    if [ "$SILENT" = true ] ;
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