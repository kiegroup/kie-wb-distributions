/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.smoke.wb.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.smoke.wb.util.RestUtil.post;
import static org.kie.smoke.wb.util.RestUtil.postEntity;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.guvnor.rest.client.AddRepositoryToOrganizationalUnitRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.Entity;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.ProjectRequest;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.RepositoryResponse;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.smoke.wb.AbstractWorkbenchIntegrationTest;
import org.kie.smoke.wb.category.KieDroolsWbSmoke;
import org.kie.smoke.wb.category.KieWbSmoke;
import org.kie.smoke.wb.util.RestUtil;
import org.kie.smoke.wb.util.TestConstants;

@Category({KieWbSmoke.class, KieDroolsWbSmoke.class})
@SuppressWarnings("unchecked")
public class GuvnorRestSmokeIntegrationTest extends AbstractWorkbenchIntegrationTest {

    private final int maxTries = 60;

    private final static String mediaType = MediaType.APPLICATION_JSON;
    private final static String user = TestConstants.MARY_USER;
    private final static String password = TestConstants.MARY_PASSWORD;

    private static final SimpleDateFormat ouSdf = new SimpleDateFormat("yy-MM-dd_HH:mm:ss");
    private static final Random random = new Random();

    // Test methods ---------------------------------------------------------------------------------------------------------------

    /**
     * Tests the following REST urls:
     * <p/>
     * ../rest/repostitories GET
     * ../rest/organizationalunit POST
     * ../rest/repostitories POST
     * ../rest/jobs/{id} GET
     * ../rest/repositories/{repo}/projects POST
     * ../rest/repositories/{repoName}/projects/{projectName} DELETE
     * ../rest/repositories/{repoName}/projects/{projectName} GET
     * ../rest/organizationalunits/{ouName}/repositories/{repoName} DELETE
     * ../rest/repositories/{repoName} DELETE
     *
     * @throws Exception When things go wrong..
     */
    @Test
    public void testManipulatingRepositoriesAndProjects() throws Exception {
        String orgUnitName = "repo-user-" + ouSdf.format(new Date());
        {
            // rest/organizationalunit POST
            OrganizationalUnit orgUnit = new OrganizationalUnit();
            orgUnit.setName(orgUnitName);
            orgUnit.setDefaultGroupId("org.kie.smoke");
            orgUnit.setDescription("Test user for the Kie Workbench smoke tests");
            orgUnit.setOwner(this.getClass().getName());

            CreateOrganizationalUnitRequest createOuRequest = RestUtil.postEntity(deploymentUrl, "rest/organizationalunits", mediaType,
                    202, user, password,
                    orgUnit, CreateOrganizationalUnitRequest.class);
            assertNotNull("create org unit request", createOuRequest);
            assertEquals("job request status", JobStatus.APPROVED, createOuRequest.getStatus());
            String jobId = createOuRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, createOuRequest.getStatus());
        }

