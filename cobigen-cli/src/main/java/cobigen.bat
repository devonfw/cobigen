ECHO OFF
echo "Current Java Version is"
echo %JAVA_HOME%
setlocal enableDelayedExpansion
SET PT="C:\Program Files\Java"
cd %PT%
::build "array" of folders
set folderCnt=0
for /f "eol=: delims=" %%F in ('dir /b /ad *') do (
  set /a folderCnt+=1
  set "folder!folderCnt!=%%F"
)

::print menu
for /l %%N in (1 1 %folderCnt%) do echo %%N - !folder%%N!
echo(
 
:get selection
set selection=
set /p "selection=Enter number to Set the JAVA_HOME and PATH "
echo you picked %selection% - !folder%selection%!
cd %PT%\!folder%selection%!
echo %cd%
( endlocal & rem return
   Set jdk=%cd%
 
)
echo Setting JAVA_HOME
SETX /M JAVA_HOME "%jdk%"
SET JAVA_HOME=%jdk%
echo %JAVA_HOME%
set path=%JAVA_HOME%\bin;
REM echo setting PATH
REM setx PATH %JAVA_HOME%\bin;%PATH% -m
REM echo Display java version
REM java -version
set CLASSPATH=.;C:\Program Files\Java\jdk1.8.0_181;C:\Program Files\Java\jdk1.8.0_181\lib\dt.jar;C:\MyData\IDE4\workspaces\com.cobigen.picocli\picocli-2.2.1.jar;C:\Users\syadav9\.m2\repository\com\devonfw\cobigen\openapiplugin\2.2.0\openapiplugin-2.2.0.jar
ECHO "Welcome to cobigen and below are increment available for your input" 

    
set arg0=%0
set arg1=%1
set arg2=%2 
javac TestPicocli.java
set /p answer= $cg -g increment=%arg1%

java -jar C:\MyData\IDE4\workspaces\com.cobigen.picocli\src\main\java\cobigen.jar 

pause






