/*
 * JBoss, Home of Professional Open Source
 * 
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.smoke.kie.wb.eap;

import static org.kie.smoke.kie.wb.base.util.TestConstants.PROJECT_VERSION;
import static org.kie.smoke.tests.util.DeployUtil.getWebArchive;

import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.smoke.kie.wb.base.AbstractKieWbSmokeIntegrationTest;

@RunAsClient
@RunWith(Arquillian.class)
public class JbossEapKieWbSmokeIntegrationTest extends AbstractKieWbSmokeIntegrationTest {

    @Deployment(testable = false, name = "kie-wb-eap")
    public static Archive<?> createWar() {
        logger.debug("Retrieving kie-wb eap6 war.");
        WebArchive war = getWebArchive("org.kie", "kie-wb-distribution-wars", "eap-6_1", PROJECT_VERSION);
        return war;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.APPLICATION_XML_TYPE;
    }

    @Override
    public boolean jmsQueuesAvailable() {
        return true;
    }

    @Override
    public RuntimeStrategy getStrategy() {
        return RuntimeStrategy.PER_PROCESS_INSTANCE;
    }

    @Override
    public int getTimeout() {
        return 1000;
    }

}