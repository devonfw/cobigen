ECHO ON
echo "Current Java Version is"
echo %JAVA_HOME%
setlocal enableDelayedExpansion

set JAVA_HOME=%JAVA_HOME%;
REM echo setting PATH
REM setx PATH %JAVA_HOME%\bin;%PATH% -m
REM echo Display java version
REM java -version
set CLASSPATH=.;%JAVA_HOME%;C:\Program Files\Java\jdk1.8.0_181\lib\dt.jar;C:\MyData\IDE4\workspaces\com.cobigen.picocli\picocli-2.2.1.jar;C:\Users\syadav9\.m2\repository\com\devonfw\cobigen\openapiplugin\2.2.0\openapiplugin-2.2.0.jar

ECHO "Welcome to CobiGen." 
ECHO "The Code-based incemental Generator for end to end code generation tasks, mostly used in Java projects."
ECHO "Available Commands:"
ECHO "cg generate (g)"
ECHO "cg update"
ECHO "cg check"
ECHO "cg revert"
ECHO "with [-h] you can get more infos about the commands you want to use or the increment you want to generate"

    
set arg0=%0
set arg1=%1
set arg2=%2 

set /p inputFile= $cg g %arg1% %arg2%

java -jar cobigen.jar %inputFile% %pathOfProject%

pause






