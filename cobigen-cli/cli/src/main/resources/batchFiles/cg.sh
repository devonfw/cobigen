#!/bin/bash

if [ -z "$*" ]; then
    java -jar $SOFTWARE_PATH/cobigen-cli/cobigen.jar -help
else
    java -javaagent:$SOFTWARE_PATH/cobigen-cli/class-loader-agent.jar -jar $SOFTWARE_PATH/cobigen-cli/cobigen.jar $*
fi