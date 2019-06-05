/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.wb.test.websocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.kie.wb.test.rest.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class WebSocketAccessIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketAccessIntegrationTest.class);
    private static final String CONTROLLER_URL = System.getProperty("kie.controller.url", "ws://localhost:8080/business-central/websocket/controller");

    private User user;
    private KieServerControllerClient client;

    public WebSocketAccessIntegrationTest(User user) {
        this.user = user;
    }

    @Parameterized.Parameters(name = "user: {0}")
    public static Collection<Object[]> remoteController() {
        return Arrays.asList(new Object[][]{{User.NO_REST}, {User.REST_ALL}});
    }

    @After
    public void close() {
        if (client != null) {
            try {
                LOGGER.info("Closing Kie Server Management Controller client");
                client.close();
            } catch (IOException e) {
                LOGGER.warn("Error trying to close Kie Server Management Controller Client: {}", e.getMessage(), e);
            }
        }
    }

    @Test
    public void testControllerConnection() {
        if (user.isAuthorized()) {
            client = KieServerControllerClientFactory.newWebSocketClient(CONTROLLER_URL, user.getUserName(), user.getPassword());
        } else {
            try {
                client = KieServerControllerClientFactory.newWebSocketClient(CONTROLLER_URL, user.getUserName(), user.getPassword());
                fail("Operation should have failed");
            } catch (Exception ex) {
                assertThat(ex).hasMessageContaining("403");
            }
        }
    }
}
