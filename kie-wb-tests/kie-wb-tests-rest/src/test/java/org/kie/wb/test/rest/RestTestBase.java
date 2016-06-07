/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.wb.test.rest;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.wb.test.rest.client.RestWorkbenchClient;
import org.kie.wb.test.rest.client.WorkbenchClient;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

public abstract class RestTestBase {

    private static final String URL = System.getProperty("kie.wb.url", "http://localhost:8080/kie-wb");
    protected static final String USER_ID = System.getProperty("kie.wb.user.name", User.REST_ALL.getUserName());
    private static final String PASSWORD = System.getProperty("kie.wb.user.password", User.REST_ALL.getPassword());

    protected static WorkbenchClient client;

    @BeforeClass
    public static void createWorkbenchClient() {
        client = RestWorkbenchClient.createWorkbenchClient(URL, USER_ID, PASSWORD);
    }

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            System.out.println(" >>> " + description.getMethodName() + " <<< ");
        }

        @Override
        protected void finished(Description description) {
            System.out.println();
        }

    };

    @Rule
    public IKeeperJUnitConnector issueKeeper = new IKeeperJUnitConnector(new JiraClient("https://issues.jboss.org"));

    protected static void deleteAllOrganizationalUnits() {
        client.getOrganizationalUnits().forEach(orgUnit -> client.deleteOrganizationalUnit(orgUnit.getName()));
    }

    protected static void deleteAllRepositories() {
        client.getRepositories().forEach(repository -> client.deleteRepository(repository.getName()));
    }

}
