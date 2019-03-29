ECHO OFF
set path=C:\Program Files\Java\jdk1.8.0_181\bin
set CLASSPATH=.;C:\Program Files\Java\jdk1.8.0_181;C:\Program Files\Java\jdk1.8.0_181\lib\dt.jar;C:\MyData\IDE4\workspaces\com.cobigen.picocli\picocli-2.2.1.jar;C:\Users\syadav9\.m2\repository\com\devonfw\cobigen\openapiplugin\2.2.0\openapiplugin-2.2.0.jar
ECHO "Welcome to cobigen and below are increment available for your input" 

    
set arg0=%0
set arg1=%1
set arg2=%2 
javac TestPicocli.java
set /p answer= $cg -g increment=%arg1%

java -jar C:\MyData\IDE4\workspaces\com.cobigen.picocli\src\main\java\cobigen.jar 

pause






