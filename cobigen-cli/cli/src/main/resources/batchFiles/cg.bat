@echo off
REM echo "Current Java Version is"
REM echo %JAVA_HOME%
setlocal enableDelayedExpansion

IF [%1]==[] (
    java -jar "%~dp0\cobigen.jar" --help
) ELSE (
    java -javaagent:"%~dp0\class-loader-agent.jar" -jar "%~dp0\cobigen.jar" %*
)

EXIT /B
