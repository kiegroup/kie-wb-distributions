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

package org.kie.wb.test.rest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.wb.test.rest.client.ClientRequestTimedOutException;
import org.kie.wb.test.rest.client.RestWorkbenchClient;
import org.kie.wb.test.rest.client.WorkbenchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

public abstract class RestTestBase {

    protected static final String GIT_PORT = System.getProperty("org.uberfire.nio.git.daemon.port", "9418");
    protected static final String URL = System.getProperty("kie.wb.url", "http://localhost:8080/kie-wb");
    protected static final String USER_ID = System.getProperty("kie.wb.user.name", User.REST_ALL.getUserName());
    protected static final String PASSWORD = System.getProperty("kie.wb.user.password", User.REST_ALL.getPassword());

    private static Logger log = LoggerFactory.getLogger(RestTestBase.class);
    private static final StopWatch stopWatch = new StopWatch();

    protected static WorkbenchClient client;
    private static File gitRepository;

    @BeforeClass
    public static void createWorkbenchClient() {
        client = RestWorkbenchClient.createWorkbenchClient(URL, USER_ID, PASSWORD);
    }

    @BeforeClass
    public static void createGitRepository() throws GitAPIException, IOException {
        gitRepository = new File(System.getProperty("user.dir"), "target/git-repository/");
        Git git = Git.init().setDirectory(gitRepository).call();

        URL pomUrl = RestTestBase.class.getResource("/pom.xml");
        File pomFile = new File(gitRepository, "pom.xml");
        FileUtils.copyURLToFile(pomUrl, pomFile);

        git.add().addFilepattern("pom.xml").call();
        git.commit().setMessage("Add pom.xml").call();
    }

    @AfterClass
    public static void cleanUp() throws IOException {
        try {
            deleteAllSpaces();
        } catch (ClientRequestTimedOutException timeoutException) {
            throw new RuntimeException(String.format(
                    "Test cleanup (deletion of spaces) failed because it took more than %d seconds to delete a space." +
                            " See https://issues.jboss.org/browse/AF-1310", timeoutException.getTimeout()), timeoutException);
        }

        if (gitRepository != null) {
            FileUtils.deleteDirectory(gitRepository);
        }
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

    protected static SpaceRequest createSpace(String name) {
        Space orgUnit = new Space();
        orgUnit.setName(name);
        orgUnit.setOwner(USER_ID);
        return client.createSpace(orgUnit);
    }

    protected static CreateProjectJobRequest createNewProject(String spaceName, String projectName, String groupId, String version) {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId(groupId);
        createProjectRequest.setVersion(version);
        createProjectRequest.setName(projectName);
        return client.createProject(spaceName, createProjectRequest);
    }

    protected static void deleteAllSpaces() {
        Collection<Space> spaces = client.getSpaces();
        log.info("Deleting {} spaces", spaces.size());
        spaces.forEach(space -> {
            stopWatch.reset();
            stopWatch.start();
            try {
                client.deleteSpace(space.getName());
            } finally {
                stopWatch.stop();
                log.debug("Deleting space '{}' took {} ms", space.getName(), stopWatch.getTime());
            }
        });
    }

    protected static String getLocalGitRepositoryUrl() {
        return "file://" + gitRepository.getAbsolutePath();
    }
}
