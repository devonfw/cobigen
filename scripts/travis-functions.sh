function justOneFolderChanged() {
	# split will return a list with one element (the empty string) if called on an empty string
	DIFF_FILES="$(git diff --name-only origin/master | xargs)"
	while IFS=$' \t\n' read -ra DIFF_FILES; do
    for i in "${ADDR[@]}"; do
      if [[ $i != $1* ]]; then
        echo "'$i' does not start with /$1"
        return false
      fi
    done
	done
	return true
}

function folderChanged() {
	# split will return a list with one element (the empty string) if called on an empty string
	DIFF_FILES="$(git diff --name-only origin/master | xargs)"
	while IFS=$' \t\n' read -ra DIFF_FILES; do
    for i in "${ADDR[@]}"; do
      if [[ $i == $1* ]]; then
        echo "'$i' has been changed within /$1"
        return true
      fi
    done
	done
	return false
}