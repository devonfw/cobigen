@echo off
REM echo "Current Java Version is"
REM echo %JAVA_HOME%
setlocal enableDelayedExpansion

IF "%*"=="" (
    java -jar C:\MyData\IDE4\workspaces\cobigen-development\master\cobigen-cli\src\main\java\cobigen.jar --help
) ELSE (
    java -jar C:\MyData\IDE4\workspaces\cobigen-development\master\cobigen-cli\src\main\java\cobigen.jar %*
)

EXIT /B






