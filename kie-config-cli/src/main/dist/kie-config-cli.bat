@echo off

SETLOCAL ENABLEDELAYEDEXPANSION

rem set jvm and java parameters if any
set JAVA_PARAMS=
set JVM_PARAMS=-Xmx256m -XX:MaxPermSize=128m
set CLASSPATH=

rem collect classpath entries from lib folder
for %%f in (.\lib\*) do (
	set CLASSPATH=%%f;!CLASSPATH!
)

rem add the kie-config-cli jar to classpath
set MAIN_JAR=kie-config-cli-6.0.1.Final.jar

set CLASSPATH=%CLASSPATH%;%MAIN_JAR%

rem run the program
java.exe %JVM_PARAMS% %JAVA_PARAMS% -cp %CLASSPATH% org.kie.config.cli.CmdMain %*