@echo off
REM echo "Current Java Version is"
REM echo %JAVA_HOME%
setlocal enableDelayedExpansion

set JAVA_HOME=%JAVA_HOME%;
REM echo setting PATH
REM setx PATH %JAVA_HOME%\bin;%PATH% -m
REM echo Display java version
REM java -version
set CLASSPATH=.;%JAVA_HOME%;C:\Program Files\Java\jdk1.8.0_181\lib\dt.jar;C:\MyData\IDE4\workspaces\com.cobigen.picocli\picocli-2.2.1.jar;C:\Users\syadav9\.m2\repository\com\devonfw\cobigen\openapiplugin\2.2.0\openapiplugin-2.2.0.jar

java -jar cobigen.jar %*

EXIT /B






