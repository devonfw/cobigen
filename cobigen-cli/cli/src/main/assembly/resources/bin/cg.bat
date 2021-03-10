@echo off
REM echo "Current Java home is"
REM echo %JAVA_HOME%
setlocal enableDelayedExpansion

IF [%1]==[] (
    java -jar "%~dp0\..\lib\cobigen.jar" --help
) ELSE (
    java -javaagent:"%~dp0\..\lib\class-loader-agent.jar" -jar "%~dp0\..\lib\cobigen.jar" %*
)

EXIT /B
