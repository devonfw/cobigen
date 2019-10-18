#!/bin/bash

if [ "$*"=="" ]; then
    java -jar $SOFTWARE_PATH/cobigen-cli/cobigen.jar -help
else
    java -jar $SOFTWARE_PATH/cobigen-cli/cobigen.jar $*
fi