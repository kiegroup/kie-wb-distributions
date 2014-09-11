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

   Create 5 groups: admin, analyst, developer, manager, user

On tabbed menu go to **_Users and groups > Users_**

   Create a single user and add to it the 5 roles above.


Configure a data source
--------------------------------

The application requires a datasource which must be created prior to the deployment of the WAR:

**Create the data source**

  - Left side panel, click on Services > Data sources_ and click New > Generic Data source
  - Fill out the creation form. Set the following JNDI name _jdbc/jbpm_
    (must match the data source defined in the _persistence.xml_ file contained in the _kie-wb.war_)
  - Follow instructions provided by Weblogic console
  - NOTE: make sure that data source has assigned target server on which is going to be deployed


Configure JMS resources
--------------------------

Before creation of JMS resources following components on Weblogic server must be created:
- JMS Server
- JMS module

for creation and configuration consult Weblogic documentation.

Connection factories and queues are created inside JMS module.

**Create JMS Connection factories**

- KIE.RESPONSE.ALL - to receive all responses produced by bpms
    assigned JNDI name needs to be set as one of JVM custom properties (kie.services.jms.queues.response)
- KIE.INPUT  - to send messages to bpms
    assigned JNDI name will be used when sending messages over JMS

  - Left side panel click on *Services > Messaging > JMS Modules > {name of the jms module} *
  - Click new and select Connection factory as type
  - Provide the name, JNDI name (e.g. _KIE.RESPONSE.ALL_ and _jms/conn/KIE.RESPONSE.ALL_)
  - Follow instructions on the screen

**Create JMS Queues**
- KIE.AUDIT - for asynchronous audit log
- KIE.RESPONSE.ALL - for bpms responses
- KIE.SESSION - for ksession based operations
- KIE.TASK - for task based operations

  - Left side panel click on _Services > Messaging > JMS Modules > {name of the jms module}_
  - Click new and select Queue as type
  - Provide a name, JNDI name (e.g. _KIE.AUDIT_ and _jms/queue/KIE.AUDIT_)
  - Choose the bus name
  - Choose _Create Service Bus Integration Destination_, follow the wizard and select the newly created queue name
  - Click _OK_


JVM Custom properties
--------------------------
**Additional JVM properties**
- kie.services.jms.queues.response - {JNDI_NAME} -- JNDI name of the response queue for JMS remote API
- javax.xml.bind.context.factory - value must be com.sun.xml.bind.v2.ContextFactory
- org.uberfire.start.method - value must be ejb
- org.uberfire.domain - value must be OracleDefaultLoginConfiguration

all properties can be set by configuring environment variable as follows:

_JAVA_OPTIONS="-Dkie.services.jms.queues.response=jms/KIE.RESPONSE.ALL -Djavax.xml.bind.context.factory=com.sun.xml.bind.v2.ContextFactory -Dorg.uberfire.start.method=ejb -Dorg.uberfire.domain=OracleDefaultLoginConfiguration"_

Deploy the application
--------------------------

Application must be deployed as exploded archive (as folder) to allow complete feature set to be activated.

Follow deployments screen with important selections:
- Application must be installed as Application and not library - second step of installation wizard.
- Security roles must be taken from deployment descriptor only - DD Only - on third step of of installation wizard

Once restarted you should be able to access the kie-wb application by typing the following URL: _http://localhost:7001/{name of the folder of exploded archive}_
