JBoss EAP/AS distributions
==========================

BPMS/BRMS web applications are distributed using skinny WARs for JBoss EAP 6.1, JBoss AS7, etc. These distributions depend only on JBoss EAP base static modules (the default installed ones).

This module is used to create a custom EAP/AS static modules layer. So the generated WAR will be lighter as many core libraries are included into the modules.

This distribution generates:

1.- A BPMS static modules layer for JBoss EAP/AS (named <code>bpms</code>) - This layer is used when deploying the BPMS and jBPM dashboard skinny wars (artifact #3, #4)

2.- A BRMS static modules layer for JBoss EAP/AS (named <code>brms</code>) - This layer is used when deploying the BRMS skinny war (artifact #5)

3.- Skinny WAR for BPMS that runs over <code>bpms</code> layer.

4.- Skinny WAR for jBPM dashboard that runs over <code>bpms</code> layer.

5.- Skinny WAR for BRMS that runs over <code>brms</code> layer.



Table of contents
------------------

* **[BPMS/BRMS Layer distribution](#bpms/brms-layer-distribution)**

* **[Distribution generation](#distribution-generation)**

* **[Resulting artifacts](#resulting-artifacts)**

* **[Deployment on JBoss EAP/AS](#deployment-on-jBoss-eap/as)**


BPMS/BRMS Layer distribution
----------------------------

The static module layer distribution contains the following modules:

* org.kie.lib
* org.kie
* org.uberfire
* org.sonatype.aether
* org.apache.ant
* org.apache.camel
* org.codehouse.plexus
* org.apache.commons.math
* org.apache.commons.compress
* org.drools
* org.apache.commons.exec
* org.apache.helix
* org.jbpm
* org.eclipse.jgit
* org.apache.lucene
* org.apache.maven
* org.mvel
* org.apache.commons.net
* org.apache.poi
* com.google.protobuf
* org.sonatype.sisu
* org.jboss.solder
* org.sonatype.maven
* org.sonatype.plexus
* org.apache.commons.vfs (Not in BRMS ditribution)
* org.apache.maven.wagon
* org.apache.zookeeper
* org.apache.batik
* org.apache.commons.httpclient
* org.apache.commons.fileupload
* org.apache.commons.jxpath
* org.apache.commons.logging
* com.opensymphony.quartz (Not in BRMS ditribution)
* org.junit
* org.apache.xmlbeans

NOTE: For BPMS and BRMS, the modules list is the same, but the resources that each module contains are not the same.

Distribution generation
-----------------------

**BPMS distribution**

* BPMS distribution is generated in module <code>kie-eap-integration-bpms</code>.

* To generate only the BPMS distribution, run <code>mvn clean install</code> from <code>kie-eap-integration-bpms</code> root path.

* To see BPMS distribution generation details, refer to [kie-eap-integration-bpms/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/kie-eap-integration-bpms/README.md).

**BRMS distribution**

* BRMS distribution is generated in module <code>kie-eap-integration-brms</code>.

* To generate only the BRMS distribution, run <code>mvn clean install</code> from <code>kie-eap-integration-brms</code> root path.

* To see BRMS distribution generation details, refer to [kie-eap-integration-brms/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/kie-eap-integration-brms/README.md).

**All distributions**

* To generate both BPMS/BRMS distributions, run <code>mvn clean install</code> from <code>kie-eap-distributions</code> root path.

**EAP versions**

* Generated distributions can contain some JBoss EAP patches or workarounds depending on EAP/AS version where it will be deployed.

* By default, the version for EAP is 6.1.1.GA. If you want to deploy this modules on a different version, please use the profile that matches the version:

    1.- JBoss EAP 6.1.0 - Profile name: <code>eap-610</code>

    2.- JBoss EAP 6.1.1 - Profile name: <code>eap-611</code>

Resulting artifacts
-------------------

* For BPMS distribution, refer to [kie-eap-integration-bpms/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/kie-eap-integration-bpms/README.md).

* For BRMS distribution, refer to [kie-eap-integration-brms/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/kie-eap-integration-brms/README.md).


Deployment on JBoss EAP/AS
--------------------------

Once a product distribution has been generated, next step is to deploy it into a JBoss EAP/AS installation.

There are basically two types of generated artifacts to deploy:

* Static modules layer distribution

    Is a ZIP containing the generated static module layer.

* Dynamic modules distributions (Web applications)

    One or more generated skinny web applications WAR files.

There are two deploy options:

**Manual deploy**

To deploy the resulting artifacts to a JBoss EAP installation, please follow the steps:

    1.- Unzip generated static modules distribution ZIP file into $JBOSS_HOME.

    2.- Copy the generated distribution skinny wars into $JBOSS_HOME/standalone/deployments

    3.- Run JBoss EAP

**Automatic deploy**

To deploy the resulting artifacts to a JBoss EAP installation, please follow the steps:

* To run this maven goal, a system property with name <code>eap-path</code> is required and must contains the path to the JBoss EAP/AS installation (JBOSS_HOME).

* For BPMS distribution, refer to [kie-eap-integration-bpms/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/kie-eap-integration-bpms/README.md).

* For BRMS distribution, refer to [kie-eap-integration-brms/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/kie-eap-integration-brms/README.md).


**IMPORTANT NOTE**: Please, use a clean JBoss EAP installation for deployment.
