#!/usr/bin/env bash

release_script="config_test.py"
fork_path="/Users/mghanmi/Desktop/Capgemini/CobiGen_IDE/workspaces/cobigen-development/dev_openapiplugin"

python3 $release_script -d -g devonfw/tools-cobigen -r $fork_path -c
