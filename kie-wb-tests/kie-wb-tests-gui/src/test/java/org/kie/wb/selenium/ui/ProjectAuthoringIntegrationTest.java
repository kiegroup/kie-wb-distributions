/*
 * Copyright 2016 JBoss by Red Hat.
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
import org.kie.wb.selenium.model.persps.ProjectAuthoringPerspective;
import org.kie.wb.selenium.model.persps.ProjectLibraryPerspective;
import org.kie.wb.selenium.model.persps.authoring.ProjectEditor;
import org.kie.wb.selenium.util.Repository;
import org.kie.wb.selenium.util.Waits;

import static org.assertj.core.api.Assertions.*;

public class ProjectAuthoringIntegrationTest extends KieSeleniumTest {

    private static final String MORTGAGES_PROJECT = "mortgages";

    private HomePerspective home;
    private ProjectAuthoringPerspective projectAuthoring;

    @Before
    public void setUp() {
        login.getLoginPage();
        if(login.isDisplayed()){
            home = login.loginDefaultUser();
        }

        projectAuthoring = home.getNavbar().projectAuthoring();

        if(projectAuthoring.isAuthoringDisabled()){
            ProjectLibraryPerspective library = home.getNavbar().projectLibrary();
            library.importDemoProject(MORTGAGES_PROJECT);
            Waits.pause(5_000);
            projectAuthoring = home.getNavbar().projectAuthoring();
        }
    }

    @After
    public void cleanUp(){
        home.logout();
    }

    @Test
    public void importAndBuildProjectFromStockRepository() {
        projectAuthoring
                .importStockExampleProject("MyRepo", "MyOrgUnit", "optacloud");
        deployAndCheckArtifact("optacloud:optacloud:1.0.0-SNAPSHOT");
    }

    @Test
    public void importAndBuildProjectFromCustomRepository() {
        projectAuthoring
                .importCustomExampleProject(Repository.JBPM_PLAYGROUND, "MyRepo", "MyOrgUnit", "Evaluation");
        deployAndCheckArtifact("org.jbpm:Evaluation:1.0");
    }

    private void deployAndCheckArtifact(String artifact) {
        ProjectEditor pe = projectAuthoring.openProjectEditor();
        pe.buildAndDeploy();
        Waits.pause(10_000); //Wait for project to build/deploy and appear in artifact repository perspective

        ArtifactRepositoryPerspective artifactRepo = projectAuthoring.getNavbar().artifactRepository();
        assertThat(artifactRepo.isArtifactPresent(artifact))
                .as("Project artifact should be present after Build & Deploy").isTrue();
    }
}