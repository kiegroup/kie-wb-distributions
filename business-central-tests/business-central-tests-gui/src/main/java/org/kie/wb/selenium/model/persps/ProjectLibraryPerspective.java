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
import org.kie.wb.selenium.model.persps.authoring.ImportProjectsScreen;
import org.kie.wb.selenium.model.persps.authoring.ImportRepositoryModal;
import org.kie.wb.selenium.util.Repository;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectLibraryPerspective extends AbstractPerspective {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectLibraryPerspective.class);

    private static final By
            SPACES_BREADCRUMB = ByJQuery.linkText("Spaces"),
            PROJECT_ACTIONS_BUTTON = By.id("add-project-dropdown"),
            IMPORT_PROJECT_ACTION_BUTTON = By.linkText("Import Project"),
            TRY_SAMPLES_ACTION_BUTTON = By.linkText("Try Samples"),
            IMPORT_PROJECT_BUTTON = By.xpath("//button[@data-field='import-project']"),
            TRY_SAMPLES_BUTTON = By.xpath("//button[@data-field='try-samples']"),
            DEPLOY_BUTTON = By.id("deploy"),
            DEFAULT_SPACE_CARD = By.className("card-pf-view-single-select"),
            PROJECTS_LOADING = By.xpath("//div[contains(@class, 'spinner')]");

    @Override
    public void waitForLoaded() {
        isDisplayed();
    }

    @Override
    public boolean isDisplayed() {
        Waits.elementPresent(PROJECTS_LOADING, 60);
        Waits.elementInvisible(PROJECTS_LOADING, 60);
        return Waits.elementClickable(SPACES_BREADCRUMB, 60) != null;
    }

    private ImportRepositoryModal importProject() {
        if (Waits.isElementPresent(IMPORT_PROJECT_BUTTON)) {
            click(IMPORT_PROJECT_BUTTON);
        } else {
            click(PROJECT_ACTIONS_BUTTON);
            click(IMPORT_PROJECT_ACTION_BUTTON);
        }
        return ImportRepositoryModal.newInstance();
    }

    private ImportProjectsScreen trySamples() {
        if (Waits.isElementPresent(TRY_SAMPLES_BUTTON)) {
            click(TRY_SAMPLES_BUTTON);
        } else {
            click(PROJECT_ACTIONS_BUTTON);
            click(TRY_SAMPLES_ACTION_BUTTON);
        }

        return ImportProjectsScreen.newInstance();
    }

    public void enterDefaultSpace() {
        click(DEFAULT_SPACE_CARD);
    }

    public void buildAndDeployProject() {
        click(DEPLOY_BUTTON);
        possiblyOverrideGavConflict();
    }

    public void importStockExampleProject(String... projects) {
        ImportProjectsScreen importProjectsScreen = trySamples();
        importProjectsScreen
                .selectProjects(projects)
                .ok();
    }

    public void importCustomExampleProject(Repository repo, String... projects) {
        ImportRepositoryModal modal = importProject();
        modal.selectCustomRepository(repo.getUrl());

        ImportProjectsScreen importProjectsScreen = modal.importProjects();
        importProjectsScreen
                .selectProjects(projects)
                .ok();
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
