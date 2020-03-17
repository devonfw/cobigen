function justOneFolderChanged() {
	# split will return a list with one element (the empty string) if called on an empty string
	DIFF_FILES="$(git diff --name-only origin/master | xargs)"
  IFS=$'\n'
  for i in $DIFF_FILES; do
    if [[ $i != $1* ]]; then
      echo "'$i' does not start with /$1"
      return 1
    fi
  done
	return 0
}

function folderChanged() {
	# split will return a list with one element (the empty string) if called on an empty string
	DIFF_FILES="$(git diff --name-only origin/master | xargs)"
	IFS=$'\n'
  for i in $DIFF_FILES; do
    if [[ $i == $1* ]]; then
      echo "'$i' has been changed within /$1"
      return 0
    fi
  done
	return 1
}

function navigateToBuildRoot() {
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

  if [ -z "$ROOT" ]; then
    echo "Execute build in root directory of the repository"
  else
    echo "Execute build in $ROOT"
    cd $ROOT
  fi
}