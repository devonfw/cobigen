#!/usr/bin/env bash
source "$(dirname "${0}")"/functions.sh

echo ""
echo "##########################################"
echo ""
echo "Checking preconditions:"

# check preconditions
cd "$SCRIPT_PATH"

if [[ $(git diff --shortstat && git status --porcelain) ]]
then
  echo "  * Working copy clean, continuing release"
else
  echo "  !ERR! Working copy not clean. Please make sure everything is committed and pushed."
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
    echo "  !ERR! Detected release revision $SED_OUT. This script is intended to be executed on -SNAPSHOT versions only."
    exit 1
  else
    echo "  !ERR! No revision detected in pom.xml."
    exit 1
  fi
fi

ORIGIN="$(git config --get remote.origin.url)"
case "$ORIGIN" in
  *devonfw/cobigen*) echo "  * Detected clone from $ORIGIN." ;;
  *) echo "  !ERR! You are working on a fork, please make sure, you are releasing from devonfw/cobigen#master" && exit 1 ;;
esac
echo ""
echo "##########################################"
echo ""

log_step "Remove -SNAPSHOT from revision"
doRunCommand "sed -E -i 's@<revision>([^<]+)-SNAPSHOT</revision>@<revision>\1</revision>@' pom.xml"
SED_OUT="$(sed -r -E -n 's@<revision>([0-9]+\.[0-9]+\.[0-9]+)</revision>@\1@p' pom.xml)"
if [[ -z "$SED_OUT" ]]
then
  echo "!ERR! could not set release revision in /pom.xml"
  exit 1
else 
  echo "Set release version to $SED_OUT"
fi

log_step "Build to set revision for p2 artifacts"
doRunCommand "sh ./build.sh parallel $*"

log_step "Commit set release version"
doRunCommand "git add -u"
doRunCommand "git commit 'Set release version'"

log_step "Final test run of new release"
# need to run in extra command for the tests as P2 resolution is done before the maven build aggregator and thus does not reflect the revision changes directly
# also it's not recommended to run the final test in the deploy script as flaky UI tests could end up in a partial release.
# Need to evaluate mvn nexus-staging:release command usage as we are running multiple commands it's not a trivial use and not intended as we would use it
doRunCommand "sh ./build.sh test $*" 

log_step "Deploy Release"
doRunCommand "sh ./deploy.sh $*"

log_step "Create Git Tag"
doRunCommand "git tag v${SED_OUT}"

log_step "Publish Release"
if [ "$DRYRUN" = true ]
then
  echo "[DRYRUN] would push now ../gh-pages to remote"
  doRunCommand "cd ../gh-pages && git status && cd $SCRIPT_PATH"
  exit 1
fi
# doRunCommand "cd ../gh-pages && git push && cd $SCRIPT_PATH"