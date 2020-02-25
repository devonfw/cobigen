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
