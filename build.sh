set -e

echo ""
echo "##########################################"
echo ""
echo "Script config: "
if [[ "$*" == *test* ]]
then
    ENABLED_TEST="-Dmaven.test.skip=true"
    echo "  * With test execution"
else
    ENABLED_TEST="-Dmaven.test.skip=false -DskipTests" # need to declare both
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
    # the latter will remove maven download logs / might cause https://stackoverflow.com/a/66801171 issues
    DEBUG="-DtrimStackTrace=false -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn" # set to false to see hidden exceptions
    echo "  * Debug On"
else
    DEBUG=""
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

log_step "Cleanup Projects"
mvn clean -P!p2-build $PARALLELIZED $BATCH_MODE

log_step "Build & Test Core"
mvn install -f cobigen --projects !cobigen-core-systemtest $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE

log_step "Build & Test Core Plugins"
mvn install -f cobigen-plugins $ENABLED_TEST $DEBUG $PARALLELIZED $BATCH_MODE

log_step "Build Core Plugins - P2 Update Sites"
mvn package -DskipTests -f cobigen-plugins bundle:bundle -Pp2-bundle --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE
mvn p2:site install -DskipTests -f cobigen-plugins -Pp2-bundle --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE

log_step "Package & Run E2E Tests"
mvn test -f cobigen/cobigen-core-systemtest $ENABLED_TEST $DEBUG $BATCH_MODE
mvn install -f cobigen-templates $ENABLED_TEST $DEBUG $BATCH_MODE
mvn install -f cobigen-cli $ENABLED_TEST $DEBUG $BATCH_MODE
mvn install -f cobigen-maven $ENABLED_TEST $DEBUG $BATCH_MODE
mvn install -f cobigen-eclipse $ENABLED_TEST $DEBUG $BATCH_MODE -Dtycho.debug.resolver=true
