/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kie.workbench.backend;

import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * This class should contain all RequestScoped producers
 * required by the application.
 */
@RequestScoped
public class RequestScopedProducer {

    @Inject
    private EntityManagerFactory emf;

    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        EntityManager em = emf.createEntityManager();
        return em;
    }

    @Produces
    public Logger createLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass()
                .getName());
    }
    
    public void close(@Disposes EntityManager em) {
        em.close();
    }

}
