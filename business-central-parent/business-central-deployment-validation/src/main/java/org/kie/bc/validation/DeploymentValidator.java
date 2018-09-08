/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.bc.validation;

import java.io.File;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

/**
 * Validates that this deployment is not using an unmigrated niogit directory.
 */
public class DeploymentValidator implements Extension {

    public void validateNiogit(@Observes BeforeBeanDiscovery bbd) {
        final File niogit = getNiogitDir();

        if (niogit.exists()) {
            failIfNotMigrated(niogit);
        }
    }

    private File getNiogitDir() {
        final ConfigProperties configProperties = new ConfigProperties(System.getProperties());
        final JGitFileSystemProviderConfiguration providerConfig = new JGitFileSystemProviderConfiguration();
        providerConfig.load(configProperties);
        final File niogit = providerConfig.getGitReposParentDir();
        return niogit;
    }

    private void failIfNotMigrated(File niogit) {
        File oldSystemGit = new File(niogit, "system.git");
        if (oldSystemGit.exists()) {
            // TODO link to docs for migration tool in message?
            // TODO i18n?
            throw new IllegalStateException(String
                                            .format("The configured repository folder contains unmigrated repositories from a previous version." +
                                                    " Please use the repostory migraiton tool to update the repository layout.\n\tRepositories directory: %s",
                                                    niogit));
        }
    }

}
