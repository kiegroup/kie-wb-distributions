package org.kie.workbench.backend;

import java.util.Properties;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.commons.io.IOSearchService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.attribute.DublinCoreView;
import org.kie.commons.java.nio.base.version.VersionAttributeView;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.kieora.backend.lucene.LuceneIndexEngine;
import org.kie.kieora.backend.lucene.LuceneSearchIndex;
import org.kie.kieora.backend.lucene.LuceneSetup;
import org.kie.kieora.backend.lucene.fields.SimpleFieldFactory;
import org.kie.kieora.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.kie.kieora.backend.lucene.setups.NIOLuceneSetup;
import org.kie.kieora.engine.MetaIndexEngine;
import org.kie.kieora.engine.MetaModelStore;
import org.kie.kieora.io.IOSearchIndex;
import org.kie.kieora.io.IOServiceIndexedImpl;
import org.kie.kieora.search.SearchIndex;
import org.kie.workbench.common.services.backend.metadata.attribute.OtherMetaView;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.DefaultSystemRepository;

/**
 * This class should contain all ApplicationScoped producers
 * required by the application.
 */
@ApplicationScoped
public class ApplicationScopedProducer {

    private final IOService ioService;
    private final IOSearchService ioSearchService;
    private final LuceneSetup luceneSetup = new NIOLuceneSetup();

    private final DefaultSystemRepository systemRepository = new DefaultSystemRepository();

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Inject
    @Selectable
    private UserGroupCallback userGroupCallback;

    public ApplicationScopedProducer() {
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

    @PreDestroy
    private void cleanup() {
        luceneSetup.dispose();
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return systemRepository;
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
    public UserGroupCallback produceSelectedUserGroupCallback() {
        return userGroupCallback;
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
    public Logger createLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass()
                .getName());
    }

}
