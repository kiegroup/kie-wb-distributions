/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectLibraryPerspective extends AbstractPerspective {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectLibraryPerspective.class);

    private static final By WELCOME_MESSAGE_HOLDER = By.id("welcome");
    private static final By OU_REPO_BREADCRUMB = ByJQuery.selector("a:contains('myteam')");

    @Override
    public void waitForLoaded() {
        isDisplayed();
    }

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent(OU_REPO_BREADCRUMB,
                                      60);
    }

    private ImportExampleModal importExample() {
        BusyPopup.waitForDisappearance();
        Waits.elementPresent(ByJQuery.selector("button#import-project-button"),
                             40)
                .click();
        Waits.elementPresent(ByJQuery.selector("a:contains('Advanced Import')"),
                             5)
                .click();
        return ImportExampleModal.newInstance();
    }

    public boolean isProjectListEmpty() {
        return Waits.isElementPresent(WELCOME_MESSAGE_HOLDER);
    }

    public void openProjectList() {
        Waits.elementPresent(OU_REPO_BREADCRUMB,
                             20)
                .click();
    }

    public void importDemoProject(String projectName) {
        Waits.elementPresent(ByJQuery.selector("button:contains('" + projectName + "')"),
                             20)
                .click();
    }

    public void buildAndDeployProject() {
        Waits.elementPresent(ByJQuery.selector("button:contains('Build & Deploy')"),
                             20)
                .click();
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
