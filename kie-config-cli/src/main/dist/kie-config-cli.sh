#!/bin/bash

#set jvm and java parameters if any
JAVA_PARAMS=""
JVM_PARAMS="-Xmx256m -XX:MaxPermSize=128m"
CLASSPATH=""

#collect classpath entries from lib folder
for file in lib/*
do
  CLASSPATH=$file:$CLASSPATH
done

#add the kie-config-cli jar to classpath
MAIN_JAR=${artifactId}-${version}.jar

CLASSPATH=$CLASSPATH$MAIN_JAR

#run the program
java $JVM_PARAMS $JAVA_PARAMS -cp $CLASSPATH org.kie.config.cli.CmdMain "$@"
