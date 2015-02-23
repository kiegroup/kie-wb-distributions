Running and debugging smoke tests locally (using IDE)
=====================================================

In order to run the tests easily from IDE, the server (container) needs be already running and the
application deployed. Following Maven command will start the Tomcat 7x and deploy the KIE
Workbench WAR. The server will run until manually (Ctrl+C) stopped:

        $ mvn clean package cargo:run -Pkie-wb,tomcat7x

You can of course choose `kie-drools-wb` instead of `kie-wb` and also different container to deploy
to (see the profiles in `pom.xml` for the list of supported containers). For some containers,
you also need to supply correct download URL, e.g. for EAP 6.4
`-Deap64x.download.url=file:///home/user/some/path/jboss-eap-6.4.0.zip` (just an example, update
the path to reflect the real location on your filesystem).


The base application URI is by default `http://localhost:8080/kie-wb`. The cargo also automatically
configures the server (users, groups, JMS queues, etc). After executing the `cargo:run` the
application is ready to be used.

The last step is to update the property `deployable.base.uri`. By default it is
`http://localhost:8080/kie-wb`, but you can override it in case e.g. your server is running on
different port.

You can now run (and debug) the tests easily from IDE by just executing the `@Test` annotated
methods.

