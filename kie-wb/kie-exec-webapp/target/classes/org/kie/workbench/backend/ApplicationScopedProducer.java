package org.kie.workbench.backend;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.io.IOSearchIndex;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.io.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

/**
 * This class should contain all ApplicationScoped producers
 * required by the application.
 */
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class ApplicationScopedProducer {

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

    @Inject
    private AuthenticationService authenticationService;

    public ApplicationScopedProducer() {
        if ( System.getProperty( "org.uberfire.watcher.autostart" ) == null ) {
            System.setProperty( "org.uberfire.watcher.autostart", "false" );
        }

        if ( System.getProperty( "org.kie.deployment.desc.location" ) == null ) {
            System.setProperty( "org.kie.deployment.desc.location", "classpath:META-INF/kie-wb-deployment-descriptor.xml" );
        }
    }

    @PostConstruct
    public void setup() {
        final IOService service = new IOServiceIndexedImpl( watchService,
                                                            config.getIndexEngine(),
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
    public AuthorizationManager getAuthManager() {
        return new RuntimeAuthorizationManager();
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        try {
            return authenticationService.getUser();
        } catch ( final IllegalStateException ex ) {
            return new UserImpl( "system" );
        }
    }

}
