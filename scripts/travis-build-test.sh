# Activate error population
set -e

source ./scripts/travis-functions.sh

MAVEN_SETTINGS=$(pwd)/.mvn/settings.xml

navigateToBuildRoot

if [[ $TRAVIS_BRANCH == 'master' ]]; then
  mvn -s ${MAVEN_SETTINGS} clean install -U -Pp2-build-photon,p2-build-stable
elif [[ $TRAVIS_BRANCH == 'dev_eclipseplugin' ]]; then
  mvn -s ${MAVEN_SETTINGS} clean install -U -Pp2-build-photon,p2-build-stable,p2-build-ci
else
  mvn -s ${MAVEN_SETTINGS} clean install -U
fi
