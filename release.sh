#!/usr/bin/env bash
> release.log
exec > >(tee -i release.log)
exec 2>&1

CALL_PARAMS=$*

source "$(dirname "${0}")"/functions.sh

DEPLOYED=false

undoRelease() {
  if { [ $? -ne 0 ] || [ "$DRYRUN" = true ]; } && [ "$DEPLOYED" = true ];
  then
    cd $SCRIPT_PATH
    log_step "Drop all sonatype releases as the release script exited abnormally or it was a dryrun"
    pauseUntilKeyPressed
    
    doRunCommand "mvn nexus-staging:drop $MVN_SETTINGS -f cobigen $DEBUG $BATCH_MODE" false
    doRunCommand "mvn nexus-staging:drop $MVN_SETTINGS -f cobigen-plugins $DEBUG $BATCH_MODE" false
    doRunCommand "mvn nexus-staging:drop $MVN_SETTINGS -f cobigen-cli $DEBUG $BATCH_MODE" false
    doRunCommand "mvn nexus-staging:drop $MVN_SETTINGS -f cobigen-maven $DEBUG $BATCH_MODE" false
    doRunCommand "mvn nexus-staging:drop $MVN_SETTINGS -f cobigen-templates $DEBUG $BATCH_MODE" false
    doRunCommand "git push origin :refs/tags/v$RELEASE_VERSION" false
    
    log_step "Cleanup ../gh-pages"
    cd ../gh-pages
    gitCleanup
    cd $SCRIPT_PATH

    if [ "$DRYRUN" = true ]
    then
      exit 0
    else
      exit 1
    fi
  fi
  # redo popd manually, as this EXIT trap will overwrite the popd trap from functions.sh
  popd
}
trap 'undoRelease' EXIT ERR

if [[ "$*" == *skip-qa* ]]
then
  SKIP_TESTRUN=true
  echo -e "\e[93m  !!! Explicitly notified that tests already executed successful beforehand - disabling tests!\e[39m"
else
  SKIP_TESTRUN=false
fi

echo ""
echo "##########################################"
echo ""
echo "Checking preconditions:"

# check preconditions
cd "$SCRIPT_PATH"

GIT_STATUS="$(git diff --shortstat && git status --porcelain)"
if [[ -z "$GIT_STATUS" ]]
then
  echo "  * Working copy clean, continuing release"
else
  echo -e "\e[91m  !ERR! Working copy not clean. Please make sure everything is committed and pushed.\e[39m"
  echo ""
  echo "git diff --shortstat && git status --porcelain"
  echo "$GIT_STATUS"
  exit 1
fi

# Check if we are in correct state
SED_OUT="$(sed -r -E -n 's@<revision>([^<]+)-SNAPSHOT</revision>@\1@p' pom.xml)"
if [[ -n "$SED_OUT" ]]
then
  echo "  * Detected development revision $SED_OUT"
else
  SED_OUT="$(sed -r -E -n 's@<revision>([0-9]+\.[0-9]+\.[0-9]+)</revision>@\1@p' pom.xml)"
  if [[ -n "$SED_OUT" ]]
  then
    echo -e "\e[91m  !ERR! Detected release revision $SED_OUT. This script is intended to be executed on -SNAPSHOT versions only.\e[39m"
    exit 1
  else
    echo -e "\e[91m  !ERR! No revision detected in pom.xml.\e[39m"
    exit 1
  fi
fi

ORIGIN="$(git config --get remote.origin.url)"
case "$ORIGIN" in
  *devonfw/cobigen*) echo "  * Detected clone from $ORIGIN." ;;
  *) echo -e "\e[91m  !ERR! You are working on a fork, please make sure, you are releasing from devonfw/cobigen#master\e[39m" && exit 1 ;;
esac
echo ""
echo "##########################################"
echo ""

log_step "Remove -SNAPSHOT from revision"
doRunCommand "sed -E -i 's@<revision>([^<]+)-SNAPSHOT</revision>@<revision>\1</revision>@' pom.xml"
SED_OUT="$(sed -r -E -n 's@<revision>([0-9]+\.[0-9]+\.[0-9]+)</revision>@\1@p' pom.xml)"
RELEASE_VERSION=$(trim $SED_OUT)
if [[ -z "$RELEASE_VERSION" ]]
then
  echo -e "\e[91m  !ERR! could not set release revision in /pom.xml\e[39m"
  exit 1
