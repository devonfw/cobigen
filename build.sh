
#buildscript
# build core
mvn clean install -f cobigen
# build plugins
mvn clean install p2:site -f cobigen-plugins bundle:bundle -Pp2-bundle --projects !cobigen-javaplugin-parent/cobigen-javaplugin-model,!cobigen-openapiplugin-parent/cobigen-openapiplugin-model,!:parent,!cobigen-javaplugin-parent,!cobigen-openapiplugin-parent,!cobigen-templateengines
# package everything. core and plugins will be skiped since they were build already
mvn package -Pp2-build-photon,p2-build-stable,p2-build-experimental
