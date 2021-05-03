set -e

DEBUG=-DtrimStackTrace=false # set to false to see hidden exceptions
PARALLELIZED=-T1C
BATCH_MODE=-B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn # the latter will remove maven download logs

echo "##########################################"
echo "### Cleanup Projects #####################"
echo "##########################################"
mvn clean -P!p2-build $PARALLELIZED $BATCH_MODE

echo "##########################################"
echo "### Build & Test Core  ###################"
echo "##########################################"
mvn install -f cobigen --projects !cobigen-core-systemtest $DEBUG $PARALLELIZED $BATCH_MODE

echo "##########################################"
echo "### Build & Test Core Plugins ############"
echo "##########################################"
mvn install -f cobigen-plugins $DEBUG $PARALLELIZED $BATCH_MODE

echo "##########################################"
echo "### Build Core Plugins - P2 Update Sites #"
echo "##########################################"
mvn package -DskipTests -f cobigen-plugins bundle:bundle -Pp2-bundle --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE
mvn install -DskipTests -f cobigen-plugins bundle:bundle -Pp2-bundle p2:site --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines $DEBUG $PARALLELIZED $BATCH_MODE

echo "##########################################"
echo "### Package & Run E2E Tests ##############"
echo "##########################################"
mvn test -f cobigen/cobigen-core-systemtest $DEBUG $BATCH_MODE
mvn verify -f cobigen-eclipse $DEBUG $BATCH_MODE -Dtycho.debug.resolver=true
mvn verify -f cobigen-cli $DEBUG $BATCH_MODE
mvn verify -f cobigen-maven $DEBUG $BATCH_MODE

