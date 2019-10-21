@echo off
REM echo "Current Java Version is"
REM echo %JAVA_HOME%
setlocal enableDelayedExpansion

IF "%*"=="" (
    java -jar %SOFTWARE_PATH%\cobigen-cli\cobigen.jar --help
) ELSE (
    java -javaagent:%SOFTWARE_PATH%\cobigen-cli\class-loader-agent.jar -jar %SOFTWARE_PATH%\cobigen-cli\cobigen.jar %*
)

EXIT /B
