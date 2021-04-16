set -e

# build core without system test
mvn clean install -f cobigen --projects !cobigen-core-systemtest

# build plugins
mvn clean install p2:site -f cobigen-plugins bundle:bundle -Pp2-bundle --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:plugins-parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines

# execute core system tests
mvn clean verify -f cobigen --projects cobigen-core-systemtest

# package everything. core and plugins will be skipped since they were build already
mvn package -Pp2-build-photon,p2-build-stable,p2-build-experimental
