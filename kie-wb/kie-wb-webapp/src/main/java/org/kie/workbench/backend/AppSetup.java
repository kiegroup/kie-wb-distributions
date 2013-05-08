/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.backend;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.console.ng.pr.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.pr.service.Initializable;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.impl.VFSDeploymentUnit;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.services.cdi.Startup;
import org.kie.commons.services.cdi.StartupType;
import org.kie.guvnor.workitems.service.WorkItemsEditorService;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.impl.ActiveFileSystemsFactory;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup {

    // default repository section - start
    private static final String JBPM_WB_PLAYGROUND_SCHEME = "git";
    private static final String JBPM_WB_PLAYGROUND_ALIAS = "jbpm-playground";
    private static final String JBPM_WB_PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    private static final String JBPM_WB_PLAYGROUND_UID = "guvnorngtestuser1";
    private static final String JBPM_WB_PLAYGROUND_PWD = "test1234";

    private static final String DROOLS_WB_PLAYGROUND_SCHEME = "git";
    private static final String DROOLS_WB_PLAYGROUND_ALIAS = "uf-playground";
    private static final String DROOLS_WB_PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    private static final String DROOLS_WB_PLAYGROUND_UID = "guvnorngtestuser1";
    private static final String DROOLS_WB_PLAYGROUND_PWD = "test1234";

    private static final String GLOBAL_SETTINGS = "settings";
    // default repository section - end

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private ActiveFileSystemsFactory activeFileSystemsFactory;

    @Inject
    private GroupService groupService;

    @Inject
    private DeploymentManagerEntryPoint deploymentManager;

    @PostConstruct
    public void assertPlayground() {
        // TODO Setup default repository for jBPM-Workbench
        Repository jbpmWorkbenchRepository = repositoryService.getRepository( JBPM_WB_PLAYGROUND_ALIAS );
        if ( jbpmWorkbenchRepository == null ) {
            repositoryService.cloneRepository( JBPM_WB_PLAYGROUND_SCHEME,
                                               JBPM_WB_PLAYGROUND_ALIAS,
                                               JBPM_WB_PLAYGROUND_ORIGIN,
                                               JBPM_WB_PLAYGROUND_UID,
                                               JBPM_WB_PLAYGROUND_PWD );
            jbpmWorkbenchRepository = repositoryService.getRepository( JBPM_WB_PLAYGROUND_ALIAS );
        }

        // TODO Setup default repository for Drools-Workbench
        final Repository droolsWorkbenchRepository = repositoryService.getRepository( DROOLS_WB_PLAYGROUND_ALIAS );
        if ( droolsWorkbenchRepository == null ) {
            repositoryService.cloneRepository( DROOLS_WB_PLAYGROUND_SCHEME,
                                               DROOLS_WB_PLAYGROUND_ALIAS,
                                               DROOLS_WB_PLAYGROUND_ORIGIN,
                                               DROOLS_WB_PLAYGROUND_UID,
                                               DROOLS_WB_PLAYGROUND_PWD );
        }

        // TODO Setup mandatory properties for Drools-Workbench
        List<ConfigGroup> configGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
        boolean globalSettingsDefined = false;
        for ( ConfigGroup configGroup : configGroups ) {
            if ( GLOBAL_SETTINGS.equals( configGroup.getName() ) ) {
                globalSettingsDefined = true;
                break;
            }
        }
        if ( !globalSettingsDefined ) {
            configurationService.addConfiguration( getGlobalConfiguration() );
        }

        // TODO Setup properties required by the Work Items Editor
        List<ConfigGroup> editorConfigGroups = configurationService.getConfiguration( ConfigType.EDITOR );
        boolean workItemsEditorSettingsDefined = false;
        for ( ConfigGroup editorConfigGroup : editorConfigGroups ) {
            if ( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS.equals( editorConfigGroup.getName() ) ) {
                workItemsEditorSettingsDefined = true;
                break;
            }
        }
        if ( !workItemsEditorSettingsDefined ) {
            configurationService.addConfiguration( getWorkItemElementDefinitions() );
        }

        //Ensure FileSystems are loaded
        activeFileSystemsFactory.fileSystems();

        Set<DeploymentUnit> deploymentUnits = produceDeploymentUnits();
        ( (Initializable) deploymentManager ).initDeployments( deploymentUnits );
    }

    private ConfigGroup getGlobalConfiguration() {
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                       GLOBAL_SETTINGS,
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.dateformat",
                                                                 "dd-MMM-yyyy" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.datetimeformat",
                                                                 "dd-MMM-yyyy hh:mm:ss" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultlanguage",
                                                                 "en" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultcountry",
                                                                 "US" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                                                                 "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "rule-modeller-onlyShowDSLStatements",
                                                                 "false" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "designer.url",
                                                                  "http://localhost:8080" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "designer.context",
                                                                   "designer" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "designer.profile",
                                                                    "jbpm" ) );
        return group;
    }

    private ConfigGroup getWorkItemElementDefinitions() {
        // Work Item Definition elements used when creating Work Item Definitions.
        // Each entry in this file represents a Button in the Editor's Palette:-
        //   - Underscores ('_') in the key will be converted in whitespaces (' ') and
        //     will be used as Button's labels.
        //   - The value will be the text pasted into the editor when an element in the
        //     palette is selected. You can use a pipe ('|') to specify the place where
        //     the cursor should be put after pasting the element into the editor.
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.EDITOR,
                                                                       WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS,
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DEFINITION,
                                                                 "[\n" +
                                                                         "    \"name\" : \"MyTask|\", \n" +
                                                                         "    \"parameters\" : [ \n" +
                                                                         "        \"MyFirstParam\" : new StringDataType(), \n" +
                                                                         "        \"MySecondParam\" : new StringDataType(), \n" +
                                                                         "        \"MyThirdParam\" : new ObjectDataType() \n" +
                                                                         "    ], \n" +
                                                                         "    \"results\" : [ \n" +
                                                                         "        \"Result\" : new ObjectDataType(\"java.util.Map\") \n" +
                                                                         "    ], \n" +
                                                                         "    \"displayName\" : \"My Task\", \n" +
                                                                         "    \"icon\" : \"\" \n" +
                                                                         "]" ) );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_PARAMETER,
                                                                 "\"MyParam|\" : new StringDataType()" ) );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_RESULT,
                                                                 "\"Result|\" : new ObjectDataType()" ) );
        group.addConfigItem( configurationFactory.newConfigItem( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DISPLAY_NAME,
                                                                 "\"displayName\" : \"My Task|\"" ) );
        return group;
    }

    @Produces
    @RequestScoped
    public Set<DeploymentUnit> produceDeploymentUnits() {
        Set<DeploymentUnit> deploymentUnits = new HashSet<DeploymentUnit>();

        Collection<Repository> repositories = repositoryService.getRepositories();
        for ( Repository repository : repositories ) {

            Path directory = ioService.get( repository.getUri() + "/processes" );
            if ( ioService.exists( directory ) ) {
                Iterable<Path> assetDirectories = ioService.newDirectoryStream( directory, new DirectoryStream.Filter<Path>() {
                    @Override
                    public boolean accept( final Path entry ) {
                        if ( org.kie.commons.java.nio.file.Files.isDirectory( entry ) ) {
                            return true;
                        }
                        return false;
                    }
                } );

                for ( Path p : assetDirectories ) {
                    String folder = p.toString();
                    if ( folder.startsWith( "/" ) ) {
                        folder = folder.substring( 1 );
                    }
                    deploymentUnits.add( new VFSDeploymentUnit( p.getFileName().toString(), repository.getAlias(), folder ) );
                }
            }
        }

        return deploymentUnits;
    }
}
