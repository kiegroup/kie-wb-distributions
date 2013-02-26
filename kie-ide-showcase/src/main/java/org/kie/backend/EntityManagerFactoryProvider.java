package org.kie.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

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
}
