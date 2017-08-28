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

package org.kie.workbench.client.authz;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.kie.workbench.common.workbench.client.authz.WorkbenchTreeProvider;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.PermissionNode;

import static org.guvnor.m2repo.security.MavenRepositoryPagedJarTableFeatures.JAR_DOWNLOAD;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES;

@Specializes
public class WorkbenchRuntimeTreeProvider extends WorkbenchTreeProvider {

    @Inject
    public WorkbenchRuntimeTreeProvider(final PermissionManager permissionManager) {
        super(permissionManager);
    }

    @Override
    protected List<PermissionNode> createPermissions() {
        return Arrays.asList(
                createPermissionLeafNode(JAR_DOWNLOAD,
                                         i18n.MavenRepositoryPagedJarTableDownloadJar(),
                                         i18n.MavenRepositoryPagedJarTableDownloadJarHelp()),
                createPermissionLeafNode(EDIT_GLOBAL_PREFERENCES,
                                         i18n.EditGlobalPreferences(),
                                         i18n.EditGlobalPreferencesHelp())
        );
    }
}
