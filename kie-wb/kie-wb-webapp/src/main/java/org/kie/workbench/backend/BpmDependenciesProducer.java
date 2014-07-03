package org.kie.workbench.backend;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.kie.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.task.api.UserInfo;

public class BpmDependenciesProducer {

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if ( this.emf == null ) {
            // this needs to be here for non EE containers
            try {
                this.emf = InitialContext.doLookup("jBPMEMF");
            } catch ( NamingException e ) {
                this.emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
            }

        }
        return this.emf;
    }

    @Inject
    @Selectable
    private UserGroupInfoProducer userGroupInfoProducer;

    @Produces
    @Named("BAM")
    public TaskLifeCycleEventListener produceBAMListener() {
        return new BAMTaskEventListener();
    }

    @Produces
    @Named("Logs")
    public TaskLifeCycleEventListener produceTaskAuditListener() {
        return new JPATaskLifeCycleEventListener();
    }


    @Produces
    public org.kie.api.task.UserGroupCallback produceSelectedUserGroupCalback() {
        return userGroupInfoProducer.produceCallback();
    }

    @Produces
    public UserInfo produceUserInfo() {
        return userGroupInfoProducer.produceUserInfo();
    }
}
