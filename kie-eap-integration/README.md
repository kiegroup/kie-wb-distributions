JBoss EAP/AS layer distributions builder
========================================

This project allows to create custom distributions for JBoss EAP/AS based on JBoss static modules.

The idea is to deploy some of the common resources that a single or several applications require in a static layer in JBoss EAP/AS container, then generate lightweight skinny WARs that depends on this layer.

For more information about JBoss EAP/AS modules refer to [JBoss community documentation](https://docs.jboss.org/author/display/MODULES/Home)

Table of contents
------------------

* **[Introduction](#introduction)**
* **[Strategy](#strategy)**
* **[Project Modules](#project-modules)**
* **[EAP Modules Plugin](#eap-modules-plugin)**
* **[Usage](#usage)**
* **[Limitations](#limitations)**

Introduction
============

In general you can create a JBoss static module manually by basically defining its resources and its dependencies to other modules in a XML file.

But in large projects, where there is a lot of people continuously working on, it can be difficult to maintain the dependencies between the modules manually, so this plugin provides a way where the dependencies are almost automatically resolved using Maven definitions for the modules to generate.

This project basically provides all the module definitions and a Maven 3.0 plugin that allows to create the modules that will be contained in a JBoss EAP/AS static layer and the web applications that use them.

The use of this plugin has several advantages:

* Full Maven integration
* Extensible
    * Some services like module scanners or dependency/graph builder are Plexus services.
    * Support for implementing different types of graph (flat, tree, etc)
* It makes life easier to the developer
    * The developer only has to define which modules to create, the metadata for them and its resources, not its dependencies.
    * Allows the developer not to worry about module dependencies, they are resolved by the maven definitions and added automatically in the module descriptor generated.
    * Allows the developer not to worry about module dependencies to base JBoss EAP/AS base modules too, as the plugin resolves them.
* Build time error detection
    * Any change in project artifacts that can produce a missing module dependency is detected in the build (package) time, not in runtime as if you create the modules manually. The build fails if any dependence is not satisfied.
    * Detects versions mismatched for module artifacts
    * Detects project unresolved resources (defined as module resources)
    * Detect resource duplications in module definitions (including EAP base ones)
* Customization
    * Support for static (not automatically resolved) dependencies.
    * Easy to add new distributions, just creating a pom file for each module

Strategy
========

In order to delegate to maven the modules dependency graph, the static modules to generate must be defined in a Maven format.

The way that each static module is defined is by a Maven POM file:

* Each pom artifact defines a JBoss static module
* JBoss static module meta-data (name, slot, etc) are Maven properties in the module pom file.
* JBoss static module resources are Maven dependencies in the module pom file.

Using this approach, the plugin can delegate to Maven the dependency graph generation and then, generate the modules layer dependencies.

* JBoss EAP/AS base layer support
    * Consider that a default JBoss EAP/AS installation already contains a static modules layer named <code>base</code>, which contains the container **shared** libraries.
    * The plugin resolves them too.
    * Each JBoss EAP/AS version has to be defined as a base version in the plugin, by generating a pom for each module.
    * In order to generate these EAP/AS module pom files, the plugin has a specific goal that read the module definitions and each resource in a jboss installation in order to generate the module pom files automatically.

Project Modules
===============

This project contains four modules:

* <code>kie-eap-modules</code> contains all module definitions.
    * <code>kie-eap-static-modules</code> contains the drools/jBPM static modules definitions for the static layer to generate.
    * <code>kie-jboss-eap-base-modules</code> contains the JBoss EAP/AS base modules definitions (pre-installed ones) for a given EAP/AS version.
    * <code>kie-eap-dynamic-modules</code> contains the dynamic modules. In this case, the drools/jBPM BPMS and BRMS webapp distributions.
* <code>kie-jboss-modules-plugin</code> contains the plugin sources.
* <code>kie-eap-distributions</code> contains the distributions to generate. There are two types of distributions:

    * Static layer distributions -> Are a set of static modules (AKA <code>layer</code>)

    * Webapp distributions -> Are web applications ready to deploy over a static layer distribution.

EAP Modules Plugin
==================

Introduction
------------
This section contains information about the <code>org.kie:jboss-modules</code> plugin.

The goal for this plugin is to generate static layer or webapp distributions by delegating to Maven all the dependency resolutions.

To generate a distribution, the plugin requires some inputs:

* *The distribution name*
    The distribution name is the name for the layer to generate.
* *The base modules - Maven module*
    All the JBoss EAP/AS base module definitions must be contained in a global maven module.
    The plugin uses this maven module to scan all the base module definitions (metadata, resources, etc).
    For each JBoss EAP/AS version, the base modules can differ, so exist a maven module that contains all base module definitions for each container version.
    Currently, the version supported is <code>6.1.1</code> and this maven module is <code>jboss-eap-6.1.1</code>
* *The static modules*
    All the static module definitions that the generated layer will contain must be specified as project dependencies.
    All dependency artifacts of type <code>pom</code> will be considered static modules to add in the layer.
    The plugin uses this artifacts to scan all the static module definitions (metadata, resources, etc).

Using these inputs, the plugin:

1.- *Scans for static module definitions*
    Read module names, slots, resources and other meta-information.

2.- *Scans for base module definitions*
    Read module names, slots, resources and other meta-information.

3.- *Generates the dependency graph for the modules*

4.- *Resolve the module that contains each node in the dependency graph, then adds a dependency to this module*

5.- *Generates the static modules graph with all resolutions performed*

6.- *Generates the assembly descriptors for the layer to generate, based on the previous static module graph resolved*

Goals
-----

TODO

* <code>generate-eap-distribution</code>
* <code>build-static</code>
* <code>build-dynamic</code>

Usage
=====

This section contains information about:

* Profiles
* Distributions generation
* Adding static module definitions
* Adding new JBoss EAP/AS versions to support.
* Adding other static layer definitions.
* How to change the target JBoss EAP/AS version

Profiles
--------
These are the available profiles:   

* <code>eap-base-modules</code>: Used to build only the JBoss EAP base module descriptors. Think that these descriptors are unusually changed from the initial definition, so this module should not be constantly build.   
* <code>bpms-layer</code>: Used to generate the BPMS layer distribution. It results in the BPMS static layer generated in a ZIP file.   
* <code>bpms-webapp</code>: Used to generate the BPMS webapp skinny WAR that works using the BPMS layer.   
* <code>brms-layer</code>: Used to generate the BRMS layer distribution. It results in the BRMS static layer generated in a ZIP file.   
* <code>brms-webapp</code>: Used to generate the BRMS webapp skinny WAR that works using the BRMS layer.   

Distributions generation
------------------------
This section describes how to generate the distributions.   

* **Generate ALL distributions** (No profile)   
If no profile enabled via profile identifier or via property, ALL modules and distributions are generated.   
Run <code>mvn clean install</code>   

* **Generate BPMS Layer**
Generates the BPMS layer ZIP   
Run <code>mvn clean install -Dbpms-layer</code>   

* **Generate BPMS Layer and BPMS webapp**
Generates the BPMS layer ZIP and the skinny WAR files for kie-wb-webapp and jbpm-dashbuilder web applications.   
Run <code>mvn clean install -Dbpms-layer -Dbpms-webapp</code>   

* **Generate BRMS Layer**
Generates the BRMS layer ZIP   
Run <code>mvn clean install -Dbrms-layer</code>   

* **Generate BRMS Layer and BRMS webapp**
Generates the BPMS layer ZIP and the skinny WAR file for kie-drools-wb-webapp web application.   
Run <code>mvn clean install -Dbrms-layer -Dbrms-webapp</code>   

* **Generate the Base JBoss EAP/AS module descriptors**
Generates the base module definitions for all JBoss EAP/AS versions included.    
Think that these descriptors are unusually changed from the initial definition, so this module should not be constantly build.    
Run <code>mvn clean install -Deap-base-modules</code>   

* NOTE: All these commands must be run from <code>kie-eap-integration</code> root directory.   
* NOTE: This <code>kie-eap-integration</code> module is not build by default for <code>kie-wb-distributions</code> module build. To enable it, active the profile <code>eap-modules</code>.   

How to add static dependencies
------------------------------

You can add specific custom inter-module dependencies if Maven do not resolve them for any reason.

This example adds a static dependency from module <code>org.jbpm</code> to module <code>org.drools</code>:

1.- Edit the pom file for the <code>org.jbpm</code> module, located at <code>kie-eap-modules/kie-eap-static-modules/org-jbpm</code>
2.- Add a new maven property:
    - Named <code>module.dependencies</code>
    - The value is a comma separated names of modules to depend on, in format <code>module:slot</code>. You can use maven properties as <code>${project.version}</code>
    In this example: <code><module.dependencies>org.drools:${project.version}</module.dependencies></code>
3.- Build and install the <code>org.jbpm</code> maven module.
4.- Build and install the static layer distribution and the web application distribution, if necessary.

How to create a static module definition
----------------------------------------

If you want to add a new module, you have to create a new maven module in the <code>kie-eap-modules</code>:

* The module must be packaged as a pom artifact.
* The module dependencies represents the resources (JARs, etc) that will contain the generated JBoss static module.
* You must define some properties in the module pom that represents the static module meta-data.
* Add this new maven module in the parent <code>modules</code> section.

How to add JBoss EAP/AS base modules for a given version
--------------------------------------------------------

The plugin has a specific goal that scans a filesystem installation of a JBoss EAP/AS container:
* Look for all module descriptors in the <code>modules</code> directory for the JBoss container.
* For each descriptor, scan its resources. If the resource is a JAR, the JAR is scanned looking for maven metadata of this artifact.
* Generates all the maven modules that represent the JBoss base modules.

Then you can quickly add a new container version support.

In addition, once a new maven modules for a specific version are generated, you can add custom mappings by editing the generated pom files.

How to use another another static modules layer definition
----------------------------------------------------------

TODO

How to change the target JBoss EAP/AS version
----------------------------------------------

TODO

Limitations
===========
* The plugin only supports the generation of a single static layer.
* Maven 3.0.X - Aether API from Maven 3.0.X to Maven 3.1.X has been changed. This plugin version only supports Maven 3.0.X
* The current plugin graph implementation type (currently only FLAT) generates the static modules by NOT exporting the dependencies (see <code>export</code> attribute for JBoss module descriptors).