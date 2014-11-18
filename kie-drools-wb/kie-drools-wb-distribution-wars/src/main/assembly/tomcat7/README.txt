Installation notes
==================


1. Install bitronix transaction manager into your tomcat 7
- copy following libs into TOMCAT_HOME/lib
    * btm-2.1.4.jar
    * btm-tomcat55-lifecycle-2.1.4.jar
    * h2-1.3.161.jar
    * jta-1.1.jar
    * slf4j-api-1.7.2.jar
    * slf4j-jdk14-1.7.2.jar

    NOTE: versions of the libraries can be different as these are the actual on the time of writing.

2. Create configuration files inside TOMCAT_HOME/conf
    * btm-config.properties
    ************************ sample btm-config.properties *************************
         bitronix.tm.serverId=tomcat-btm-node0
         bitronix.tm.journal.disk.logPart1Filename=${btm.root}/work/btm1.tlog
         bitronix.tm.journal.disk.logPart2Filename=${btm.root}/work/btm2.tlog
         bitronix.tm.resource.configuration=${btm.root}/conf/resources.properties

    *******************************************************************************


    * resources.properties
    ************************ sample resources.properties **************************
         resource.ds1.className=bitronix.tm.resource.jdbc.lrc.LrcXADataSource
         resource.ds1.uniqueName=jdbc/jbpm
         resource.ds1.minPoolSize=10
         resource.ds1.maxPoolSize=20
         resource.ds1.driverProperties.driverClassName=org.h2.Driver
         resource.ds1.driverProperties.url=jdbc:h2:file:~/jbpm
         resource.ds1.driverProperties.user=sa
         resource.ds1.driverProperties.password=
         resource.ds1.allowLocalTransactions=true
    *******************************************************************************

    NOTE: jdbc/jbpm is the JNDI name used by tomcat distribution of the application

3. Define btm.root system property and location where bitronix config file is placed

    create setenv.sh (or setenv.bat) file inside TOMCAT_HOME/bin and add following:

    CATALINA_OPTS="-Xmx512M -XX:MaxPermSize=512m -Dbtm.root=$CATALINA_HOME -Dbitronix.tm.configuration=$CATALINA_HOME/conf/btm-config.properties -Djbpm.tsr.jndi.lookup=java:comp/env/TransactionSynchronizationRegistry"

    NOTE: this is an example for unix like systems for Windows $CATALINA_HOME needs to be replaced with windows env variable or absolute path


   *******************************************************************************

4. Configure JEE security for kie-wb on tomcat (with default realm backed by tomcat-users.xml)

   2a. Copy "kie-tomcat-integration" JAR into TOMCAT_HOME/lib (org.kie:kie-tomcat-integration)
   2b. Copy "JACC" JAR into TOMCAT_HOME/lib (javax.security.jacc:artifactId=javax.security.jacc-api in JBoss Maven Repository)
   2c. Copy "slf4j-api" JAR into TOMCAT_HOME/lib (org.slf4j:artifactId=slf4j-api in JBoss Maven Repository)
   2d. Add valve configuration into TOMCAT_HOME/conf/server.xml inside Host element as last valve definition:

      <Valve className="org.kie.integration.tomcat.JACCValve" />

   2e. Edit TOMCAT_HOME/conf/tomcat-users.xml to include roles and users, make sure there will be 'analyst' or 'admin' roles defined as it's required to be authorized to use kie-wb