else 
  echo "Set release revision to $RELEASE_VERSION"
fi

log_step "Build to set revision $RELEASE_VERSION for p2 artifacts"
doRunCommand "bash ./build.sh parallel $CALL_PARAMS"

log_step "Commit set release revision $RELEASE_VERSION"
doRunCommand "git add -u"
doRunCommand "git commit -m'Set release version $RELEASE_VERSION'"

if [[ -z "$SKIP_TESTRUN" ]]
then
  log_step "Final test run of new release"
  # need to run in extra command for the tests as P2 resolution is done before the maven build aggregator and thus does not reflect the revision changes directly
  # also it's not recommended to run the final test in the deploy script as flaky UI tests could end up in a partial release.
  # Need to evaluate mvn nexus-s  taging:release command usage as we are running multiple commands it's not a trivial use and not intended as we would use it
  doRunCommand "bash ./build.sh test $CALL_PARAMS"
fi

log_step "Deploy Release ${RELEASE_VERSION}"
# need to activate beforehand to cleanup if an error occurred
DEPLOYED=true
doRunCommand "bash ./deploy.sh $CALL_PARAMS"

log_step "Create Git Tag v${RELEASE_VERSION}"
doRunCommand "git tag v${RELEASE_VERSION}"

log_step "Increase revision and convert to SNAPSHOT"
SED_OUT="$(sed -r -E -n 's@<revision>([0-9]+)\.([0-9]+)\.([0-9]+)</revision>@\3@p' pom.xml)"
SED_OUT=$(trim $SED_OUT)
if [[ -z "$SED_OUT" ]]
then
  echo -e "\e[91m  !ERR! could not identify release revision in /pom.xml\e[39m"
  exit 1
else
  SED_OUT=$((SED_OUT+1))
  NEW_PATCH=$(printf "%03d\n" $SED_OUT)
  VERSION_PREFIX="$(sed -r -E -n 's@<revision>([0-9]+)\.([0-9]+)\.([0-9]+)</revision>@\1.\2.@p' pom.xml)"
  VERSION_PREFIX=$(trim $VERSION_PREFIX)
  NEW_VERSION="$VERSION_PREFIX$NEW_PATCH-SNAPSHOT"
  doRunCommand "sed -E -i 's@<revision>([^<]+)</revision>@<revision>$NEW_VERSION</revision>@' pom.xml"
fi

log_step "Build to set revision $NEW_VERSION for p2 artifacts"
doRunCommand "bash ./build.sh no-clean parallel $CALL_PARAMS"

log_step "Commit set revision $NEW_VERSION"
doRunCommand "git add -u"
doRunCommand "git commit -m'Set next revision $NEW_VERSION'"

if [[ "$DRYRUN" = true ]]
then
  log_step "[DRYRUN] Review and Abort"
  echo "Git Status of ../gh-pages for review:"
  doRunCommand "cd ../gh-pages && git status"
  cd $SCRIPT_PATH
  exit 0
else
  log_step "Publish Release"
  doRunCommand "cd ../gh-pages"
  doRunCommand "git commit -m'Deploy P2 Bundles for Release $NEW_VERSION'"
  doRunCommand "git push origin gh-pages"
  doRunCommand "cd $SCRIPT_PATH"
  # Remove GA auth header in case of CI (workaround): https://github.community/t/how-to-push-to-protected-branches-in-a-github-action/16101/47
  doRunCommand "git -c "http.https://github.com/.extraheader=" push origin master"
  doRunCommand "git -c "http.https://github.com/.extraheader=" push origin v$RELEASE_VERSION"
  doRunCommand "mvn nexus-staging:release $MVN_SETTINGS -f cobigen $DEBUG $BATCH_MODE"
  doRunCommand "mvn nexus-staging:release $MVN_SETTINGS -f cobigen-plugins $DEBUG $BATCH_MODE"
  doRunCommand "mvn nexus-staging:release $MVN_SETTINGS -f cobigen-cli $DEBUG $BATCH_MODE"
  doRunCommand "mvn nexus-staging:release $MVN_SETTINGS -f cobigen-maven $DEBUG $BATCH_MODE"
  doRunCommand "mvn nexus-staging:release $MVN_SETTINGS -f cobigen-templates $DEBUG $BATCH_MODE"
fi
