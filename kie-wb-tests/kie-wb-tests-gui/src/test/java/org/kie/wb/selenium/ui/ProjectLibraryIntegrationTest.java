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

import org.junit.Before;
import org.junit.Test;
import org.kie.wb.selenium.model.KieSeleniumTest;
import org.kie.wb.selenium.model.persps.ArtifactRepositoryPerspective;
import org.kie.wb.selenium.model.persps.ProjectLibraryPerspective;
import org.kie.wb.selenium.util.Repository;
import org.kie.wb.selenium.util.Waits;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectLibraryIntegrationTest extends KieSeleniumTest {

    private ProjectLibraryPerspective projectLibrary;

    @Before
    public void setUp() {
        login.get();
        if (login.isDisplayed()) {
            login.loginDefaultUser();
        }

        projectLibrary = home.getNavbar().projects();
    }

    @Test
    public void importAndBuildProjectFromStockRepository() {
        final String
                projectName = "OptaCloud",
                projectGav = "optacloud:optacloud:1.0.0-SNAPSHOT";

        importBuildDeployAndCheckArtifact(
                projectName,
                projectGav,
                () -> projectLibrary.importStockExampleProject(projectName)
        );
    }

    @Test
    public void importAndBuildProjectFromCustomRepository() {
        final String
                projectName = "Evaluation",
                projectGav = "org.jbpm:Evaluation:1.0";

        importBuildDeployAndCheckArtifact(
                projectName,
                projectGav,
                () -> projectLibrary.importCustomExampleProject(Repository.JBPM_PLAYGROUND, projectName)
        );
    }

    private void importBuildDeployAndCheckArtifact(String projectName, String projectGav, Runnable stepsToImportProject) {
        stepsToImportProject.run();
        deployAndCheckArtifact(projectGav);
        // Important: don't put logout in @After, because ScreenshotOnFailure captures screenshots after @After,
        // which results in useless image of login screen
        home.logout();
    }

    private void deployAndCheckArtifact(String artifact) {
        projectLibrary.buildAndDeployProject();
        //Wait for project to build/deploy and appear in artifact repository perspective
        Waits.pause(10_000);
        ArtifactRepositoryPerspective artifactRepo = projectLibrary.getNavbar().admin().artifactRepository();
        assertThat(artifactRepo.isArtifactPresent(artifact))
                .as("Project artifact should be present after Build & Deploy").isTrue();
    }
}