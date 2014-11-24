#!/bin/sh

CATALINA_OPTS="-Xmx512M -XX:MaxPermSize=512m -Dbtm.root=$CATALINA_HOME \
-Dbitronix.tm.configuration=$CATALINA_HOME/conf/btm-config.properties \
-Djbpm.tsr.jndi.lookup=java:comp/env/TransactionSynchronizationRegistry"
