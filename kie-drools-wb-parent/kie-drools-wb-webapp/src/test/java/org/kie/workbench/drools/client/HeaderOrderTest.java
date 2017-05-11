/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.drools.client;

import org.junit.Test;
import org.kie.workbench.drools.client.navbar.AppNavBar;
import org.kie.workbench.drools.client.navbar.LogoNavBar;
import org.uberfire.client.workbench.Header;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainerImpl;

import static org.junit.Assert.*;

public class HeaderOrderTest {

    @Test
    public void headerOrderTest() {
        Header logo = new LogoNavBar();
        Header app = new AppNavBar();
        Header breadcrumb = new UberfireBreadcrumbsContainerImpl();

        assertTrue(logo.getOrder() > app.getOrder());
        assertTrue(app.getOrder() > breadcrumb.getOrder());
    }

}
