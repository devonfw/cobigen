@echo off
REM echo "Current Java Version is"
REM echo %JAVA_HOME%
setlocal enableDelayedExpansion

set JAVA_HOME=%JAVA_HOME%;
set CLASSPATH=.;%JAVA_HOME%;

java -jar cobigen.jar %*

EXIT /B






