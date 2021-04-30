set -e

echo "##########################################"
echo "### Cleanup Projects #####################"
echo "##########################################"
mvn clean -P!p2-build -T1C --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

echo "##########################################"
echo "### Build & Test Core  ###################"
echo "##########################################"
mvn install -f cobigen --projects !cobigen-core-systemtest -DtrimStackTrace=false -T1C --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

echo "##########################################"
echo "### Build & Test Core Plugins ############"
echo "##########################################"
mvn install -f cobigen-plugins -DtrimStackTrace=false -T1C --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

echo "##########################################"
echo "### Build Core Plugins - P2 Update Sites #"
echo "##########################################"
mvn package -DskipTests -f cobigen-plugins bundle:bundle -Pp2-bundle --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines -DtrimStackTrace=false -T1C --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
mvn install -DskipTests -f cobigen-plugins bundle:bundle -Pp2-bundle p2:site --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines -DtrimStackTrace=false -T1C --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

echo "##########################################"
echo "### Package & Run E2E Tests ##############"
echo "##########################################"
mvn test -f cobigen/cobigen-core-systemtest -DtrimStackTrace=false --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
mvn verify -f cobigen-eclipse -DtrimStackTrace=false --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
mvn verify -f cobigen-cli -DtrimStackTrace=false --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
mvn verify -f cobigen-maven -DtrimStackTrace=false --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

