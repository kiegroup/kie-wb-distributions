JBoss EAP/AS - BRMS distribution
================================

This module generated the BRMS distribution for JBoss EAP/AS.

BRMS distribution contains:

* The BRMS statis modules layer (BRMS layer).

* The BRMS web application artifact that runs with BRMS layer (kie-drools-wb-webapp).

Table of contents
------------------

* **[Distribution generation](#distribution-generation)**

* **[Resulting artifacts](#resulting-artifacts)**

* **[Deployment on JBoss EAP/AS](#deployment-on-jBoss-eap/as)**

Distribution generation
-----------------------

**Full distribution**

* To generate both BRMS static layer and BRMS web application run <code>mvn clean install</code> from <code>kie-eap-integration-brms</code> root path.

**Profiles**

To generate only some of the resulting distribution artifacts, use the following profiles:

* <code>brms-static-layer</code>: Generates the BRMS static modules layer (BRMS layer).

* <code>brms</code>: Generates the BRMS web application artifact that runs with BRMS layer (kie-drools-wb-webapp).


Resulting artifacts
-------------------

Resulting artifacts for BRMS distributions are:

* BRMS static modules layer (BRMS layer) - <code>eap-modules-distributions-X.Y.Z-brms-layer.zip</code>

* BRMS web application artifact that runs with BRMS layer (kie-drools-wb-webapp) - <code>eap-modules-distributions-X.Y.Z-org.kie.kie-drools-wb-webapp.war</code>

Deployment on JBoss EAP/AS
--------------------------

There are two deploy options:

**Manual deploy**

To deploy the resulting artifacts to a JBoss EAP installation, please follow the steps:

    1.- Unzip generated static modules distribution ZIP file into $JBOSS_HOME.

    2.- Copy the generated distribution skinny wars into $JBOSS_HOME/standalone/deployments

    3.- Run JBoss EAP

**Automatic deploy**

* To run this maven goal, a system property with name <code>eap-path</code> is required and must contains the path to the JBoss EAP/AS installation (JBOSS_HOME).

* Run <code>mvn package -Pdeploy-brms -Deap-path=<JBOSS_HOME></code> from <code>kie-eap-integration-brms</code> root path.

* Example: <code>mvn package -Pdeploy-brms -Deap-path=/home/romartin/development/EAP-6.1.1/jboss-eap-6.1</code>

**IMPORTANT NOTES**:

* Do not use <code>clean</code> Maven target when using automatic deploy, as generated artifacts on target directory are required.
* Please, use a clean JBoss EAP installation for deployment.
