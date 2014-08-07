package org.kie.workbench.backend;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.kie.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.audit.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.task.api.UserInfo;

public class BpmDependenciesProducer {

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
