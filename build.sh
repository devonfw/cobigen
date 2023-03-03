#!/usr/bin/env bash
source "$(dirname "${0}")"/functions.sh

echo ""
echo "##########################################"
echo ""

if [[ "$NO_CLEAN" = false ]]
then
  log_step "Cleanup Projects"
  doRunCommand "mvn clean $MVN_SETTINGS $PARALLELIZED $BATCH_MODE $DEBUG"
fi

if [[ " ${COMPONENTS_TO_BUILD[*]} " =~ " core " ]]; then
  log_step "Build & Test Core"
  doRunCommand "mvn install $MVN_SETTINGS -f cobigen --projects !cobigen-core-systemtest $COVERAGE $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE"
fi

if [[ " ${COMPONENTS_TO_BUILD[*]} " =~ " plugins " ]]; then
  log_step "Build & Test Core Plugins"
  doRunCommand "mvn install $MVN_SETTINGS -f cobigen-plugins $COVERAGE $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE"

  log_step "Build Core Plugins - P2 Update Sites"
  doRunCommand "mvn package $MVN_SETTINGS bundle:bundle -Pp2-build,p2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE"
  doRunCommand "mvn install $MVN_SETTINGS bundle:bundle -Pp2-build,p2-bundle -DskipTests p2:site -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE"
fi

log_step "Package & Run E2E Tests"
if [[ " ${COMPONENTS_TO_BUILD[*]} " =~ " core " ]]; then
  doRunCommand "mvn integration-test $MVN_SETTINGS -f cobigen/cobigen-core-systemtest $COVERAGE $ENABLED_TEST $DEBUG $BATCH_MODE"
fi
if [[ " ${COMPONENTS_TO_BUILD[*]} " =~ " cli " ]]; then
  doRunCommand "mvn install $MVN_SETTINGS -f cobigen-cli $COVERAGE $ENABLED_TEST $DEBUG $BATCH_MODE"
fi
if [[ " ${COMPONENTS_TO_BUILD[*]} " =~ " maven " ]]; then
  doRunCommand "mvn install $MVN_SETTINGS -f cobigen-maven $COVERAGE $ENABLED_TEST $DEBUG $BATCH_MODE"
fi
if [[ " ${COMPONENTS_TO_BUILD[*]} " =~ " templates " ]]; then
  doRunCommand "mvn install $MVN_SETTINGS -f cobigen-templates $COVERAGE $ENABLED_TEST $DEBUG $BATCH_MODE"
fi
if [[ " ${COMPONENTS_TO_BUILD[*]} " =~ " eclipse " ]]; then
  doRunCommand "mvn install $MVN_SETTINGS -f cobigen-eclipse -Pp2-build $COVERAGE $ENABLED_TEST $DEBUG $BATCH_MODE"
fi

if [[ "$COV_REPORT" = true ]]
then
  doRunCommand "mvn -DskipTests verify $COVERAGE $DEBUG $PARALLELIZED $BATCH_MODE"
fi