        String repoName = UUID.randomUUID().toString();
        {
            // rest/repositories POST
            RepositoryRequest newRepo = new RepositoryRequest();
            newRepo.setName(repoName);
            newRepo.setDescription("repo for rest services smoke tests");
            newRepo.setRequestType("new");
            newRepo.setOrganizationalUnitName(orgUnitName);

            CreateOrCloneRepositoryRequest createJobRequest = RestUtil.postEntity(deploymentUrl, "rest/repositories", mediaType,
                    202, user, password, 1d,
                    newRepo, CreateOrCloneRepositoryRequest.class);
            assertNotNull("create repo job request", createJobRequest);
            JobStatus requestStatus = createJobRequest.getStatus();
            assertTrue( "job request status: " + requestStatus, JobStatus.ACCEPTED.equals(requestStatus) || JobStatus.APPROVED.equals(requestStatus) );

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, createJobRequest.getJobId(), createJobRequest.getStatus());
        }

        {
            // rest/repositories/{repoName}/projects POST
            // - backwards compatibility
            Entity project = new Entity();
            project.setDescription("random project");
            String testProjectName = UUID.randomUUID().toString();
            project.setName(testProjectName);

            CreateProjectRequest createProjectRequest = RestUtil.postEntity(deploymentUrl, "rest/repositories/" + repoName + "/projects", mediaType,
                    202, user, password, 0.5,
                    project, CreateProjectRequest.class);

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, createProjectRequest.getJobId(), createProjectRequest.getStatus());
        }

        String testProjectName = UUID.randomUUID().toString();
        ProjectRequest newProject = new ProjectRequest();
        {
            // rest/repositories/{repoName}/projects POST
            newProject.setDescription("test get/del project");
            newProject.setName(testProjectName);
            String testProjectGroupid = UUID.randomUUID().toString();
            newProject.setGroupId(testProjectGroupid);
            String testVersion = "1.0";
            newProject.setVersion(testVersion);
            CreateProjectRequest createProjectRequest = RestUtil.postEntity(deploymentUrl, "rest/repositories/" + repoName + "/projects", mediaType,
                    202, user, password, 0.5,
                    newProject, CreateProjectRequest.class);

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, createProjectRequest.getJobId(), createProjectRequest.getStatus());
        }

        // rest/repositories/{repoName}/projects GET
        Collection<ProjectResponse> projectResponses = RestUtil.get(deploymentUrl,
                "rest/repositories/" + repoName + "/projects", mediaType,
                200, user, password,
                Collection.class, ProjectResponse.class);

        assertNotNull( "Null project request list", projectResponses );
        assertFalse( "Empty project request list", projectResponses.isEmpty() );
        ProjectRequest foundProjReq = null;
        for( ProjectRequest projReq : projectResponses ) {
           if( testProjectName.equals(projReq.getName()) ) {
              foundProjReq = projReq;
              break;
           }
        }
        assertNotNull( "Could not find project", foundProjReq );
        assertEquals( "Project group id", newProject.getGroupId(), foundProjReq.getGroupId() );
        assertEquals( "Project version", newProject.getVersion(), foundProjReq.getVersion() );


        {
            // rest/repositories/{repoName}/projects/{projectName} DELETE
            DeleteProjectRequest delProjectRequest = RestUtil.delete(deploymentUrl,
                    "rest/repositories/" + repoName + "/projects/" + testProjectName, mediaType,
                    202, user, password,
                    DeleteProjectRequest.class);
            String jobId = delProjectRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, delProjectRequest.getStatus());
        }

        {
            // rest/repositories/{repoName}/projects/{projectName} GET
            Collection<ProjectResponse> projectList = RestUtil.get(deploymentUrl, "rest/repositories/" + repoName + "/projects", mediaType,
                    200, user, password,
                    Collection.class, ProjectResponse.class);
            assertNotNull( "Null project list", projectList );

            for( ProjectResponse project : projectList ) {
               assertNotEquals( "Test project should have been deleted", testProjectName, project.getName() );
            }
        }

        {
            // rest/organizationalunits/{ouName}/repositories/{repoName} DELETE
            RemoveRepositoryFromOrganizationalUnitRequest remRepoFromOuRequest = RestUtil.delete(deploymentUrl,
                    "rest/organizationalunits/" + orgUnitName + "/repositories/" + repoName, mediaType,
                    202, user, password,
                    RemoveRepositoryFromOrganizationalUnitRequest.class);
            String jobId = remRepoFromOuRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, remRepoFromOuRequest.getStatus());
        }

        {
            // rest/repositories GET
            Collection<RepositoryResponse> repoList = RestUtil.get(deploymentUrl, "rest/repositories/", mediaType,
                    200, user, password,
                   Collection.class, RepositoryResponse.class);

            assertNotNull( "Null repo list", repoList );
            assertFalse( "Empty repo list", repoList.isEmpty() );
            boolean repoFound = false;
            for( RepositoryResponse repo : repoList ) {
               if( repoName.equals(repo.getName()) ) {
                   repoFound = true;
                   assertTrue( "Empty URL for repo '" + repoName + "'", repo.getGitURL() != null && ! repo.getGitURL().isEmpty() );
               }
            }
            assertTrue( "Could not find repo '" + repoName + "'", repoFound );
        }

        {
            // rest/repositories/{repoName} DELETE
            RemoveRepositoryRequest delRepoRequest = RestUtil.delete(deploymentUrl, "rest/repositories/" + repoName, mediaType,
                    202, user, password,
                    RemoveRepositoryRequest.class);
            String jobId = delRepoRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, delRepoRequest.getStatus());
        }

        {
            // rest/repositories GET
            Collection<RepositoryResponse> repoList = RestUtil.get(deploymentUrl, "rest/repositories/", mediaType,
                    200, user, password,
                   Collection.class, RepositoryResponse.class);

            assertNotNull( "Null repo list", repoList );
            assertFalse( "Empty repo list", repoList.isEmpty() );
            for( RepositoryResponse repo : repoList ) {
               assertNotEquals( "Repository should have been deleted", repoName, repo.getName() );
            }
        }
    }

    /**
     * Tests the following REST urls:
     * <p/>
     * ../rest/repositories GET
     * ../rest/repositories/{repo}/projecst POST
     * ../rest/jobs/{id} GET
     *
     * @throws Exception
     */
    @Test
    public void testMavenOperations() throws Exception {
        // rest/repositories GET
        Collection<RepositoryResponse> repoResponses = RestUtil.get(deploymentUrl, "rest/repositories", mediaType,
                200, user, password,
                Collection.class, RepositoryResponse.class);
        assertTrue(repoResponses.size() > 0);
        String repoName = repoResponses.iterator().next().getName();

        String projectName = UUID.randomUUID().toString();
        {
            // rest/repositories/{repoName}/projects POST
            ProjectRequest project = new ProjectRequest();
            project.setDescription("test project");
            String groupId = UUID.randomUUID().toString();
            String version = random.nextInt(1000) + ".0";
            project.setName(projectName);
            project.setGroupId(groupId);
            project.setVersion(version);

            CreateProjectRequest createProjectRequest = RestUtil.postEntity(deploymentUrl, "rest/repositories/" + repoName + "/projects", mediaType,
                    202, user, password,
                    project,
                    CreateProjectRequest.class);

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, createProjectRequest.getJobId(), createProjectRequest.getStatus());
        }

        {
            // rest/repositories/{repoName}/projects POST
            CompileProjectRequest compileRequest = RestUtil.post(deploymentUrl,
                    "rest/repositories/" + repoName + "/projects/" + projectName + "/maven/compile", mediaType,
                    202, user, password,
                    CompileProjectRequest.class);

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, compileRequest.getJobId(), compileRequest.getStatus());
        }

    }

    private JobResult waitForJobToComplete(URL deploymentUrl, String jobId, JobStatus jobStatus) throws Exception {
        return waitForJobToHaveStatus(deploymentUrl, jobId, jobStatus, JobStatus.SUCCESS);
    }

    private JobResult waitForJobToHaveStatus(URL deploymentUrl, String jobId, JobStatus jobStatus, JobStatus expectedStatus ) throws Exception {
        assertTrue( "Initial status of request should be ACCEPTED or APROVED: " + jobStatus,
                jobStatus.equals(JobStatus.ACCEPTED) || jobStatus.equals(JobStatus.APPROVED) );
        int wait = 0;
        JobResult jobResult = null;
        while( ( jobStatus.equals(JobStatus.ACCEPTED) || jobStatus.equals(JobStatus.APPROVED) )
                && wait < maxTries ) {
            jobResult = RestUtil.get(deploymentUrl, "rest/jobs/" + jobId, mediaType, 200, user, password, JobResult.class);
            assertEquals( jobResult.getJobId(), jobId );
            jobStatus = jobResult.getStatus();
            if( jobStatus.equals(expectedStatus) ) {
                break;
            } else if( jobStatus.equals(JobStatus.FAIL) ) {
                fail( "Request failed." );
            }
            ++wait;
            Thread.sleep(3*1000);
        }
        assertTrue( "Too many tries!", wait < maxTries );

        return jobResult;
    }

    /**
     * Tests the following REST urls:
     * <p/>
     * ../rest/organizationalunits GET
     * ../rest/organizationalunits POST
     *
     * @throws Exception
     */
    @Test
    public void testManipulatingOUs() throws Exception {
        int origUnitsSize;
        List<OrganizationalUnit> ouList = new ArrayList<OrganizationalUnit>(2);
        {
            // rest/organizationalunits GET
            Collection<OrganizationalUnit> orgUnits = RestUtil.get(deploymentUrl, "rest/organizationalunits", mediaType,
                    200, user, password, Collection.class, OrganizationalUnit.class);
            origUnitsSize = orgUnits.size();
        }

        {
            // rest/organizationalunits POST
            for( int i = 0; i < 2; ++i ) {
                OrganizationalUnit orgUnit = new OrganizationalUnit();
                orgUnit.setDescription("Smoke Tests OU");
                orgUnit.setName(UUID.randomUUID().toString());
                orgUnit.setOwner(this.getClass().getSimpleName());
                CreateOrganizationalUnitRequest createOURequest = RestUtil.postEntity(deploymentUrl, "rest/organizationalunits", mediaType,
                        202, user, password,
                        orgUnit, CreateOrganizationalUnitRequest.class);

                // rest/jobs/{jobId}
                waitForJobToComplete(deploymentUrl, createOURequest.getJobId(), createOURequest.getStatus());

                ouList.add(orgUnit);
            }
        }

        {
            // rest/organizaionalunits GET
            Collection<OrganizationalUnit> orgUnits = RestUtil.get(deploymentUrl, "rest/organizationalunits",  mediaType,
                    200, user, password,
                    Collection.class, OrganizationalUnit.class);
            assertEquals("Exepcted an OU to be added.", origUnitsSize + 2, orgUnits.size());
        }

        String repoName = UUID.randomUUID().toString();
        {
            // rest/repositories POST
            RepositoryRequest newRepo = new RepositoryRequest();
            newRepo.setName(repoName);
            newRepo.setDescription("repo for testing rest services");
            newRepo.setRequestType("new");
            newRepo.setOrganizationalUnitName(ouList.get(0).getName());

            CreateOrCloneRepositoryRequest createRepoRequest = RestUtil.postEntity(deploymentUrl, "rest/repositories", mediaType,
                    202, user, password,
                    newRepo, CreateOrCloneRepositoryRequest.class);
            assertNotNull("create repo job request", createRepoRequest);
            assertEquals("job request status", JobStatus.APPROVED, createRepoRequest.getStatus());

            // rest/jobs/{jobId}
           waitForJobToComplete(deploymentUrl, createRepoRequest.getJobId(), createRepoRequest.getStatus());
        }

        {
            // rest/organizationalunits/{ou}/repositories/{repoName} POST
            AddRepositoryToOrganizationalUnitRequest addRepoToOuRequest = RestUtil.post(deploymentUrl,
                    "rest/organizationalunits/" + ouList.get(1).getName() + "/repositories/" + repoName, mediaType,
                    202, user, password,
                    AddRepositoryToOrganizationalUnitRequest.class);

            assertNotNull("add repo to ou job request", addRepoToOuRequest);
            assertEquals("job request status", JobStatus.APPROVED, addRepoToOuRequest.getStatus());

            // rest/jobs/{jobId}
            waitForJobToComplete(deploymentUrl, addRepoToOuRequest.getJobId(), addRepoToOuRequest.getStatus());
        }

        {
            // rest/organizationalunits/{ou} GET
            OrganizationalUnit orgUnitRequest = RestUtil.get(deploymentUrl,
                     "rest/organizationalunits/" + ouList.get(1).getName(), mediaType,
                    200, user, password,
                    OrganizationalUnit.class);
            assertNotNull("organizational unit request", orgUnitRequest);

            assertTrue("repository has not been added to organizational unit", orgUnitRequest.getRepositories().contains(repoName));
        }

        {
            // rest/organizationalunits/{ou}/repositories/{repoName} DELETE
            RemoveRepositoryFromOrganizationalUnitRequest remRepoFromOuRquest = RestUtil.delete(deploymentUrl,
                    "rest/organizationalunits/" + ouList.get(1).getName() + "/repositories/" + repoName, mediaType,
                    202, user, password,
                    RemoveRepositoryFromOrganizationalUnitRequest.class);
            assertNotNull("delete repo from ou job request", remRepoFromOuRquest);
            assertEquals("job request status", JobStatus.APPROVED, remRepoFromOuRquest.getStatus());

            // rest/jobs/{jobId}
            waitForJobToComplete(deploymentUrl, remRepoFromOuRquest.getJobId(), remRepoFromOuRquest.getStatus());
        }

        {
            // rest/organizationalunits/{ou} GET
            OrganizationalUnit orgUnitRequest = RestUtil.get(deploymentUrl, "rest/organizationalunits/" + ouList.get(1).getName(), mediaType,
                    200, user, password, OrganizationalUnit.class);
            assertNotNull("organizational unit request", orgUnitRequest);

            assertFalse("repository should have been deleted from organizational unit", orgUnitRequest.getRepositories().contains(repoName));
        }

        {
            // rest/organizationalunits/{ou} DELETE
            RemoveOrganizationalUnitRequest removeOrgUnitRequest = RestUtil.delete(deploymentUrl, "rest/organizationalunits/" + ouList.get(1).getName(), mediaType,
                    202, user, password,
                    RemoveOrganizationalUnitRequest.class);
            assertNotNull("organizational unit request", removeOrgUnitRequest);
            waitForJobToComplete(deploymentUrl, removeOrgUnitRequest.getJobId(), removeOrgUnitRequest.getStatus());
        }

        {
            // verify the OU was deleted - the GET request should return 404
            RestUtil.get(deploymentUrl, "rest/organizationalunits/" + ouList.get(1).getName(), mediaType,
                    404, user, password);
        }
    }
}
