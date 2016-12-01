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

import org.kie.wb.selenium.model.persps.authoring.ImportExampleModal;
import org.kie.wb.selenium.util.Repository;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProjectLibraryPerspective extends AbstractPerspective {

    //<div class="full" data-field="emptyLibrary">
    private static final By PROJECT_LIBRARY_HOLDER = By.xpath( "//div[@data-field='emptyLibrary']" );

    @FindBy(xpath = "//button[@id='example']")
    private WebElement exampleButton;

    @Override
    public void waitForLoaded() {
        Waits.pause( 500 );
    }

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent( PROJECT_LIBRARY_HOLDER );
    }

    public void importExampleProject( final Repository repo,
                                      final String targetRepo,
                                      final String targetOrgUnit,
                                      final String... projects ) {
        ImportExampleModal modal = importExample();
        modal.selectRepo( repo.getUrl() );
        modal.selectProjects( projects );
        modal.setTargetRepoAndOrgUnit( targetRepo, targetOrgUnit );
    }

    private ImportExampleModal importExample() {
        exampleButton.click();
        return ImportExampleModal.newInstance();
    }

}
