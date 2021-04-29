set -e

# build core without system test
mvn clean install -f cobigen --projects !cobigen-core-systemtest -DtrimStackTrace=false

# build & test plugins ones
mvn clean install -f cobigen-plugins -DtrimStackTrace=false

# build plugin p2 repositories
mvn clean install -DskipTests p2:site -f cobigen-plugins bundle:bundle -Pp2-bundle --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines -DtrimStackTrace=false

# execute core system tests
mvn clean verify -f cobigen --projects cobigen-core-systemtest -DtrimStackTrace=false

# package everything. core and plugins will be skipped since they were build already
mvn package -DskipTests -Pp2-build-photon,p2-build-stable,p2-build-experimental -DtrimStackTrace=false
