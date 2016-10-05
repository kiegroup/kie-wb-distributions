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

import static org.kie.wb.selenium.util.ByUtil.jquery;

import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;

public class ArtifactRepositoryPerspective extends AbstractPerspective {

    private static final By NAME_COLUMN_HEADER = By.xpath("//th[contains(text(),'Name')]");

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent(NAME_COLUMN_HEADER);
    }

    public boolean isArtifactPresent(String gav) {
        return Waits.isElementPresent(jquery("tr[__gwt_row]:contains('%s')", gav), 5);
    }
}
