/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.wb.selenium.util;

import org.jboss.arquillian.graphene.Graphene;
import org.kie.wb.selenium.model.PageObject;
import org.openqa.selenium.By;

public class BusyPopup extends PageObject {

    public void waitForDisappearance() {
        By glass = By.cssSelector(".gwt-PopupPanelGlass");
        Graphene.waitModel().until().element(glass).is().not().present();
        Waits.pause(500);
    }
}
