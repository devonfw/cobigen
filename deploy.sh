#!/bin/sh
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
    echo "  * With test execution"
else
	ENABLED_TEST="-DskipTests"
    echo "  * No test execution (pass 'test' as argument to enable)"
fi

if [[ "$*" == *parallel* ]]
then
    PARALLELIZED="-T1C"
    echo "  * Parallel execution of 1 thread per core"
else
    PARALLELIZED=""
    echo "  * No parallel execution (pass 'parallel' as argument to enable)"
fi

if [[ "$*" == *debug* ]]
then
    DEBUG="-DtrimStackTrace=false -Dtycho.debug.resolver=true" # set to false to see hidden exceptions
    echo "  * Debug On"
else
	# the latter will remove maven download logs / might cause https://stackoverflow.com/a/66801171 issues
    DEBUG="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    echo "  * Debug Off (pass 'debug' as argument to enable)"
fi

if [[ "$*" == *gpgkey=* ]]
then
    GPG_KEYNAME=$(echo "$*" | sed -r -E -n 's|gpgkey=([^\s]+)|\1|p')
    echo "  * GPG Key set to $GPG_KEYNAME"
fi

if [[ $(sed -r -E -n 's@<revision>([^<]+)-SNAPSHOT</revision>@\1@p' pom.xml) ]]
then
    DEPLOY_UPDATESITE="test"
    echo "  * Detected snapshot release number. Releasing to test p2 repository"
else
	if [ -z $GPG_KEYNAME ]
	then 
		echo "Please set GPG keyname by passing gpgkey=<your-email>"
		exit 1
	fi
	DEPLOY_UPDATESITE="stable"
    echo "  * Detected final release number. Releasing to stable p2 repository"
fi
echo ""
echo "##########################################"

# $1: yes/no question
function doAskQuestion() {
	local question="${1}"
  
	local answer
	while true
	do
	  echo -e "${question}"
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

# check preconditions
if [ "$DEPLOY_UPDATESITE" = "stable" ] && [ -d "../gh-pages" ]
then
    cd ../gh-pages
	CHANGE_LIST=$(git diff --shortstat && git status --porcelain)
	if [ ! -z "$CHANGE_LIST" ]
	then
		echo "../gh-pages is not clean"
		doAskQuestion "Should I cleanup?" # will exit if no
		doRunCommand "git reset --hard HEAD"
		doRunCommand "git clean -xf"
	fi
	cd "$SCRIPT_PATH"
else
	echo "Not detected cloned gh-pages branch in ../gh-pages folder."
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

# https://stackoverflow.com/a/66801171
BATCH_MODE="-Djansi.force=true -Djansi.passthrough=true -B"
DEPLOY_SIGN="-Poss -Dgpg.keyname=$GPG_KEYNAME -Dgpg.executable=gpg"

log_step "Cleanup Projects"
doRunCommand "mvn clean -P!p2-build $PARALLELIZED $BATCH_MODE"

log_step "Build & Test Core"
# need to exclude cobigen-core-systemtest as of https://issues.sonatype.org/browse/NEXUS-19853 for deployment only!
doRunCommand "mvn deploy -f cobigen --projects !cobigen-core-systemtest -P!p2-build $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE $DEPLOY_SIGN"

log_step "Build & Test Core Plugins"
doRunCommand "mvn deploy -f cobigen-plugins -P!p2-build $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE $DEPLOY_SIGN"

log_step "Build Core Plugins - P2 Update Sites"
doRunCommand "mvn package bundle:bundle -Pp2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE "
doRunCommand "mvn install bundle:bundle -Pp2-bundle -DskipTests p2:site -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE "
doRunCommand "mvn deploy -Pp2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE"

log_step "Package & Run E2E Tests"
doRunCommand "mvn test -f cobigen/cobigen-core-systemtest -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE"
# need to exclude cli-systemtest as of https://issues.sonatype.org/browse/NEXUS-19853 for deployment only!
doRunCommand "mvn deploy -f cobigen-cli --projects !cli-systemtest -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN"
# need to exclude cobigen-maven-systemtest as of https://issues.sonatype.org/browse/NEXUS-19853 for deployment only!
doRunCommand "mvn deploy -f cobigen-maven --projects !cobigen-maven-systemtest -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN"
doRunCommand "mvn deploy -f cobigen-templates -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN"
doRunCommand "mvn deploy -f cobigen-eclipse -DskipTests $ENABLED_TEST $DEBUG $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE --projects cobigen-eclipse-updatesite"
