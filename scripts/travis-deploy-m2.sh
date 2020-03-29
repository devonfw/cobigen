# Activate error population
set -e

source ./scripts/travis-functions.sh

MAVEN_SETTINGS=$(pwd)/.mvn/settings.xml

navigateToBuildRoot

mvn -s ${MAVEN_SETTINGS} deploy -e -U -Dmaven.test.skip=true
