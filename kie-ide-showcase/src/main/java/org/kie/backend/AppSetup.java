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
package org.kie.backend;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.io.IOSearchService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.attribute.DublinCoreView;
import org.kie.commons.java.nio.base.version.VersionAttributeView;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.guvnor.services.backend.metadata.attribute.OtherMetaView;
import org.kie.guvnor.services.config.AppConfigService;
import org.kie.guvnor.services.repositories.RepositoryService;
import org.kie.kieora.backend.lucene.LuceneIndexEngine;
import org.kie.kieora.backend.lucene.LuceneSearchIndex;
import org.kie.kieora.backend.lucene.LuceneSetup;
import org.kie.kieora.backend.lucene.fields.SimpleFieldFactory;
import org.kie.kieora.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.kie.kieora.backend.lucene.setups.NIOLuceneSetup;
import org.kie.kieora.engine.MetaIndexEngine;
import org.kie.kieora.engine.MetaModelStore;
import org.kie.kieora.search.SearchIndex;
import org.kie.kieora.io.IOSearchIndex;
import org.kie.kieora.io.IOServiceIndexedImpl;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;

@Singleton
public class AppSetup {

    private final IOSearchService   ioSearchService;
    private final LuceneSetup luceneSetup;
    private final RepositoryService repositoryService;
    private final AppConfigService  appConfigService;
    private final IOService         ioService;
    private final ActiveFileSystems activeFileSystems = new ActiveFileSystemsImpl();
    
    private static final String JBPM_REPO_PLAYGROUND = "git://jbpm-playground";
    
    private static final String JBPM_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    
    private static final String GUVNOR_URL      = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    
    private static final String GUVNOR_REPO_PLAYGROUND = "git://uf-playground";
    
    private final String userName = "guvnorngtestuser1";
    private final String password = "test1234";
    
    private FileSystem fsJBPM = null;

    @Inject
    public AppSetup( final RepositoryService repositoryService, final AppConfigService appConfigService ) {
        this.repositoryService = repositoryService;
        this.appConfigService = appConfigService;

        this.luceneSetup = new NIOLuceneSetup();
        final MetaModelStore metaModelStore = new InMemoryMetaModelStore();
        final MetaIndexEngine indexEngine = new LuceneIndexEngine( metaModelStore, luceneSetup, new SimpleFieldFactory() );
        final SearchIndex searchIndex = new LuceneSearchIndex( luceneSetup );
        this.ioService = new IOServiceIndexedImpl( indexEngine, DublinCoreView.class, VersionAttributeView.class, OtherMetaView.class );
        this.ioSearchService = new IOSearchIndex( searchIndex, this.ioService );
    }

    @PreDestroy
    private void cleanup() {
        luceneSetup.dispose();
    }

    @PostConstruct
    public void onStartup() {
        
        
        final URI fsURI = URI.create( GUVNOR_REPO_PLAYGROUND );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( "username", userName );
            put( "password", password );
            put( "origin", GUVNOR_URL );
        }};

        FileSystem fs = null;

        try {
            fs = ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
        } catch ( FileSystemAlreadyExistsException ex ) {
            fs = ioService.getFileSystem( fsURI );
        }

        activeFileSystems.addFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
            put( GUVNOR_REPO_PLAYGROUND, "uf-playground" );
        }}, fs.supportedFileAttributeViews() ) );
        
        
        
        final URI jBPMfsURI = URI.create(JBPM_REPO_PLAYGROUND);
        try {

            final Map<String, Object> jBPMEnv = new HashMap<String, Object>();
            jBPMEnv.put( "username", userName );
            jBPMEnv.put( "password", password );
            jBPMEnv.put( "origin", JBPM_URL );
            fsJBPM = ioService.newFileSystem( jBPMfsURI, jBPMEnv, BOOTSTRAP_INSTANCE );
        } catch ( FileSystemAlreadyExistsException ex ) {
            fsJBPM = ioService.getFileSystem( jBPMfsURI );
        }
        
        activeFileSystems.addFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
            put( JBPM_REPO_PLAYGROUND, "jbpm-playground" );
        }}, fsJBPM.supportedFileAttributeViews() ) );

        
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("fs")
    public ActiveFileSystems fileSystems() {
        return activeFileSystems;
    }

    @Produces
    @Named("fileSystem")
    public FileSystem fileSystem() {
        return fsJBPM;
    }

    @Produces
    @Named("ioSearchStrategy")
    public IOSearchService ioSearchService() {
        return ioSearchService;
    }

}
