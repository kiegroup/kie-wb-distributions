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
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

@Singleton
public class AppSetup {

    private static final String JBPM_REPO_PLAYGROUND = "jbpm-playground";
    private static final String GUVNOR_REPO_PLAYGROUND = "uf-playground";
    // default repository section - start
    private static final String JBPM_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    private static final String GUVNOR_URL      = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    private final String userName = "guvnorngtestuser1";
    private final String password = "test1234";
    // default repository section - end

    private final IOService ioService;
    private final IOSearchService ioSearchService;

    private FileSystem fs = null;
    private final LuceneSetup luceneSetup = new NIOLuceneSetup();

    @Inject
    private RepositoryService repositoryService;


    public AppSetup() {
        final MetaModelStore metaModelStore = new InMemoryMetaModelStore();
        final MetaIndexEngine indexEngine = new LuceneIndexEngine( metaModelStore,
                luceneSetup,
                new SimpleFieldFactory() );
        final SearchIndex searchIndex = new LuceneSearchIndex( luceneSetup );
        this.ioService = new IOServiceIndexedImpl( indexEngine,
                DublinCoreView.class,
                VersionAttributeView.class,
                OtherMetaView.class );
        this.ioSearchService = new IOSearchIndex( searchIndex,
                this.ioService );
    }

    @PostConstruct
    public void onStartup() {

        // TODO in case repo is not defined in system repository so we add default
        Repository jbpmRepo = repositoryService.getRepository(JBPM_REPO_PLAYGROUND);
        if(jbpmRepo == null) {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            repositoryService.cloneRepository("git", JBPM_REPO_PLAYGROUND, JBPM_URL, userName, password);
            jbpmRepo = repositoryService.getRepository(JBPM_REPO_PLAYGROUND);
        }
        try {
            fs = ioService.newFileSystem(URI.create(jbpmRepo.getUri()), jbpmRepo.getEnvironment());

        } catch (FileSystemAlreadyExistsException e) {
            fs = ioService.getFileSystem(URI.create(jbpmRepo.getUri()));

        }

        // TODO in case repo is not defined in system repository so we add default
        Repository guvnorRepo = repositoryService.getRepository(GUVNOR_REPO_PLAYGROUND);
        if(guvnorRepo == null) {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            repositoryService.cloneRepository("git", GUVNOR_REPO_PLAYGROUND, GUVNOR_URL, userName, password);
            guvnorRepo = repositoryService.getRepository(GUVNOR_REPO_PLAYGROUND);
        }
    }
    @PreDestroy
    private void cleanup() {
        luceneSetup.dispose();
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("ioSearchStrategy")
    public IOSearchService ioSearchService() {
        return ioSearchService;
    }


    @Produces
    @Named("fileSystem")
    public FileSystem fileSystem() {
        return fs;
    }


}
