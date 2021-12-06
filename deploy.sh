#!/usr/bin/env bash
source "$(dirname "${0}")"/functions.sh

if [[ $(sed -r -E -n 's@<revision>([^<]+)-SNAPSHOT</revision>@\1@p' pom.xml) ]]
then
  DEPLOY_UPDATESITE="test"
  echo "  * Detected snapshot release number. Releasing to test p2 repository"
else
  DEPLOY_UPDATESITE="stable"
  echo -e "\e[92m  > Detected final release number. Releasing to stable p2 repository\e[39m"
fi

echo ""
echo "##########################################"
echo ""
echo "Checking preconditions:"

# check preconditions
if [ "$DEPLOY_UPDATESITE" = "stable" ] && [ -d "../gh-pages" ]
then
  cd ../gh-pages
  if [[ $(git diff --shortstat && git status --porcelain) ]]
  then
    echo " * ../gh-pages is prepared"
  else
    echo " ! ../gh-pages is not clean"
    doAskQuestion "Should I cleanup?" # will exit if no
    gitCleanup
  fi
  cd "$SCRIPT_PATH"
else
  echo " ! Not detected cloned gh-pages branch in ../gh-pages folder."
  ORIGIN="$(git config --get remote.origin.url)"
  case "$ORIGIN" in
    *devonfw/cobigen*) doAskQuestion "Should I clone gh-pages from $ORIGIN" && echo "Cloning from $ORIGIN into ../gh-pages ..." && doRunCommand "git clone --branch gh-pages $ORIGIN ../gh-pages" ;;
    *) echo "You are working on a fork, please make sure, you are releasing from devonfw/cobigen#master" && exit 1 ;;
  esac
fi
echo ""
echo "##########################################"
echo ""

log_step "Cleanup Projects"
doRunCommand "mvn clean $PARALLELIZED $BATCH_MODE"

log_step "Build & Test Core"
# need to exclude cobigen-core-systemtest as of https://issues.sonatype.org/browse/NEXUS-19853 for deployment only!
doRunCommand "mvn deploy -s .mvn/settings.xml -f cobigen --projects !cobigen-core-systemtest $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE $DEPLOY_SIGN"

log_step "Build & Test Core Plugins"
doRunCommand "mvn deploy -s .mvn/settings.xml -f cobigen-plugins $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE $DEPLOY_SIGN"

log_step "Build Core Plugins - P2 Update Sites"
doRunCommand "mvn package bundle:bundle -Pp2-build,p2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE "
doRunCommand "mvn install bundle:bundle -Pp2-build,p2-bundle -DskipTests p2:site -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE "
doRunCommand "mvn deploy -s .mvn/settings.xml -Pp2-build,p2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE"

log_step "Package & Run E2E Tests"
doRunCommand "mvn test -f cobigen/cobigen-core-systemtest $ENABLED_TEST $DEBUG $BATCH_MODE"
# need to exclude cli-systemtest as of https://issues.sonatype.org/browse/NEXUS-19853 for deployment only!
doRunCommand "mvn deploy -s .mvn/settings.xml -f cobigen-cli --projects !cli-systemtest $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN"
# need to exclude cobigen-maven-systemtest as of https://issues.sonatype.org/browse/NEXUS-19853 for deployment only!
doRunCommand "mvn deploy -s .mvn/settings.xml -f cobigen-maven --projects !cobigen-maven-systemtest $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN"
doRunCommand "mvn deploy -s .mvn/settings.xml -f cobigen-templates $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN"
doRunCommand "mvn deploy -s .mvn/settings.xml -f cobigen-eclipse -Pp2-build -DskipTests $ENABLED_TEST $DEBUG $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE"
