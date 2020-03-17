# Activate error population
set -e

source ./scripts/travis-functions.sh

navigateToBuildRoot

if [[ $TRAVIS_BRANCH == 'master' ]]; then
  mvn clean install -U -Pp2-build-photon,p2-build-stable
elif [[ $TRAVIS_BRANCH == 'dev_eclipseplugin' ]]; then
  mvn clean install -U -Pp2-build-photon,p2-build-stable,p2-build-ci
else
  mvn clean install -U
fi
