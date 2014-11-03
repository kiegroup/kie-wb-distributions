Installation notes
==================

1. Set-up environment

    create setenv.sh (or setenv.bat) file inside TOMCAT_HOME/bin and add following:

    CATALINA_OPTS="-Xmx512M -XX:MaxPermSize=512m"

    NOTE: this is an example for unix like systems for Windows $CATALINA_HOME needs to be replaced with windows env variable or absolute path


    *******************************************************************************

2. Configure JEE security for kie-wb on tomcat (with default realm backed by tomcat-users.xml)

   2a. Copy "kie-tomcat-integration" JAR into TOMCAT_HOME/lib (org.kie:kie-tomcat-integration)
   2b. Copy "JACC" JAR into TOMCAT_HOME/lib (javax.security.jacc:artifactId=javax.security.jacc-api in JBoss Maven Repository)
   2c. Copy "slf4j-api" JAR into TOMCAT_HOME/lib (org.slf4j:artifactId=slf4j-api in JBoss Maven Repository)
   2d. Add valve configuration into TOMCAT_HOME/conf/server.xml inside Host element as last valve definition:

      <Valve className="org.kie.integration.tomcat.JACCValve" />

   2e. Edit TOMCAT_HOME/conf/tomcat-users.xml to include roles and users, make sure there will be 'analyst' or 'admin' roles defined as it's required to be authorized to use kie-wb
