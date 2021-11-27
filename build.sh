#!/usr/bin/env bash
source "$(dirname "${0}")"/functions.sh
echo ""
echo "##########################################"
echo ""

log_step "Cleanup Projects"
doRunCommand "mvn clean $PARALLELIZED $BATCH_MODE $DEBUG"

log_step "Build & Test Core"
doRunCommand "mvn install -f cobigen --projects !cobigen-core-systemtest $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE"

log_step "Build & Test Core Plugins"
doRunCommand "mvn install -f cobigen-plugins $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE"

log_step "Build Core Plugins - P2 Update Sites"
doRunCommand "mvn package bundle:bundle -Pp2-build,p2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE"
doRunCommand "mvn install bundle:bundle -Pp2-build,p2-bundle -DskipTests p2:site -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE"

log_step "Package & Run E2E Tests"
doRunCommand "mvn test -f cobigen/cobigen-core-systemtest $ENABLED_TEST $DEBUG $BATCH_MODE"
doRunCommand "mvn install -f cobigen-cli $ENABLED_TEST $DEBUG $BATCH_MODE"
doRunCommand "mvn install -f cobigen-maven $ENABLED_TEST $DEBUG $BATCH_MODE"
doRunCommand "mvn install -f cobigen-templates $ENABLED_TEST $DEBUG $BATCH_MODE"
doRunCommand "mvn install -f cobigen-eclipse -Pp2-build $ENABLED_TEST $DEBUG $BATCH_MODE"

