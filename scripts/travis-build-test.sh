# Activate error population
set -e

source ./scripts/travis-functions.sh

ROOT=""

if [[ $TRAVIS_BRANCH == "master" ]]; then 
  if justOneFolderChanged "cobigen-templates/"; then
    echo "Just Templates changed!"
    ROOT="cobigen-templates"
  else
    ROOT=""
  fi
elif [[ $TRAVIS_BRANCH == "dev_eclipseplugin" ]]; then
  ROOT="cobigen-eclipse"
elif [[ $TRAVIS_BRANCH == "dev_htmlmerger" ]]; then
  ROOT="cobigen/cobigen-htmlplugin"
elif [[ $TRAVIS_BRANCH == "dev_mavenplugin" ]]; then
  ROOT="cobigen-maven"
elif [[ $TRAVIS_BRANCH == "dev_tempeng_freemarker" ]]; then
  ROOT="cobigen/cobigen-templateengines/cobigen-tempeng-freemarker"
elif [[ $TRAVIS_BRANCH == "dev_tempeng_velocity" ]]; then
  ROOT="cobigen/cobigen-templateengines/cobigen-tempeng-velocity"
elif [[ $TRAVIS_BRANCH == "dev_core" ]]; then
  ROOT="cobigen/cobigen-core-parent"
elif [[ $TRAVIS_BRANCH == "dev_javaplugin" ]]; then
  ROOT="cobigen/cobigen-javaplugin-parent"
elif [[ $TRAVIS_BRANCH == "dev_openapiplugin" ]]; then
  ROOT="cobigen/cobigen-openapiplugin-parent"
else
  ROOT="cobigen/cobigen-${TRAVIS_BRANCH/dev_/}"
fi
echo "Execute build in $ROOT"

# -s ${MAVEN_SETTINGS} 
if [ $TRAVIS_BRANCH == 'master' ]; then
  mvn clean install -U -Pp2-build-photon,p2-build-stable
elif [ $TRAVIS_BRANCH == 'dev_eclipseplugin' ]; then
  mvn clean package -U -Pp2-build-photon,p2-build-ci
else
  mvn clean install -U
fi
