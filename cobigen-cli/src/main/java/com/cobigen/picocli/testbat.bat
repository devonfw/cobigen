ECHO OFF
set path=C:\Program Files\Java\jdk1.8.0_181\bin
set CLASSPATH=.;C:\Program Files\Java\jdk1.8.0_181;C:\Program Files\Java\jdk1.8.0_181\lib\dt.jar;C:\MyData\IDE4\workspaces\com.cobigen.picocli\picocli-2.2.1.jar

set arg1=%1
set arg2=%2
javac TestPicocli.java
echo %arg1%
echo %arg2%

java  com.cobigen.picocli.TestPicocli  %arg1% %arg2%
pause


