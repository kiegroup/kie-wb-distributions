# Business Central tests

This module contains tests which require Business Central to be running in order to be executed.

## Running and debugging tests locally

You can run tests directly from the command line.
Cargo will take care of starting the container and the tests will be run afterwards.

```bash
mvn clean verify -Pbusiness-central,wildfly11
mvn clean verify -Pbusiness-central,eap7 -Dproductized -Deap7.download.url=url-where-to-get-the-eap-zip
```

In order to run the tests easily from IDE, the server (container) needs to be already running and the application be deployed.
Following Maven command will start the Wildfly 11 and deploy the Business Central WAR.
The server will run until manually (Ctrl+C) stopped:

```bash
mvn clean package cargo:run -Pkie-wb,wildfly
```

You can of course choose `kie-drools-wb` instead of `kie-wb` and also different container to deploy
to (see the profiles in `pom.xml` for the list of supported containers).

The base application URI is by default `http://localhost:8080/kie-wb`.
Cargo also automatically configures the server (users, groups, etc).
After executing the `cargo:run` the application is ready to be used.

The last step is to update the property `kie.wb.url`.
By default it is `http://localhost:8080/kie-wb`, but you can override it in case e.g. your server is running on different port.
