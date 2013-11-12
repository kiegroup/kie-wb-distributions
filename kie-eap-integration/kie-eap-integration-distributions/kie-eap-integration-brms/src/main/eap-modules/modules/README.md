BRMS distribution - Static modules definitions
==============================================

In this <code>modules</code> folder, BRMS distribution is overriding some <code>kie-eap-integration-core</code> modules definitions:

* <code>org.jbpm.resources</code>: Resources in jbpm module for brms are different from the default ones used by bpms distro.

BRMS distribution - dynamic modules
===================================

This distribution generates a dynamic module:

* The BRMS webapp (<code>kie-drools-wb-webapp</code>) that runs with brms static modules layer.