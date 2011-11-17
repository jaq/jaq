@echo off
setlocal
if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
goto :eof

:nt
set CLASSPATH=%CLASSPATH%;./data

@echo Getting lib\*.jar
FOR %%f IN (lib\*.jar) DO (call :append_classpath "%%f")

echo CLASSPATH = %CLASSPATH%

java -Dfile.encoding=ISO-8859-1 -cp %CLASSPATH% org.jaq.SqlExecutor %1 %2 %3 %4 %5 %6 %7 %8 %9

GOTO :eof

:append_classpath
set CLASSPATH=%CLASSPATH%;%1
