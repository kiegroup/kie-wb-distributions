/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.drools.backend.server;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.screens.workitems.backend.server.WorkbenchConfigurationHelper;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jbpm.console.ng.bd.service.AdministrationService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.screens.workbench.backend.BaseAppSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup extends BaseAppSetup {

    private static final Logger logger = LoggerFactory.getLogger( AppSetup.class );

    // default repository section - start
    private static final String OU_NAME = "demo";
    private static final String OU_OWNER = "demo@demo.org";
    // default repository section - end

    private Event<ApplicationStarted> applicationStartedEvent;

    private AdministrationService administrationService;

    private WorkbenchConfigurationHelper workbenchConfigurationHelper;

    protected AppSetup() {
    }

    @Inject
    public AppSetup( @Named("ioStrategy") final IOService ioService,
                     final RepositoryService repositoryService,
                     final OrganizationalUnitService organizationalUnitService,
                     final KieProjectService projectService,
                     final ConfigurationService configurationService,
                     final ConfigurationFactory configurationFactory,
                     final AdministrationService administrationService,
                     final Event<ApplicationStarted> applicationStartedEvent,
                     final WorkbenchConfigurationHelper workbenchConfigurationHelper ) {
        super( ioService, repositoryService, organizationalUnitService, projectService, configurationService, configurationFactory );
        this.administrationService = administrationService;
        this.applicationStartedEvent = applicationStartedEvent;
        this.workbenchConfigurationHelper = workbenchConfigurationHelper;
    }

    @PostConstruct
    public void assertPlayground() {
        try {
            configurationService.startBatch();
            final String exampleRepositoriesRoot = System.getProperty( "org.kie.example.repositories" );
            if ( !( exampleRepositoriesRoot == null || "".equalsIgnoreCase( exampleRepositoriesRoot ) ) ) {
                loadExampleRepositories( exampleRepositoriesRoot,
                                         OU_NAME,
                                         OU_OWNER,
                                         GIT_SCHEME );

            } else if ( "true".equalsIgnoreCase( System.getProperty( "org.kie.example" ) ) ) {

                Repository exampleRepo = createRepository( "repository1",
                                                           GIT_SCHEME,
                                                           null,
                                                           "",
                                                           "" );
                createOU( exampleRepo,
                          "example",
                          "" );
                createProject( exampleRepo,
                               "org.kie.example",
                               "project1",
                               "1.0.0-SNAPSHOT" );
            }

            // Setup mandatory properties for Drools-Workbench
            final ConfigItem<String> supportRuntimeDeployConfigItem = new ConfigItem<>();
            supportRuntimeDeployConfigItem.setName( "support.runtime.deploy" );
            supportRuntimeDeployConfigItem.setValue( "false" );
            setupConfigurationGroup( ConfigType.GLOBAL,
                                     GLOBAL_SETTINGS,
                                     getGlobalConfiguration(),
                                     supportRuntimeDeployConfigItem );

            // Setup properties required by the Work Items Editor
            setupConfigurationGroup( ConfigType.EDITOR,
                                     WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS,
                                     workbenchConfigurationHelper.getWorkItemElementDefinitions() );

            // rest of jbpm wb bootstrap
            administrationService.bootstrapConfig();
            administrationService.bootstrapDeployments();
            // notify components that bootstrap is completed to start post setups
            applicationStartedEvent.fire( new ApplicationStarted() );
        } catch ( final Exception e ) {
            logger.error( "Error during update config", e );
            throw new RuntimeException( e );
        } finally {
            configurationService.endBatch();
        }
    }

    protected ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                       GLOBAL_SETTINGS,
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.dateformat",
                                                                 "dd-MMM-yyyy" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.datetimeformat",
                                                                 "dd-MMM-yyyy HH:mm:ss" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultlanguage",
                                                                 "en" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultcountry",
                                                                 "US" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                                                                 "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "rule-modeller-onlyShowDSLStatements",
                                                                 "false" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "support.runtime.deploy",
                                                                 "false" ) );
        return group;
    }
}
