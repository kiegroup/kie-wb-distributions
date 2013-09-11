Custom distribution for JBoss EAP based on static modules
==========================================================

BPMS/BRMS application are distributed using skinny WARs for JBoss EAP 6.1, JBoss AS7, etc. But these distributions depend only on JBoss EAP base static modules.

This module allows to create a BPMS/BRMS distribution based on base EAP modules and custom new generated ones. So, this distribution contains:
* A new static modules layer for JBoss EAP named <code>bpms</code>
* Skinny WARS for BPMS/BRMS that depend on base and <code>bpms</code> layer modules.


BPMS Layer distribution
-----------------------

The BPMS static modules layer distribution adds the follosing modules:

* org.kie.lib
* org.kie
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


How to run
==========

Enable this module
------------------
* This <code>kie-eap-integration</code> maven module is NOT executed on maven build process by default.

* To activate this module, there are two options:
* run with profile <code>eap-modules</code> from kie-wb-distributions root.
$# mvn clean install -Deap-modules
* run $# mvn clean install from this kie-eap-integration root directory.

EAP version to deploy
---------------------
* NOTE: By default, the distribution is generated for JBoss EAP 6.1.1. If you must generate for EAP 6.1.0 use <code>-Deap-610</code>
$# mvn clean install -Deap-modules Deap-610

Profiles:
----------
- If no profile for building this module is specified, only static modules distribution is generated.
- To generate the BPMS (kie-wb) distribution, use <code>mvn clean install -Dbpms</code> to enable the bpms profile and generate the skinny war.
- To generate the BRMS (kie-drools-wb) distribution, use <code>mvn clean install -Dbrms</code> to enable the brms profile and generate the skinny war.
- To generate the JBPM Dashboard distribution, use <code>mvn clean install -Djbpm-dashboard</code> to enable the jbpm-dashboard profile and generate the skinny war.
- To generate all skinny wars (for BPMS, BRMS and jBPM Dashboard) at same build, use <code>mvn clean install -Dbpms -Dbrms -Djbpm-dashboard</code>

Examples
-------
- Generate only the static modules: $# mvn clean install
- Generate static modules and BPMS distributions: $# mvn clean install -Dbpms
- Generate static modules and BRMS distributions: $# mvn clean install -Dbrms
- Generate static modules and jbpm-dashboard distributions: $# mvn clean install -Djbpm-dashboard
- Generate static modules and BPMS, BRMS and jbpm-dashboard distributions: $# mvn clean install -Dbpms -Dbrms -Djbpm-dashboard

Resulting artifacts
---------------------
* The resulting artifacts are generated into <code>target</code> directory:
- <code>kie-eap-integration-X.Y.Z-SNAPSHOT-eap-static-modules.zip</code> Contains the static modules zip distribution to deploy on JBoss EAP.
- <code>kie-eap-integration-X.Y.Z-SNAPSHOT-org.kie.kie-wb-webapp-eap-modules.war</code> Contains the skinny war distribution for kie-wb (BPMS) artifact customized on new static bpms modules
- <code>kie-eap-integration-X.Y.Z-SNAPSHOT-org.kie.kie-drools-wb-webapp-eap-modules.war</code> Contains the skinny war distribution for kie-drools-wb (BRMS) artifact customized on new static bpms modules
- <code>kie-eap-integration-X.Y.Z-SNAPSHOT-org.jbpm.dashboard.jbpm-dashboard-eap-modules.war</code> Contains the skinny war distribution for jBPM Dashbuilder artifact customized on new static bpms modules

Deployment on JBoss EAP
=======================

To deploy the resulting artifacts to a JBoss EAP installation, please follow the steps:
1.- Unzip <code>kie-eap-integration-X.Y.Z-SNAPSHOT-eap-static-modules.zip</code> into $JBOSS_HOME
2.- Copy a generated skinny war (for example <code>kie-eap-integration-X.Y.Z-SNAPSHOT-org.kie.kie-wb-webapp-eap-modules.war</code>) into $JBOSS_HOME/standalone/deployments
3.- Run JBoss EAP
NOTE: Please, use a clean JBoss EAP installation for deployment.
