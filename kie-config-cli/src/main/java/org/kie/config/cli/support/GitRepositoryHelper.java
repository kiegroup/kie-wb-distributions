/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.config.cli.support;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.DefaultPublicURI;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.config.SecureConfigItem;
import org.guvnor.structure.server.repositories.RepositoryFactoryHelper;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.structure.repositories.impl.git.GitRepository.*;
import static org.uberfire.backend.server.util.Paths.*;
import static org.uberfire.commons.validation.Preconditions.*;

@ApplicationScoped
public class GitRepositoryHelper implements RepositoryFactoryHelper {

    private IOService ioService;

    @Inject
    private PasswordService secureService;

    @Inject
    private GitRepositoryHelperContext context;

    public GitRepositoryHelper() {
    }

    @Inject
    public GitRepositoryHelper( @Named("ioStrategy") IOService ioService ) {
        this.ioService = ioService;
    }

    @Override
    public boolean accept( final ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig", repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem", schemeConfigItem );
        return SCHEME.equals( schemeConfigItem.getValue() );
    }

    @Override
    public Repository newRepository( final ConfigGroup repoConfig ) {
        validate( repoConfig );

        String branch = repoConfig.getConfigItemValue( EnvironmentParameters.BRANCH );
        if ( branch == null ) {
            branch = "master";
        }

        return newRepository( repoConfig, branch );
    }

    public Repository newRepository( ConfigGroup repoConfig,
                                     String branch ) {
        validate( repoConfig );
        checkNotNull( "branch", branch );

        final GitRepository repo = new GitRepository( repoConfig.getName() );

        for ( final ConfigItem item : repoConfig.getItems() ) {
            if ( item instanceof SecureConfigItem ) {
                repo.addEnvironmentParameter( item.getName(), secureService.decrypt( item.getValue().toString() ) );
            } else {
                repo.addEnvironmentParameter( item.getName(), item.getValue() );
            }
        }

        if ( !repo.isValid() ) {
            throw new IllegalStateException( "Repository " + repoConfig.getName() + " not valid" );
        }

        //If the Repository was created by kie-config-cli then we cannot override the origin to point to the workbench.
        //Repositories that were created within the workbench need to have their origin overridden so that the instance of VFS
        //running within kie-config-cli clones from the workbench and not the original remote origin. This ensures changes made
        //to Repositories within the workbench are visible to kie-config-cli.
        final Object command = repo.getEnvironment().get( "org.kie.config.cli.command.CliCommand" );
        if ( command != null ) {
            //The Repository was created using kie-config-cli and hence cannot be cloned from the workbench
            repo.getEnvironment().put( "init", true );
        } else {
            //Override with a workbench URL
            final String url = context.getScheme() + "://" + context.getUser() + "@" + context.getHost() + ":" + context.getPort() + "/" + repo.getAlias();
            repo.getEnvironment().put( "origin",
                                       url );
            repo.getEnvironment().put( "username",
                                       context.getUser() );
            repo.getEnvironment().put( "password",
                                       context.getPassword() );
        }

        FileSystem fs = null;
        URI uri = null;
        try {
            uri = URI.create( repo.getUri() );
            fs = ioService.newFileSystem( uri, new HashMap<String, Object>( repo.getEnvironment() ) );
        } catch ( final FileSystemAlreadyExistsException e ) {
            fs = ioService.getFileSystem( uri );
        } catch ( final Throwable ex ) {
            throw new RuntimeException( ex.getCause().getMessage(), ex );
        }

        org.uberfire.backend.vfs.Path defaultRoot = convert( fs.getRootDirectories().iterator().next() );
        Map<String, org.uberfire.backend.vfs.Path> branches = getBranches( fs );
        if ( branches.containsKey( branch ) ) {
            defaultRoot = branches.get( branch );
        }
        repo.setBranches( branches );

        repo.setRoot( defaultRoot );

        repo.changeBranch( branch );

        final String[] uris = fs.toString().split( "\\r?\\n" );
        final List<PublicURI> publicURIs = new ArrayList<PublicURI>( uris.length );

        for ( final String s : uris ) {
            final int protocolStart = s.indexOf( "://" );
            final PublicURI publicURI;
            if ( protocolStart > 0 ) {
                publicURI = new DefaultPublicURI( s.substring( 0, protocolStart ), s );
            } else {
                publicURI = new DefaultPublicURI( s );
            }
            publicURIs.add( publicURI );
        }
        repo.setPublicURIs( publicURIs );

        return repo;
    }

    /**
     * collect all branches
     * @param fs
     * @return
     */
    private Map<String, org.uberfire.backend.vfs.Path> getBranches( FileSystem fs ) {
        Map<String, org.uberfire.backend.vfs.Path> branches = new HashMap<String, org.uberfire.backend.vfs.Path>();
        for ( final Path path : fs.getRootDirectories() ) {
            String gitBranch = getBranchName( path );
            branches.put( gitBranch, convert( path ) );
        }
        return branches;
    }

    protected String getBranchName( final Path path ) {
        URI uri = path.toUri();
        String gitBranch = uri.getAuthority();

        if ( gitBranch.indexOf( "@" ) != -1 ) {
            return gitBranch.split( "@" )[ 0 ];
        }

        return gitBranch;
    }

    private void validate( ConfigGroup repoConfig ) {
        checkNotNull( "repoConfig", repoConfig );
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem( EnvironmentParameters.SCHEME );
        checkNotNull( "schemeConfigItem", schemeConfigItem );
    }
}
