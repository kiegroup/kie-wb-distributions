Weblogic deployment instructions
========================================

This document explains how to deploy a KIE Workbench distribution file (_kie-wb-was8.war_) on Weblogic Application Server 12c.

Open the Weblogic's Administration Console _http://localhost:7001/console/console.portal_

Then login (if you have administrative security setup)

Before deploy the war file, some server configurations are required:

Increase JVM memory size
------------------------------

Set environment variable:
USER_MEM_ARGS=-Xms512m -Xmx1024m -XX:MaxPermSize=512m

Security settings
------------------------------

The following settings are required in order to enable the container managed authentication mechanisms provided by the app. server.

Go to **Domain structure > Security realms > myrealm_**


On tabbed menu go to **_Users and groups > Groups_**

   Create 5 groups: admin, analyst

On tabbed menu go to **_Users and groups > Users_**

   Create a single user and add to it the 2 roles above.



JVM Custom properties
--------------------------
**Additional JVM properties**
- javax.xml.bind.context.factory - value must be com.sun.xml.bind.v2.ContextFactory
- org.uberfire.start.method - value must be ejb
- org.uberfire.domain - value must be OracleDefaultLoginConfiguration

all properties can be set by configuring environment variable as follows:

_JAVA_OPTIONS="-Djavax.xml.bind.context.factory=com.sun.xml.bind.v2.ContextFactory -Dorg.uberfire.start.method=ejb -Dorg.uberfire.domain=OracleDefaultLoginConfiguration"_

Deploy the application
--------------------------

Application must be deployed as exploded archive (as folder) to allow complete feature set to be activated.

Follow deployments screen with important selections:
- Application must be installed as Application and not library - second step of installation wizard.
- Security roles must be taken from deployment descriptor only - DD Only - on third step of of installation wizard

Once restarted you should be able to access the kie-wb application by typing the following URL: _http://localhost:7001/{name of the folder of exploded archive}_
