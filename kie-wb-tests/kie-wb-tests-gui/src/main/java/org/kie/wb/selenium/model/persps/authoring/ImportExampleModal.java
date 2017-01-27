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
package org.kie.wb.selenium.model.persps.authoring;

import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.kie.wb.selenium.model.widgets.ModalDialog;
import org.kie.wb.selenium.util.ByUtil;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.kie.wb.selenium.util.ByUtil.jquery;

public class ImportExampleModal extends ModalDialog {

    @FindByJQuery( "#stockRadio" )
    private WebElement stockRepositoryRadioOption;

    @FindByJQuery( "#customRadio" )
    private WebElement customRepositoryRadioOption;

    @FindByJQuery("#repositoryUrlInput")
    private WebElement repoUrlInput;

    @FindByJQuery("#targetRepositoryTextBox")
    private WebElement targetRepoInput;

    @FindByJQuery("#organizationalUnitsDropdown > input")
    private WebElement targetOrgUnit;

    public static ImportExampleModal newInstance() {
        if ( Waits.isElementPresent( jquery( ".modal-content:has(.modal-header:contains('ExamplesWizard'))", 40 ) ) ) {
            return ModalDialog.newInstance( ImportExampleModal.class, "ExamplesWizard" );
        }

        return ModalDialog.newInstance( ImportExampleModal.class, "Import Example" );
    }

    public void selectStockRepository() {
        stockRepositoryRadioOption.click();
        next();
        Waits.elementPresent( By.id( "projects" ) );
    }

    public void selectCustomRepository( String repoUrl ) {
        customRepositoryRadioOption.click();
        repoUrlInput.clear();
        repoUrlInput.sendKeys( repoUrl );
        repoUrlInput.sendKeys( Keys.TAB );
        next();
        Waits.elementPresent( By.id( "projects" ) );
    }

    public void selectProjects( String... projects ) {
        for ( String proj : projects ) {
            selectProject( proj );
        }
        next();
    }

    private void selectProject( String project ) {
        By projCheckboxLoc = ByUtil.xpath( "//input[following-sibling::span[contains(text(),'%s')]]", project );
        WebElement checkbox = Waits.elementPresent( projCheckboxLoc, 10 );
        checkbox.click();
    }

    public void setTargetRepoAndOrgUnit( String repoName,
                                         String orgUnit ) {
        targetRepoInput.sendKeys( repoName );
        targetOrgUnit.sendKeys( orgUnit );
        targetRepoInput.click(); //workaround to fire onchange event or something //TODO report not user friendly
        finish();
    }
}