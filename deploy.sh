set -e

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
echo ""
echo "##########################################"

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
DEPLOY_SIGN="-Poss -Dgpg.keyname=maybeec@users.noreply.github.com -Dgpg.executable=gpg"
DEPLOY_UPDATESITE="test"

log_step "Cleanup Projects"
mvn clean -P!p2-build $PARALLELIZED $BATCH_MODE

log_step "Build & Test Core"
mvn deploy -f cobigen --projects !cobigen-core-systemtest -P!p2-build $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE $DEPLOY_SIGN

log_step "Build & Test Core Plugins"
mvn deploy -f cobigen-plugins -P!p2-build $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE $DEPLOY_SIGN

log_step "Build Core Plugins - P2 Update Sites"
mvn package bundle:bundle -Pp2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE 
mvn install bundle:bundle -Pp2-bundle -DskipTests p2:site -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE 
mvn deploy -Pp2-bundle -DskipTests -f cobigen-plugins --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE 

log_step "Package & Run E2E Tests"
mvn test -f cobigen/cobigen-core-systemtest -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE
mvn deploy -f cobigen-cli -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN
mvn deploy -f cobigen-maven -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN
mvn deploy -f cobigen-templates -P!p2-build $ENABLED_TEST $DEBUG $BATCH_MODE $DEPLOY_SIGN
mvn install -f cobigen-eclipse $ENABLED_TEST $DEBUG $BATCH_MODE
mvn deploy -f cobigen-eclipse -DskipTests $DEBUG $BATCH_MODE -Dupdatesite.repository=$DEPLOY_UPDATESITE --projects cobigen-eclipse-updatesite
