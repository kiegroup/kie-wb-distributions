Patches
=======

This directory contains some patches required to run BPMS/BRMS applications using a custom EAP static module layer distribution.

Table of contents
------------------

* **[Patches and the patch lifecycle](#patches-and-the-patch-lifecycle)**

* **[Available patches](#available-patches)**

* **[Other workarounds](#other-workarounds)**

* **[How to create a patch](#how-to-create-a-patch)**

Patches and the patch lifecycle
===============================

* Patches are considered independent extensions for the eap-modules builder that allow to perform additional operations when creating the modules distribution.

* Patches are coded as ANT build files.

* Patches have a unique identifier that corresponds to the ANT filename.

* Patches are defined for:
  - A given JBoss EAP version.
  - A given static/dynamic module.

* When building the modules distribution, always an EAP version is indicated to the build process, so a patch is executed if:
 - The EAP version descriptor has the patch enabled for this version (patches are executed for a given EAP version, might not apply to all versions)
 - The static/dynamic module that is currently building have the patch definition entry in the module definition file.

* Patches have their own lifecycle, allowing the user to create new patches quickly and intercept in the build generation process.

* To see the lifecycle for static module patches refer to [templates/patch-static-module-template.xml](https://github.com/droolsjbpm/kie-wb-distributions/blob/master/kie-eap-integration/kie-eap-integration-core/src/main/resources/eap-modules/patches/templates/patch-static-module-template.xml)

* To see the lifecycle for dynamic module patches refer to [templates/patch-dynamic-module-template.xml](https://github.com/droolsjbpm/kie-wb-distributions/blob/master/kie-eap-integration/kie-eap-integration-core/src/main/resources/eap-modules/patches/templates/patch-dynamic-module-template.xml)


Available patches
=================

These are the current coded and tested available patches.

CDI Extensions
--------------
* Patch identifier: <code>dynamic-module-patch-cdi-extensions</code>  
* In EAP 6.1.0.GA the CDI Extensions declared in JARs from the BPMS layer are not loaded.  
* This bug is already reported and fixed for EAP 6.1.1.  
   See https://bugzilla.redhat.com/show_bug.cgi?id=988093  
* The patch consist of copying the CDI extensions from jars inside <code>META-INF/services</code> directory of the webapp.  

Servlet spec 3.0 - Webfragments
-------------------------------
* Patch identifier: <code>dynamic-module-patch-webfragments</code>  
* Is known that on both EAP 6.1.0. and 6.1.1 webfragment descriptors located inside custom static modules are not loaded.
* The patch consists on creating a new jar on runtime with the web-fragment descriptor to use as a patch. For each web-fragment descriptor a new jar is created and added into WEB-INF/lib of the webapp.
* This method allows to not modify the original deployment descriptor (web.xml) of the webapp.

Other workarounds
=================

These ones are not patches themselves, they do not have any patch build file and modules are not referencing they as are not patches.  
But for some EAP unknown issues yet, some artifacts must be placed in different locations than expected, and these workarounds have been applied to the BPMS/BRMS distribution.  

Seam transactions
-----------------
Seam consists of two artifacts:  
* seam-transaction-api-3.X.jar  
* seam-transaction-3.X.jar  

The jBPM core static module for EAP depends on seam transaction api. So, this jars should be placed in another static module, not in the webapp.  
But for a unknown reason yet, when putting seam-transaction-3.X.jar outside the webapp, the transactions are not running.  
The reason seems to be that the transaction interceptor defined in <code>beans.xml</code> located inside webapp, is not registered if seam-transaction-3.X.jar (impl classes) is outside webapp lib.  
This interceptor is:  
 <code>
 <interceptors>
      <class>org.jboss.seam.transaction.TransactionInterceptor</class>
  </interceptors>
 </code>  
This behaviour should be analyzed with EAP team.  

REST services
-------------
As seam transactions, if the jar containing kie remote REST services <code>kie-common-services-6-X</code> is located outside webapp lib, for example inside a EAP static module, the services are not running.  
This behaviour should be analyzed with EAP team.  

How to create a patch
=====================

* NOTE: For this example, consider that the patch to create has as identifier the value <code>patch-example</code>

1.- Create the filesystem structure for the patch inside [templates/src](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-core/src/main/resources/eap-modules/patches/src)  
    1.1.- Create a directory named <code>patch-example</code>  
    1.2.- Create a ANT build file named <code>patch-example</code> by copying the template file from:  
    1.2.1.- In case of a patch for a static module: [templates/patch-static-module-template.xml](https://github.com/droolsjbpm/kie-wb-distributions/blob/master/kie-eap-integration/kie-eap-integration-core/src/main/resources/eap-modules/patches/templates/patch-static-module-template.xml)    
   1.2.2.- In case of a patch for a dynamic module: [templates/patch-dynamic-module-template.xml](https://github.com/droolsjbpm/kie-wb-distributions/blob/master/kie-eap-integration/kie-eap-integration-core/src/main/resources/eap-modules/patches/templates/patch-dynamic-module-template.xml)   
2.- Implement the lifecycle methods.  
3.- Add the patch to the EAP version descriptors that it will apply  
    3.1.- The EAP version descriptors are located in [eap-configurations](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-core/src/main/resources/eap-modules/eap-configurations)  
    3.2.- Add a property named <code>patch.patch-example</code> with value:  
    3.2.1.- If the patch apply for this EAP version: <code>true</code>  
    3.2.2.- If the patch does apply for this EAP version: <code>false</code>  
4.- Add the patch definition entry in the static and/or dynamic modules that it will apply.  
    4.1.- Add a property named <code>module.patch..patch-example</code> with the value required by the patch execution.  
5.- Build the core module [kie-eap-integration-core](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-core)  
6.- Generate the desired distributions.
