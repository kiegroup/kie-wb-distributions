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
package org.kie.wb.selenium.model.persps;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.kie.wb.selenium.model.persps.authoring.ConflictingRepositoriesModal;
import org.kie.wb.selenium.model.persps.authoring.ImportExampleModal;
import org.kie.wb.selenium.util.BusyPopup;
import org.kie.wb.selenium.util.Repository;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectLibraryPerspective extends AbstractPerspective {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectLibraryPerspective.class);

    private static final By
            WELCOME_MESSAGE = By.id("welcome"),
            TEAM_BREADCRUMB = By.linkText("myteam"),
            IMPORT_PROJECT_BUTTON = By.id("import-project-button"),
            ADVANCED_IMPORT_BUTTON = By.linkText("Advanced Import"),
            BUILD_AND_DEPLOY_BUTTON = ByJQuery.selector("button:contains('Build & Deploy')");

    @Override
    public void waitForLoaded() {
        isDisplayed();
    }

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent(TEAM_BREADCRUMB,
                                      60);
    }

    private ImportExampleModal importExample() {
        click(IMPORT_PROJECT_BUTTON);
        click(ADVANCED_IMPORT_BUTTON);
        return ImportExampleModal.newInstance();
    }

    public boolean isProjectListEmpty() {
        return Waits.isElementPresent(WELCOME_MESSAGE);
    }

    public void openProjectList() {
        click(TEAM_BREADCRUMB);
    }

    public void importDemoProject(String projectName) {
        click(ByJQuery.selector("button:contains('" + projectName + "')"));
    }

    public void buildAndDeployProject() {
        click(BUILD_AND_DEPLOY_BUTTON);
        possiblyOverrideGavConflict();
    }

    public void importStockExampleProject(String targetRepo,
                                          String targetOrgUnit,
                                          String... projects) {
        ImportExampleModal modal = importExample();
        modal.selectStockRepository();
        modal.selectProjects(projects);
        modal.setTargetRepoAndOrgUnit(targetRepo,
                                      targetOrgUnit);
    }

    public void importCustomExampleProject(Repository repo,
                                           String targetRepo,
                                           String targetOrgUnit,
                                           String... projects) {
        ImportExampleModal modal = importExample();
        modal.selectCustomRepository(repo.getUrl());
        modal.selectProjects(projects);
        modal.setTargetRepoAndOrgUnit(targetRepo,
                                      targetOrgUnit);
    }

    private void possiblyOverrideGavConflict() {
        try {
            ConflictingRepositoriesModal modal = ConflictingRepositoriesModal.newInstance();
            modal.overrideArtifactInMavenRepo();
        } catch (TimeoutException | NoSuchElementException ignored) {
            LOG.info("Modal showing GAV conflict didn't appear");
        }
    }
}
