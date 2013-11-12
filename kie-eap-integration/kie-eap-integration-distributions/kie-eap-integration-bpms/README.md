JBoss EAP/AS - BPMS distribution
================================

This module generated the BPMS distribution for JBoss EAP/AS.

BPMS distribution contains:

* The BPMS statis modules layer (BPMS layer).

* The BPMS web application artifact that runs with BPMS layer (kie-wb-webapp).

* The jBPM Dashboard web application artifact that runs with BPMS layer (jbpm-dashboard).

Table of contents
------------------

* **[Distribution generation](#distribution-generation)**

* **[Resulting artifacts](#resulting-artifacts)**

* **[Deployment on JBoss EAP/AS](#deployment-on-jBoss-eap/as)**

Distribution generation
-----------------------

**Full distribution**

* To generate both BPMS static layer and BPMS and jbpm-dashbuilder web applications run <code>mvn clean install</code> from <code>kie-eap-integration-bpms</code> root path.

**Profiles**

To generate only some of the resulting distribution artifacts, use the following profiles:

* <code>bpms-static-layer</code>: Generates the BPMS static modules layer (BPMS layer).

* <code>bpms</code>: Generates the BPMS web application artifact that runs with BPMS layer (kie-wb-webapp).

* <code>jbpm-dashboard</code>: Generates the jBPM Dashboard web application artifact that runs with BPMS layer (jbpm-dashboard).


Resulting artifacts
-------------------

Resulting artifacts for BPMS distributions are:

* BPMS static modules layer (BPMS layer) - <code>eap-modules-distributions-X.Y.Z-bpms-layer.zip</code>

* BPMS web application artifact that runs with BPMS layer (kie-wb-webapp) - <code>eap-modules-distributions-X.Y.Z-org.kie.kie-wb-webapp.war</code>

* jBPM Dashboard web application artifact that runs with BPMS layer (jbpm-dashboard) - <code>eap-modules-distributions-X.Y.Z-org.jbpm.dashboard.jbpm-dashboard.war</code>

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

* Run <code>mvn package -Pdeploy-bpms -Deap-path=<JBOSS_HOME></code> from <code>kie-eap-integration-bpms</code> root path.

* Example: <code>mvn package -Pdeploy-bpms -Deap-path=/home/romartin/development/EAP-6.1.1/jboss-eap-6.1</code>

**IMPORTANT NOTES**:

* Do not use <code>clean</code> Maven target when using automatic deploy, as generated artifacts on target directory are required.
* Please, use a clean JBoss EAP installation for deployment.
