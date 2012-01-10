@echo off
setlocal

set JAVA_HOME=C:\"Program Files"\Java\jdk1.6.0_17

if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
goto :eof

:nt
set CLASSPATH=%CLASSPATH%;./data

@echo Getting lib\*.jar
FOR %%f IN (lib\*.jar) DO (call :append_classpath "%%f")

echo CLASSPATH = %CLASSPATH%

%JAVA_HOME%\bin\java -Dfile.encoding=ISO-8859-1 -cp %CLASSPATH% org.jaq.Jaq %1 %2 %3 %4 %5 %6 %7 %8 %9

GOTO :eof

:append_classpath
set CLASSPATH=%CLASSPATH%;%1
