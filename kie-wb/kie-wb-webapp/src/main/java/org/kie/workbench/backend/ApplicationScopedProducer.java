package org.kie.workbench.backend;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.backend.server.io.IOSecurityAuth;
import org.uberfire.backend.server.io.IOSecurityAuthz;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.metadata.io.IOSearchIndex;
import org.uberfire.metadata.io.IOServiceIndexedImpl;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.security.server.cdi.SecurityFactory;

/**
 * This class should contain all ApplicationScoped producers
 * required by the application.
 */
@ApplicationScoped
public class ApplicationScopedProducer {

    /**
     * If the EMF is not produced by an @ApplicationScoped bean, 
     * then tomcat will create an emf *per request*, leading to a memory leak
     */
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if ( this.emf == null ) {
            // this needs to be here for non EE containers
            try {
                this.emf = InitialContext.doLookup("jBPMEMF");
            } catch ( NamingException e ) {
                this.emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
            }

        }
        return this.emf;
    }
    
    @Inject
    @IOSecurityAuth
    private AuthenticationManager authenticationManager;

    @Inject
    @IOSecurityAuthz
    private AuthorizationManager authorizationManager;

    @Inject
    @Named("luceneConfig")
    private LuceneConfig config;

    private IOService ioService;
    private IOSearchService ioSearchService;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    @Inject
    private IOWatchServiceNonDotImpl watchService;

    public ApplicationScopedProducer() {
        if ( System.getProperty( "org.uberfire.watcher.autostart" ) == null ) {
            System.setProperty( "org.uberfire.watcher.autostart", "false" );
        }
    }

    @PostConstruct
    public void setup() {
        SecurityFactory.setAuthzManager( new RuntimeAuthorizationManager() );

        this.config = new LuceneConfigBuilder().withInMemoryMetaModelStore()
                .useDirectoryBasedIndex()
                .useNIODirectory()
                .build();

        final IOService service = new IOServiceIndexedImpl( watchService,
                                                            config.getIndexEngine(),
                                                            config.getIndexers(),
                                                            DublinCoreView.class,
                                                            VersionAttributeView.class,
                                                            OtherMetaView.class );

        if ( clusterServiceFactory == null ) {
            ioService = service;
        } else {
            ioService = new IOServiceClusterImpl( service,
                                                  clusterServiceFactory,
                                                  false );
        }

        ioService.setAuthenticationManager( authenticationManager );
        ioService.setAuthorizationManager( authorizationManager );

        this.ioSearchService = new IOSearchIndex( config.getSearchIndex(),
                                                  ioService );
    }

    @PreDestroy
    private void cleanup() {
        config.dispose();
        ioService.dispose();
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
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment( EntityManagerFactory emf ) {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment( emf );
        Properties properties = new Properties();
        environment.setUserGroupCallback( new JBossUserGroupCallbackImpl( properties ) );
        return environment;
    }



}
