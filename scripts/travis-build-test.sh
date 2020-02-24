# Activate error population
set -e

source ./travis-functions.sh

ROOT = ""

if [ $TRAVIS_BRANCH == "master" ]; then 
  if [ justOneFolderChanged "cobigen-templates/" ]; then
    echo "Just Templates changed!"
    ROOT = "cobigen-templates"
  else
    ROOT = ""
  fi
} else if ($TRAVIS_BRANCH == "dev_eclipseplugin") {
  ROOT = "cobigen-eclipse"
} else if ($TRAVIS_BRANCH == "dev_htmlmerger") {
  ROOT = "cobigen/cobigen-htmlplugin"
} else if ($TRAVIS_BRANCH == "dev_mavenplugin") {
  ROOT = "cobigen-maven"
} else if ($TRAVIS_BRANCH == "dev_tempeng_freemarker") {
  ROOT = "cobigen/cobigen-templateengines/cobigen-tempeng-freemarker"
} else if ($TRAVIS_BRANCH == "dev_tempeng_velocity") {
  ROOT = "cobigen/cobigen-templateengines/cobigen-tempeng-velocity"
} else if ($TRAVIS_BRANCH == "dev_core") {
  ROOT = "cobigen/cobigen-core-parent"
} else if ($TRAVIS_BRANCH == "dev_javaplugin") {
  ROOT = "cobigen/cobigen-javaplugin-parent"
} else if ($TRAVIS_BRANCH == "dev_openapiplugin") {
  ROOT = "cobigen/cobigen-openapiplugin-parent"
} else {
  ROOT = "cobigen/cobigen-" + ${TRAVIS_BRANCH/dev_/}
fi

# -s ${MAVEN_SETTINGS} 
if [ $TRAVIS_BRANCH == 'master' ]; then
  mvn clean install -U -Pp2-build-photon,p2-build-stable
elif [ $TRAVIS_BRANCH == 'dev_eclipseplugin' ]; then
  mvn clean package -U -Pp2-build-photon,p2-build-ci
else
  mvn clean install -U
fi
