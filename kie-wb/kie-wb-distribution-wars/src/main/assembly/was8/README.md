Websphere deployment instructions
========================================

This document explains how to deploy a KIE Workbench distribution file (_kie-wb-was8.war_) on WebSphere Application Server 8.

Open the WebSphere's Administration Console _http://127.0.0.1:9060/ibm/console_

Then login (if you have administrative security setup)

Before deploy the war file, some server configurations are required:

Increase JVM memory size
------------------------------

Go to _Servers > Server Types > WAS app servers_

Go to _MyServer > Server Infrastructure > Process Definition > Java Virtual Machine_

Set the JVM max. heap size to a value greater than 1024 Mb. Otherwise, with the default values, WAS freezes while deploying the war.

Security settings
------------------------------

The following settings are required in order to enable the container managed authentication mechanisms provided by the app. server.

Go to **_Security > Global security_**

   Ensure the option _Enable Application security_ is checked.

Go to **_Users and groups > Manage groups_**

   Create 5 groups: admin, analyst, developer, manager, user

Go to **_Users and groups > Manage users_**

   Create a single user and add to it the 5 roles above.

Go to **_Servers > Server types > WAS app servers > server1 > Session management> Custom properties_**

   Add this property _InvalidateOnUnauthorizedSessionRequestException = true_


**Register the SSL certificate from Github.com**

This is needed in order to enable repository cloning from Github.
This is the case of the kie-wb repository examples which are fetched from Github. To do so:

- Go to _Security > SSL Certificate and Key Management > Manage endpoint security configurations_
- Go to _Outbound section_. Go to your server node within the tree. Select the _HTTP_ subnode.
- Go to _Related Items > Key Stores and certificates_
- Select the row in the table named _NodeDefaultTrustStore_
- Go to _Additional properties > Signer certificates_
- Click button _Retrieve from port_
- Fill out the form with these values: _Host=github.com, Port=443, Alias=github.com_
- Click on _Retrieve signer information_ button, then _Ok_, and finally, _Save_ to master configuration.



Deploy the application
--------------------------

**Upload the WAR file**

  - Left side panel click on *Applications > Application types > Websphere enterprise applications*
  - Click on _Install_, select the _kie-wb-was8.war_ file from your local filesystem. Click _Next_
  - From here, you will be asked with several deployments settings.
  - You'll need to select the datasource created above as the datasource to be used by the application.
  - Screen *Bind listeners for message-driven beans* - select for every bean *Activation Specification* and fill the corresponding activation specification JNDI name into *Target Resource JNDI Name* (e.g. _jms/activation/KIE.SESSION_). You may also specify *Destination JNDI name* using JNDI name of the appropriate JMS queue (e.g. _jms/queue/KIE.SESSION_).
  - We also recommend to set is the context path of the webapp to _kie-wb_.
  - Screen *Map resource references to resources* - for both beans provide JNDI name of KIE.RESPONSE.ALL connection factory (e.g. _jms/conn/KIE.RESPONSE.ALL_).
  - Click _Next_ until finished.

**App. settings**

Go to _Applications > Application types > Websphere enterprise applications > kie-wb app > Security role to user/group mapping_

   - Select the five BPMS roles: admin, analyst, developer, manager, user.
   - Click on _Map Special Subjects_ and select the _All Authenticated in Application's Realm_ option.

Go to _Applications > Application types > Websphere enterprise applications > kie-wb app > Class loading and update detection_

Ensure the following radio buttons are checked:

* _Classes loaded with local class loader first (parent last)_
* _Single class loader for application_


Save the configurations to the master and restart the server.

Once restarted you should be able to access the kie-wb application by typing the following URL: _http://http://localhost:9081/kie-wb_
