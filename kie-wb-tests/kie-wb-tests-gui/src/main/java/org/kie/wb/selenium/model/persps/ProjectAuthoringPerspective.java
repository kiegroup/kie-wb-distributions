/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;

public class ProjectAuthoringPerspective extends AbstractPerspective {

    private static final By WELCOME_MESSAGE_HOLDER = By.id("welcome");
    private static final By NEW_ITEM_MENU = By.xpath( "//a[contains(text(),'New Item')]" );

    @Override
    public void waitForLoaded() {
        //Don't check for specific elements to appear as the ProjectLibraryPerspective may have been shown
        Waits.pause( 500 );
    }

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent(NEW_ITEM_MENU);
    }

    public boolean isAuthoringDisabled(){
        return Waits.isElementPresent(WELCOME_MESSAGE_HOLDER);
    }
}
