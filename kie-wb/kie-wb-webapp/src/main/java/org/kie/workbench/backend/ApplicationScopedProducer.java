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
import org.jbpm.kie.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.backend.server.io.IOSecurityAuth;
import org.uberfire.backend.server.io.IOSecurityAuthz;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.metadata.backend.lucene.LuceneIndexEngine;
import org.uberfire.metadata.backend.lucene.LuceneSearchIndex;
import org.uberfire.metadata.backend.lucene.LuceneSetup;
import org.uberfire.metadata.backend.lucene.fields.SimpleFieldFactory;
import org.uberfire.metadata.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.uberfire.metadata.backend.lucene.setups.NIOLuceneSetup;
import org.uberfire.metadata.engine.MetaIndexEngine;
import org.uberfire.metadata.engine.MetaModelStore;
import org.uberfire.metadata.io.IOSearchIndex;
import org.uberfire.metadata.io.IOServiceIndexedImpl;
import org.uberfire.metadata.search.SearchIndex;
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

    @Inject
    @IOSecurityAuth
    private AuthenticationManager authenticationManager;

    @Inject
    @IOSecurityAuthz
    private AuthorizationManager authorizationManager;


    private IOService ioService;
    private IOSearchService ioSearchService;
    private final LuceneSetup luceneSetup = new NIOLuceneSetup();

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    @Inject
    private IOWatchServiceNonDotImpl watchService;

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Inject
    @Selectable
    private UserGroupInfoProducer userGroupInfoProducer;

    public ApplicationScopedProducer() {
        if (System.getProperty("org.uberfire.watcher.autostart") == null) {
            System.setProperty("org.uberfire.watcher.autostart", "false");
        }
    }


    @PostConstruct
    public void setup() {
        SecurityFactory.setAuthzManager( new RuntimeAuthorizationManager() );

        final MetaModelStore metaModelStore = new InMemoryMetaModelStore();
        final MetaIndexEngine indexEngine = new LuceneIndexEngine( metaModelStore,
                                                                   luceneSetup,
                                                                   new SimpleFieldFactory() );
        final SearchIndex searchIndex = new LuceneSearchIndex( luceneSetup );

        final IOService service = new IOServiceIndexedImpl( watchService,
                                                            indexEngine,
                                                            DublinCoreView.class,
                                                            VersionAttributeView.class,
                                                            OtherMetaView.class );

        if ( clusterServiceFactory == null ) {
            ioService = service;
        } else {
            ioService = new IOServiceClusterImpl( service,
                                                  clusterServiceFactory,
                                                  false);
        }

        ioService.setAuthenticationManager( authenticationManager );
        ioService.setAuthorizationManager( authorizationManager );

        this.ioSearchService = new IOSearchIndex( searchIndex,
                                                  ioService );
    }

    @PreDestroy
    private void cleanup() {
        luceneSetup.dispose();
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
    public org.kie.api.task.UserGroupCallback produceSelectedUserGroupCalback() {
        return userGroupInfoProducer.produceCallback();
    }

    @Produces
    public UserInfo produceUserInfo() {
        return userGroupInfoProducer.produceUserInfo();
    }


    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if ( this.emf == null ) {
            // this needs to be here for non EE containers
            try {
                this.emf = InitialContext.doLookup( "jBPMEMF" );
            } catch ( NamingException e ) {
                this.emf = Persistence.createEntityManagerFactory( "org.jbpm.domain" );
            }

        }
        return this.emf;
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

    @Produces
    @ApplicationScoped
    public TaskLifeCycleEventListener produceBAMListener() {
        return new BAMTaskEventListener();
    }

    @Produces
    @ApplicationScoped
    public TaskLifeCycleEventListener produceTaskAuditListener() {
        return new JPATaskLifeCycleEventListener();
    }

}
