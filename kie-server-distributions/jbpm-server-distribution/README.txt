Welcome to jBPM
http://www.jbpm.org/

Go to the above link for documentation, and additional downloads.

Also, once jBPM is started you can go to http://localhost:8080/jbpm-console

Release Notes
-------------
You can obtain the release notes here:

https://issues.jboss.org/projects/JBPM?selectedItem=com.atlassian.jira.jira-projects-plugin:release-page&status=released

Getting Started
---------------
jBPM requires JDK 1.8 or later. For information regarding installation
of the JDK, see http://www.oracle.com/technetwork/java/index.html


Reporting problems
---------------

In case of any issues or problems please file a jira issue in jBPM project
https://issues.jboss.org/projects/JBPM/summary

Starting a Server
----------------------------
A jBPM server runs a single instance.

<JBOSS_HOME>/bin/standalone.sh      (Unix / Linux)

<JBOSS_HOME>\bin\standalone.bat     (Windows)


Accessing the jBPM Console
-------------------------
Once the server has started you can access the landing page:

http://localhost:8080/jbpm-console

This is the entry point app that provides comprehensive tooling for 
authoring and monitoring your business applications.


Accessing the jBPM execution server
-------------------------
Once the server has started you can access the execution server over
its REST API

http://localhost:8080/kie-server/services/rest/server

A complete Swagger based documentation can be found on running jBPM server at

http://localhost:8080/kie-server/docs

Accessing the jBPM Service Repository
-------------------------
Once the server has started you can access the jBPM Service repository that provides reusable work items
that can be imported into your processes. Following is the link that directs you to it:

http://localhost:8080/repository


Authentication
-------------------------

Access to jbpm console and execution server is protected and requires users to logon. 
jBPM Server comes with predefined set of sample users that can be used directly

admin/admin
krisv/krisv
john/john
mary/mary
katy/katy
jack/jack
kieserver/kieserver1!

Stopping the Server
-------------------
A jBPM server can be stopped by pressing Ctrl-C on the command line.
If the server is running in a background process, the server can be stopped
using the JBoss CLI:

<JBOSS_HOME>/bin/jboss-cli.sh --connect --command=:shutdown      (Unix / Linux)

<JBOSS_HOME>\bin\jboss-cli.bat --connect --command=:shutdown     (Windows)


Switching to different database
-------------------

jBPM runs by default with H2 database with file storage - located under <JBOSS_HOME>/standalone/data/jbpm-db

Users can switch to another database very easily by invoking a script located in <JBOSS_HOME>/bin.

To switch to MySQL use following command when server is stopped
<JBOSS_HOME>/bin/jboss-cli.sh --file=jbpm-mysql-config.cli      (Unix / Linux)

<JBOSS_HOME>\bin\jboss-cli.bat --file=jbpm-mysql-config.cli     (Windows)

To switch to PostgreSQL use following command when server is stopped
<JBOSS_HOME>/bin/jboss-cli.sh --file=jbpm-postgres-config.cli      (Unix / Linux)

<JBOSS_HOME>\bin\jboss-cli.bat --file=jbpm-postgres-config.cli     (Windows)


These scripts assume that database is installed and some default configuration is present:
- host -> localhost
- port -> 3306 for MySQL and 5432 for PostgreSQL
- database name -> jbpm
- user name -> jbpm
- password -> jbpm

in case the values are not correct, edit them in the script files jbpm-mysql-config.cli for MySQL 
and jbpm-postgres-config.cli for PostgreSQL - in both scripts values to be updated are on line 17.


There is H2 script as well to go back to defaults.

After running the script the only thing that is needed is to start the server. 
