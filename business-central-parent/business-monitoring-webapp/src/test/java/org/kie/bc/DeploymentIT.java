/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.bc;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class DeploymentIT {

    public static final String ARCHIVE_NAME = "wildfly.war";

    @Deployment(testable = false)
    public static WebArchive create() {
        final String warFile = System.getProperty(ARCHIVE_NAME);
        return ShrinkWrap.create(ZipImporter.class,
                                 warFile)
                .importFrom(new File("target/" + warFile))
                .as(WebArchive.class);
    }

    @Test
    @RunAsClient
    public void testDeployment(@ArquillianResource URL baseURL) throws Exception {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) baseURL.openConnection();
            assertEquals(200,
                         c.getResponseCode());
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
    }
}