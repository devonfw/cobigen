function justOneFolderChanged { args : string folderName } {
	# split will return a list with one element (the empty string) if called on an empty string
	DIFF_FILES="$(git diff --name-only origin/master | xargs)"
	while IFS=$' \t\n' read -ra DIFF_FILES; do
    for i in "${ADDR[@]}"; do
      if [[ $i != $folderName* ]]; then
        echo "'$i' does not start with /$folderName"
        return false
      fi
    done
	done
	return true
}

function folderChanged { args : string folderName } {
	# split will return a list with one element (the empty string) if called on an empty string
	DIFF_FILES="$(git diff --name-only origin/master | xargs)"
	while IFS=$' \t\n' read -ra DIFF_FILES; do
    for i in "${ADDR[@]}"; do
      if [[ $i == $folderName* ]]; then
        echo "'$i' has been changed within /$folderName"
        return true
      fi
    done
	done
	return false
}