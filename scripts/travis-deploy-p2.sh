# Activate error population
set -e

source ./scripts/travis-functions.sh

MAVEN_SETTINGS=$(pwd)/.mvn/settings.xml

navigateToBuildRoot

if [ "$TRAVIS_BRANCH" != "dev_eclipseplugin" ]; then
  if [ "$TRAVIS_BRANCH" == "dev_javaplugin" ]; then
    cd cobigen-javaplugin
  fi
  if [ "$TRAVIS_BRANCH" == "dev_openapiplugin" ]; then
    cd cobigen-openapiplugin
  fi
  
  # deploy maven module as eclipse bundle.
  # we currently need these three steps to assure the correct sequence of packaging,
  # manifest extension, osgi bundling, and upload
  mvn -s ${MAVEN_SETTINGS} package -e -U bundle:bundle -Pp2-bundle,p2-build-photon,p2-build-ci -Dmaven.test.skip=true
  mvn -s ${MAVEN_SETTINGS} install -e -U bundle:bundle -Pp2-bundle,p2-build-photon,p2-build-ci p2:site -Dmaven.test.skip=true
  mvn -s ${MAVEN_SETTINGS} deploy -e -U -Pp2-build-photon,p2-build-ci -Dmaven.test.skip=true -Dbintray.repository=cobigen.p2.ci

elif [ "$TRAVIS_BRANCH" == "dev_eclipseplugin" ]; then
  mvn -s ${MAVEN_SETTINGS} deploy -e -U -Dmaven.test.skip=true -Pp2-build-photon,p2-build-ci -Dbintray.repository=cobigen.p2.ci
fi
