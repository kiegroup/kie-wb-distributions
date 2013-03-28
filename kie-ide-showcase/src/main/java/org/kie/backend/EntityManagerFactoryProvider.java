package org.kie.backend;

import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;

@ApplicationScoped
public class EntityManagerFactoryProvider {
    
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;
    
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            // this needs to be here for non EE containers
            try {
                this.emf = InitialContext.doLookup("jBPMEMF");
            } catch (NamingException e) {
                this.emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
            }
            
        }
        return this.emf;
    }

    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment(emf);
        Properties properties= new Properties();
        environment.setUserGroupCallback( new JBossUserGroupCallbackImpl(properties));
        return environment;
    }
}
