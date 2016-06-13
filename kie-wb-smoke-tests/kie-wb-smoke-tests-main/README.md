Running and debugging smoke tests locally (using IDE)
=====================================================

In order to run the tests easily from IDE, the server (container) needs be already running and the
application deployed. Following Maven command will start the Tomcat 7x and deploy the KIE
Workbench WAR. The server will run until manually (Ctrl+C) stopped:

        $ mvn clean package cargo:run -Pkie-wb,tomcat7

You can of course choose `kie-drools-wb` instead of `kie-wb` and also different container to deploy
to (see the profiles in `pom.xml` for the list of supported containers).

The base application URI is by default `http://localhost:8080/kie-wb`. The cargo also automatically
configures the server (users, groups, JMS queues, etc). After executing the `cargo:run` the
application is ready to be used.

The last step is to update the property `deployable.base.uri`. By default it is
`http://localhost:8080/kie-wb`, but you can override it in case e.g. your server is running on
different port.

You can now run (and debug) the tests easily from IDE by just executing the `@Test` annotated
methods.

How to execute tests for different containers and apps
======================================================
By default, when you run `mvn clean install` _no_ tests will be executed. Tests are executed either when specifying `full`
profile (by setting `-Dfull`) or when configuring explicit container and deployable profiles. The tests can be also executed
on the productized binaries (by setting `-Dproductized`).

Examples of different scenarios:

  * `mvn clean install` - tests are only compiled. Execution is skipped.
  * `mvn clean install -Dfull` - default configuration is used. The tests are executed on KIE Workbench deployed to Tomcat 7.
  * `mvn clean install -Dfull -Dproductized` - same as above, but productized WAR (e.g. tomcat7-redhat) is used.
  * `mvn clean install -Pwildfly10,kie-drools-wb` - tests are executed on KIE Drools Workbench deployed to WildFly 10.
  * `mvn clean install -Dcustom-container -Ddeployable.base.uri=<value>` - tests will be executed on custom container, which needs to be already running.
