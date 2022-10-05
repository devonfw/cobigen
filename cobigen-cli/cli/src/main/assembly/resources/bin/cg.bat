@echo off
REM echo "Current Java home is"
REM echo %JAVA_HOME%
setlocal enableDelayedExpansion

IF [%1]==[] (
    java %CGCLI_JAVA_OPTIONS% -cp "%~dp0\..\lib\*" com.devonfw.cobigen.cli.CobiGenCLI --help
) ELSE (
    java %CGCLI_JAVA_OPTIONS% -cp "%~dp0\..\lib\*" com.devonfw.cobigen.cli.CobiGenCLI %*
)
