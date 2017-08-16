/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.security;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.events.AuthorizationPolicyDeployedEvent;
import org.uberfire.backend.server.authz.AuthorizationPolicyDeployer;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.uberfire.security.authz.AuthorizationResult.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchACLTest {

    static final String HOME_PERSPECTIVE = "HomePerspective";

    static final List<String> DEFAULT_DENIED = Arrays.asList(
            "perspective.read",
            "perspective.create",
            "perspective.delete",
            "perspective.update",
            "project.create",
            "project.build",
            "project.read",
            "project.update",
            "project.delete",
            "orgunit.create",
            "orgunit.read",
            "orgunit.update",
            "orgunit.delete",
            "repository.create",
            "repository.read",
            "repository.update",
            "repository.delete",
            "dataobject.edit",
            "asset.promote",
            "project.release",
            "repository.configure",
            "planner.available");

    static final List<String> DEVELOPER_DENIED = Arrays.asList(
            "perspective.read.AdministrationPerspective",
            "perspective.read.AppsPerspective",
            "perspective.read.PlugInAuthoringPerspective",
            "perspective.read.DataSetAuthoringPerspective",
            "perspective.read.DataSourceManagementPerspective",
            "perspective.read.TaskAdmin",
            "orgunit.create",
            "orgunit.update",
            "orgunit.delete");

    static final List<String> ANALYST_DENIED = Arrays.asList(
            "perspective.read.AdministrationPerspective",
            "perspective.read.AppsPerspective",
            "perspective.read.PlugInAuthoringPerspective",
            "perspective.read.DataSetAuthoringPerspective",
            "perspective.read.DataSourceManagementPerspective",
            "perspective.read.GuvnorM2RepoPerspective",
            "perspective.read.ProvisioningManagementPerspective",
            "perspective.read.ServerManagementPerspective",
            "perspective.read.TaskAdmin",
            "perspective.read.ExecutionErrors",
            "orgunit.create",
            "orgunit.update",
            "orgunit.delete",
            "dataobject.edit");

    static final List<String> MANAGER_GRANTED = Arrays.asList(
            "perspective.read.DashboardPerspective",
            "dashboard.manage");

    static final List<String> USER_GRANTED = Arrays.asList(
            "perspective.read.SocialHomePagePerspective",
            "perspective.read.UserHomePagePerspective",
            "perspective.read.ProcessDefinitions",
            "perspective.read.ProcessInstances",
            "perspective.read.Tasks",
            "perspective.read.DashboardPerspective",
            "dashboard.manage");
            
        static final List<String> PROCESS_ADMIN_GRANTED = Arrays.asList(
            "perspective.read.SocialHomePagePerspective",
            "perspective.read.UserHomePagePerspective",
            "perspective.read.ProcessDefinitions",
            "perspective.read.ProcessInstances",
            "perspective.read.Tasks",
            "perspective.read.TaskAdmin",
            "perspective.read.ExecutionErrors",
            "perspective.read.DashboardPerspective",
            "dashboard.manage");

    @Mock
    AuthorizationPolicyStorage storage;

    @Mock
    Event<AuthorizationPolicyDeployedEvent> deployedEvent;

    AuthorizationPolicyDeployer deployer;
    PermissionManager permissionManager;
    AuthorizationPolicy policy;

    @Before
    public void setUp() throws Exception {
        permissionManager = new DefaultPermissionManager();
        deployer = new AuthorizationPolicyDeployer(storage, permissionManager, deployedEvent);

        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("security-policy.properties");
        Path policyDir = Paths.get(fileURL.toURI()).getParent();
        deployer.deployPolicy(policyDir);

        ArgumentCaptor<AuthorizationPolicy> policyCaptor = ArgumentCaptor.forClass(AuthorizationPolicy.class);
        verify(storage).loadPolicy();
        verify(storage).savePolicy(policyCaptor.capture());
        policy = policyCaptor.getValue();
    }

    @Test
    public void testPolicyDeployment() {
        assertNotNull(policy);
        assertEquals(policy.getRoles().size(), 6);

        verify(storage).savePolicy(policy);
        verify(deployedEvent).fire(any());
    }

    @Test
    public void testDefaultPermissions() {
        assertEquals(policy.getHomePerspective(), HOME_PERSPECTIVE);
        PermissionCollection pc = policy.getPermissions();

        for (String permissionName : DEFAULT_DENIED) {
            Permission p = pc.get(permissionName);
            assertNotNull(p);
            assertEquals(p.getResult(), ACCESS_DENIED);
        }
    }

    @Test
    public void testAdminPermissions() {
        testPermissions(new RoleImpl("admin"), null, HOME_PERSPECTIVE, ACCESS_GRANTED, null);
    }

    @Test
    public void testDeveloperPermissions() {
        testPermissions(new RoleImpl("developer"), DEVELOPER_DENIED, HOME_PERSPECTIVE, ACCESS_GRANTED, ACCESS_DENIED);
    }

    @Test
    public void testAnalystPermissions() {
        testPermissions(new RoleImpl("analyst"), ANALYST_DENIED, HOME_PERSPECTIVE, ACCESS_GRANTED, ACCESS_DENIED);
    }

    @Test
    public void testManagerPermissions() {
        testPermissions(new RoleImpl("manager"), MANAGER_GRANTED, HOME_PERSPECTIVE, ACCESS_DENIED, ACCESS_GRANTED);
    }

    @Test
    public void testUserPermissions() {
        testPermissions(new RoleImpl("user"), USER_GRANTED, HOME_PERSPECTIVE, ACCESS_DENIED, ACCESS_GRANTED);
    }
    
    @Test
    public void testProcessAdminPermissions() {
        testPermissions(new RoleImpl("process-admin"), PROCESS_ADMIN_GRANTED, HOME_PERSPECTIVE, ACCESS_DENIED, ACCESS_GRANTED);
    }

    public void testPermissions(Role role,
                                List<String> exceptionList,
                                String homeExpected,
                                AuthorizationResult defaultExpected,
                                AuthorizationResult exceptionExpected) {

        assertEquals(role != null ? policy.getHomePerspective(role) : policy.getHomePerspective(), homeExpected);
        PermissionCollection pc = policy.getPermissions(role);

        for (String permissionName : DEFAULT_DENIED) {
            if (exceptionList == null || !exceptionList.contains(permissionName)) {
                Permission p = pc.get(permissionName);
                assertNotNull(p);
                assertEquals(p.getResult(), defaultExpected);
            }
        }
        if (exceptionList != null) {
            for (String permissionName : exceptionList) {
                Permission p = pc.get(permissionName);
                assertNotNull(p);
                assertEquals(p.getResult(), exceptionExpected);
            }
        }
    }
}
