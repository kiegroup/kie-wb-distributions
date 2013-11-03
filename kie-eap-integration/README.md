JBoss EAP/AS distributions builder
==================================

This module allows to create custom distributions for JBoss EAP/AS based on static modules.

For more information about JBoss EAP/AS modules refer to [JBoss community documentation](https://docs.jboss.org/author/display/MODULES/Home)


Table of contents
------------------

* **[Builder strategy](#builder-strategy)**

* **[Builder stucture](#builder-structure)**

* **[Core module](#core-module)**

* **[Distributions module](#distributions-module)**

* **[Usage](#usage)**

Builder strategy
================

The builder module implementation is based on ANT and Maven:

* ANT: Read module descriptors and generate module resources and assembly descriptor files.
* Maven: Geneate the final distributions by assembling the generated assembly descriptor files.

The main goals to generate a product distribution are:

1.- Obtain de list of static modules to create from a descriptor file named <code>modules.list</code>.

2.- Obtain the list of dynamic modules to create (web applications)

3.- For each static module, read it's definition, resources and dependencies to other modules from the module descriptor files.

4.- For each static module, generate module descriptor (module.xml), copy module resources (jars, etc) into a temporal directory.

5.- Generate the assembly descriptor for static modules.

5.- For each dynamic module, read it's definition and dependencies descriptor files.

6.- For each dynamic module, generate the jboss-deployment-structure.xml file and the assembly descriptor (excluding artifacts already contained in static modules).

7.- Assembly the static modules distribution (using the dynamic assembly generated files).

8.- Assembly each dynamic module distribution (using the dynamic assembly generated files).


Builder stucture
================

This project contains two sub-modules:

* **[Core module](#core-module)**

This module contain builder core files for generating the static and dynamic modules.

* **[Distributions module](#distributions-module)**

This module is the parent module for all product distributions.

Core module
===========

This module contain builder core files for generating the static and dynamic modules.

Directory structure:

* <code>eap-configurations</code>: Provides some information for each EAP/AS versions.
* <code>modules</code>: Contains common static modules definitions.
* <code>patches</code>: Contains some common patches to apply. See [patches documentation](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-core/src/main/resources/eap-modules/patches/README.md).
* <code>scripts</code>: Contains builder ANT scripts.
* <code>templates</code>: Contains builder templates.

Distributions module
====================

This module is the parent module for all product distributions.

This module uses the core one to generate static and dynamic modules for a given product.

To obtain information about current EAP product distributions, please refer to [kie-eap-integration-distributions/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/README.md) file.


Usage
=====
* This <code>kie-eap-integration</code> maven module is NOT executed on maven build process by default.

* To activate this module, there are two options:

    1.- run with profile <code>eap-modules</code> from <code>kie-wb-distributions</code> root: Ex: <code>$# mvn clean install -Deap-modules</code>

    2.- run <code>$# mvn clean install</code> from <code>kie-eap-integration</code> root directory.

* To obtain more information about distributions generation refer to [kie-eap-integration-distributions/README.md](https://github.com/droolsjbpm/kie-wb-distributions/tree/master/kie-eap-integration/kie-eap-integration-distributions/README.md) file.