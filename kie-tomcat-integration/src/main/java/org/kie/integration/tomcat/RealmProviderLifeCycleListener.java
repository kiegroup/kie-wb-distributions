package org.kie.integration.tomcat;

import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class RealmProviderLifeCycleListener implements LifecycleListener {

    @Override
    public void lifecycleEvent(LifecycleEvent lifecycleEvent) {
        Lifecycle lifecycle = lifecycleEvent.getLifecycle();

        if (Lifecycle.AFTER_START_EVENT.equals(lifecycleEvent.getType())) {
            if (lifecycle instanceof Container) {
                TomcatRealmLoginModule.setRealm(((Container) lifecycle).getRealm());
            }
        }
    }
}
