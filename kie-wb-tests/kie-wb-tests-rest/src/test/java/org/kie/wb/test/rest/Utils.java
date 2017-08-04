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

package org.kie.wb.test.rest;

import java.util.Collection;

import org.assertj.core.api.SoftAssertions;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.ProjectRequest;
import org.guvnor.rest.client.ProjectResponse;
import org.kie.wb.test.rest.client.WorkbenchClient;

public class Utils {

    private static final String DEFAULT_VERSION = "1.0";
    private final String orgUnit;
    private final WorkbenchClient client;

    public Utils(String orgUnit, WorkbenchClient client) {

        this.orgUnit = orgUnit;
        this.client = client;
    }

    public void createProject(String name, String description, String groupId, String version) {
        ProjectRequest project = new ProjectRequest();
        project.setName(name);
        project.setDescription(description);
        project.setGroupId(groupId);
        project.setVersion(version);

        CreateProjectRequest request = client.createProject(orgUnit, project);
        assertCreateProjectRequest(request, project);
        assertProjectExists(project);
    }

    private void assertCreateProjectRequest(CreateProjectRequest actual, ProjectRequest expected) {
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getOrganizationalUnitName()).isEqualTo(orgUnit);
        assertions.assertThat(actual.getProjectName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getProjectGroupId()).isEqualTo(expected.getGroupId());
        assertions.assertThat(actual.getProjectVersion()).isEqualTo(expected.getVersion());
        assertions.assertAll();
    }

    private void assertProjectExists(ProjectRequest projectRequest) {
        ProjectResponse projectResponse = getProjectByName(projectRequest.getName());

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(projectResponse.getName()).isEqualTo(projectRequest.getName());
        assertions.assertThat(projectResponse.getDescription()).isEqualTo(projectRequest.getDescription());
        assertions.assertThat(projectResponse.getGroupId())
                .isEqualTo(projectRequest.getGroupId() == null ? projectRequest.getName() : projectRequest.getGroupId());
        assertions.assertThat(projectResponse.getVersion())
                .isEqualTo(projectRequest.getVersion() == null ? DEFAULT_VERSION : projectRequest.getVersion());
        assertions.assertAll();
    }

    public ProjectResponse getProjectByName(String name) {
        Collection<ProjectResponse> projects = client.getProjects(orgUnit);
        return projects.parallelStream()
                .filter(projectResponse -> projectResponse.getName().equals(name))
                .findAny().orElse(null);
    }
}
