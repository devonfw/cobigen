# Activate error population
set -e

source ./scripts/travis-functions.sh

MAVEN_SETTINGS=$(pwd)/.mvn/settings.xml

if ! folderChanged "documentation/"; then
  echo "Nothing new to build. Closing..."
fi

cd documentation

mvn -s ${MAVEN_SETTINGS} clean package -U