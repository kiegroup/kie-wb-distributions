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

import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;

@Singleton
public class AppSetup {

    private final IOService         ioService         = new IOServiceDotFileImpl();
    private final ActiveFileSystems activeFileSystems = new ActiveFileSystemsImpl();
    
    private static final String JBPM_REPO_PLAYGROUND = "git://jbpm-playground";
    
    private static final String JBPM_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    
    private static final String GUVNOR_URL      = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    
    private static final String GUVNOR_REPO_PLAYGROUND = "git://uf-playground";
    
    private final String userName = "guvnorngtestuser1";
    private final String password = "test1234";
    
    private FileSystem fsJBPM = null;
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

        activeFileSystems.addBootstrapFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
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
        
        activeFileSystems.addBootstrapFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
            put( JBPM_REPO_PLAYGROUND, "jbpm-playground" );
        }}, fsJBPM.supportedFileAttributeViews() ) );

        
    }
    
    // @PreDestroy -> ds.close();???

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

}