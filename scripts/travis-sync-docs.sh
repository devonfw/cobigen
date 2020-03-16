# Activate error population
set -e

source ./scripts/travis-functions.sh

if ! folderChanged "documentation/"; then
  echo "Nothing to synchronize. Closing..."
fi

# Clone repositories
# travis will automatically already clone REPO_SOURCE into devonfw/cobigen and navigate to this folder. So we go one up again.
cd ..
git clone https://${GH_REPO_DEST}
git clone https://${GH_REPO_CONSOLIDATE}

# Update wiki repository with documentation folder contents
yes | cp -rf ${REPO_SOURCE}/documentation/* ${REPO_DEST}/
cd ${REPO_DEST}
grep -lr "link:[a-zA-Z0-9_.-]*.asciidoc.*" .| xargs -r sed -i "s/.asciidoc//g"

# Terminate Travis CI build when no changes detected
if [ ! -n "$(git status -s)" ]; then 
  set +e
  echo "Nothing to synchronize. Closing..."
  pkill -9 -P $$ &> /dev/null || true 
  exit 0
else 
  git config user.email ${EMAIL}
  git config user.name ${USER}
  git status
  git add .
  git commit -m "${REPO_SOURCE} documentation | Travis CI build number $TRAVIS_BUILD_NUMBER"
  git remote add origin-wiki "https://${GITHUB_TOKEN}@${GH_REPO_DEST}"
  git push origin-wiki master
  cd ../${REPO_CONSOLIDATE}
  if [ ! -d ${REPO_DEST} ]; then git submodule add https://${GH_REPO_DEST}; fi;
  git submodule init
  git submodule update --recursive --remote
  cd ${REPO_DEST}
  git checkout master
  git pull
  cd ..
  git add .
  git commit -m "${REPO_SOURCE} documentation | Travis CI build number $TRAVIS_BUILD_NUMBER"
  git remote add origin-wiki "https://${GITHUB_TOKEN}@${GH_REPO_CONSOLIDATE}"
  git push origin-wiki master
fi