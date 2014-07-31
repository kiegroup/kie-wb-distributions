package org.kie.workbench.drools.backend.server.weblogic;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

public class SwapClassloaderExtension implements Extension {

    private ClassLoader tccl;

    public void beforeBeanDiscovery(@Observes javax.enterprise.inject.spi.BeforeBeanDiscovery bbd) {
        this.tccl = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

    }

    public void afterDeploymentValidation( final @Observes AfterDeploymentValidation event, final BeanManager manager ) {
        Thread.currentThread().setContextClassLoader(tccl);
    }
}
