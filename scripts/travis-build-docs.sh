# Activate error population
set -e

source ./scripts/travis-functions.sh

if ! folderChanged "documentation/"; then
  echo "Nothing new to build. Closing..."
fi

cd documentation

mvn -s $(pwd)/.mvn/settings.xml clean package -U