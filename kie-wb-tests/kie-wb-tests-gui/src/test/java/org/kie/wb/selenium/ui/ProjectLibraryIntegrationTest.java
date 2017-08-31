/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.wb.selenium.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.wb.selenium.model.KieSeleniumTest;
import org.kie.wb.selenium.model.persps.ArtifactRepositoryPerspective;
import org.kie.wb.selenium.model.persps.HomePerspective;
import org.kie.wb.selenium.model.persps.ProjectLibraryPerspective;
import org.kie.wb.selenium.util.Repository;
import org.kie.wb.selenium.util.Waits;

import static org.assertj.core.api.Assertions.*;

public class ProjectLibraryIntegrationTest extends KieSeleniumTest {

    private static final String MORTGAGES_PROJECT = "mortgages";

    private HomePerspective home;
    private ProjectLibraryPerspective projectLibrary;

    @Before
    public void setUp() {
        login.getLoginPage();
        if (login.isDisplayed()){
            home = login.loginDefaultUser();
        }

        projectLibrary = home.getNavbar().projectAuthoring();

        if (projectLibrary.isProjectListEmpty()){
            ProjectLibraryPerspective library = home.getNavbar().projectAuthoring();
            library.importDemoProject(MORTGAGES_PROJECT);
            Waits.pause(5_000);
            projectLibrary.openProjectList();
        }
    }

    @After
    public void cleanUp(){
        home.logout();
    }

    @Test
    public void importAndBuildProjectFromStockRepository() {
        projectLibrary
                .importStockExampleProject("MyRepo", "optacloud");
        deployAndCheckArtifact("optacloud:optacloud:1.0.0-SNAPSHOT");
    }

    @Test
    public void importAndBuildProjectFromCustomRepository() {
        projectLibrary
                .importCustomExampleProject(Repository.JBPM_PLAYGROUND, "MyRepo",  "Evaluation");
        deployAndCheckArtifact("org.jbpm:Evaluation:1.0");
    }

    private void deployAndCheckArtifact(String artifact) {
        projectLibrary.buildAndDeployProject();
        Waits.pause(10_000); //Wait for project to build/deploy and appear in artifact repository perspective

        ArtifactRepositoryPerspective artifactRepo = projectLibrary.getNavbar().admin().artifactRepository();
        assertThat(artifactRepo.isArtifactPresent(artifact))
                .as("Project artifact should be present after Build & Deploy").isTrue();
    }
}