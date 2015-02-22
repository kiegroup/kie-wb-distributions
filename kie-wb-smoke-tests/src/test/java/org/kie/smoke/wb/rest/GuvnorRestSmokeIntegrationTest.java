package org.kie.smoke.wb.rest;

import static org.junit.Assert.*;
import static org.kie.smoke.wb.util.RestUtil.delete;
import static org.kie.smoke.wb.util.RestUtil.get;
import static org.kie.smoke.wb.util.RestUtil.post;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.RepositoryResponse;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.util.GenericType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.smoke.wb.AbstractWorkbenchIntegrationTest;
import org.kie.smoke.wb.category.KieDroolsWbSmoke;
import org.kie.smoke.wb.category.KieWbSmoke;
import org.kie.smoke.wb.util.RestRequestHelper;
import org.kie.smoke.wb.util.TestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category({KieWbSmoke.class, KieDroolsWbSmoke.class})
public class GuvnorRestSmokeIntegrationTest extends AbstractWorkbenchIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(GuvnorRestSmokeIntegrationTest.class);

    private final int maxTries = 60;
    private final int jobCompleteSleepSecs = 1;

    private final MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

    private static final SimpleDateFormat ouSdf = new SimpleDateFormat("yy-MM-dd_HH:mm:ss");

    protected static void addToRequestBody(ClientRequest restRequest, Object obj) throws Exception {
        String body = convertObjectToJsonString(obj);
        logger.debug("]] " + body);
        restRequest.body(MediaType.APPLICATION_JSON_TYPE, body);
    }

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
    }

    protected static String convertObjectToJsonString(Object object) throws JsonGenerationException, JsonMappingException, IOException {
        return mapper.writeValueAsString(object);
    }

    protected static Object convertJsonStringToObject(String jsonStr, Class<?> type) throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(jsonStr, type);
    }

    private RestRequestHelper getRestRequestHelper(URL deploymentUrl) {
        return RestRequestHelper.newInstance(deploymentUrl,
                TestConstants.MARY_USER, TestConstants.MARY_PASSWORD,
                500,
                MediaType.APPLICATION_JSON_TYPE);
    }

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
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl);
        
        {
            // rest/repositories GET
            ClientRequest restRequest = requestHelper.createRequest("repositories");
            Collection<RepositoryResponse> repoResponses = get(restRequest, mediaType, new GenericType<Collection<RepositoryResponse>>() {});
            assertFalse("Empty repository list", repoResponses.isEmpty());
            String ufPlaygroundUrl = null;
            for( RepositoryResponse repo : repoResponses ) {
                if ("uf-playground".equals(repo.getName()) ) {
                    ufPlaygroundUrl = repo.getGitURL();
                }
            }
            assertEquals("UF-Playground Git URL", "git://uf-playground", ufPlaygroundUrl);
        }

        String orgUnitName = "repo-user-" + ouSdf.format(new Date());
        {
            // rest/organizationalunit POST
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits");
            OrganizationalUnit orgUnit = new OrganizationalUnit();
            orgUnit.setName(orgUnitName);
            orgUnit.setDefaultGroupId("org.kie.smoke");
            orgUnit.setDescription("Test user for the Kie Workbench smoke tests");
            orgUnit.setOwner(this.getClass().getName());
            addToRequestBody(restRequest, orgUnit);

            CreateOrganizationalUnitRequest createOuRequest = post(restRequest, mediaType, 202, CreateOrganizationalUnitRequest.class);
            logger.debug("]] " + convertObjectToJsonString(createOuRequest));
            assertNotNull("create org unit request", createOuRequest);
            assertEquals("job request status", JobStatus.APPROVED, createOuRequest.getStatus());
            String jobId = createOuRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, createOuRequest.getStatus(), requestHelper);
        }

        String repoName = UUID.randomUUID().toString();
        {
            // rest/repositories POST
            ClientRequest restRequest = requestHelper.createRequest("repositories");
            RepositoryRequest newRepo = new RepositoryRequest();
            newRepo.setName(repoName);
            newRepo.setDescription("repo for rest services smoke tests");
            newRepo.setRequestType("new");
            newRepo.setOrganizationlUnitName(orgUnitName);
            addToRequestBody(restRequest, newRepo);

            CreateOrCloneRepositoryRequest createJobRequest = post(restRequest, mediaType, 202, CreateOrCloneRepositoryRequest.class);
            logger.debug("]] " + convertObjectToJsonString(createJobRequest));
            assertNotNull("create repo job request", createJobRequest);
            assertEquals("job request status", JobStatus.APPROVED, createJobRequest.getStatus());
            String jobId = createJobRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, createJobRequest.getStatus(), requestHelper);
        }

        String testProjectName = "test-project";
        {
            // rest/repositories/{repoName}/projects POST
            ClientRequest restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects");
            Entity project = new Entity();
            project.setDescription("test project");
            project.setName(testProjectName);
            addToRequestBody(restRequest, project);
            CreateProjectRequest createProjectRequest = post(restRequest, mediaType, 202, CreateProjectRequest.class);
            logger.debug("]] " + convertObjectToJsonString(createProjectRequest));
            String jobId = createProjectRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, createProjectRequest.getStatus(), requestHelper);
        }
       
        {
            // rest/repositories/{repoName}/projects/{projectName} DELETE
            ClientRequest restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects/" + testProjectName);
            DeleteProjectRequest delProjectRequest = delete(restRequest, mediaType, 202, DeleteProjectRequest.class);
            logger.debug("]] " + convertObjectToJsonString(delProjectRequest));
            String jobId = delProjectRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, delProjectRequest.getStatus(), requestHelper);
        }
      
        {
            // rest/repositories/{repoName}/projects/{projectName} GET
            ClientRequest restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects" );
            Collection<ProjectResponse> projectList = get(restRequest, mediaType, new GenericType<Collection<ProjectResponse>>() {});
            logger.debug("]] " + convertObjectToJsonString(projectList));
            
            assertNotNull( "Null project list", projectList );

            for( ProjectResponse project : projectList ) { 
               assertNotEquals( "Test project should have been deleted", testProjectName, project.getName() );
            }
        }
        
        {
            // rest/organizationalunits/{ouName}/repositories/{repoName} DELETE
            ClientRequest restRequest = requestHelper.createRequest("/organizationalunits/" + orgUnitName + "/repositories/" + repoName );
            RemoveRepositoryFromOrganizationalUnitRequest remRepoFromOuRequest = delete(restRequest, mediaType, 202, RemoveRepositoryFromOrganizationalUnitRequest.class);
            logger.debug("]] " + convertObjectToJsonString(remRepoFromOuRequest));
            String jobId = remRepoFromOuRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, remRepoFromOuRequest.getStatus(), requestHelper);
        }
       
        {
            // rest/repositories/{repoName} DELETE
            ClientRequest restRequest = requestHelper.createRequest("/repositories/" + repoName );
            RemoveRepositoryRequest delRepoRequest = delete(restRequest, mediaType, 202, RemoveRepositoryRequest.class);
            logger.debug("]] " + convertObjectToJsonString(delRepoRequest));
            String jobId = delRepoRequest.getJobId();

            // rest/jobs/{jobId} GET
            waitForJobToComplete(deploymentUrl, jobId, delRepoRequest.getStatus(), requestHelper);
        }
        
        {
            // rest/repositories GET
            ClientRequest restRequest = requestHelper.createRequest("repositories/");
            Collection<RepositoryResponse> repoList = get(restRequest, mediaType, new GenericType<Collection<RepositoryResponse>>() {});
            logger.debug("]] " + convertObjectToJsonString(repoList));

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
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl);
        ClientRequest restRequest = requestHelper.createRequest("repositories");
        Collection<Map<String, String>> repoResponses = get(restRequest, mediaType, Collection.class);
        assertTrue(repoResponses.size() > 0);
        String repoName = repoResponses.iterator().next().get("name");

        // rest/repositories/{repoName}/projects POST
        restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects");
        Entity project = new Entity();
        project.setDescription("test project");
        String projectName = UUID.randomUUID().toString();
        project.setName(projectName);
        addToRequestBody(restRequest, project);

        CreateProjectRequest createProjectRequest = post(restRequest, mediaType, 202, CreateProjectRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createProjectRequest));

        // rest/jobs/{jobId} GET
        waitForJobToComplete(deploymentUrl, createProjectRequest.getJobId(), createProjectRequest.getStatus(), requestHelper);

        // rest/repositories/{repoName}/projects POST
        restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects/" + projectName + "/maven/compile");
        CompileProjectRequest compileRequest = post(restRequest, mediaType, 202, CompileProjectRequest.class);
        logger.debug("]] " + convertObjectToJsonString(compileRequest));

        // rest/jobs/{jobId} GET
        waitForJobToComplete(deploymentUrl, createProjectRequest.getJobId(), createProjectRequest.getStatus(), requestHelper);

        // TODO implement GET
        // rest/repositories/{repoName}/projects GET
        /** get projects, compare/verify that new project is in list **/

        // TODO implement DELETE
        // rest/repositories/{repoName}/projects DELETE
        /** delete projects, verify that list of projects is now one less */
    }

    private void waitForJobToComplete(URL deploymentUrl, String jobId, JobStatus jobStatus, RestRequestHelper requestHelper) throws Exception {
        waitForJobToComplete(deploymentUrl, jobId, jobStatus, requestHelper, JobStatus.SUCCESS);
    }
    
    private void waitForJobToComplete(URL deploymentUrl, String jobId, JobStatus jobStatus, RestRequestHelper requestHelper, JobStatus expectedStatus) throws Exception {
        assertEquals("Initial status of request", JobStatus.APPROVED, jobStatus);
        
        int wait = 0;
        do {
            Thread.sleep(jobCompleteSleepSecs * 1000);
            ClientRequest restRequest = requestHelper.createRequest("jobs/" + jobId);
            JobResult jobResult = get(restRequest, mediaType, JobResult.class);
            logger.debug("]] " + convertObjectToJsonString(jobResult));
            assertEquals(jobResult.getJobId(), jobId);
            jobStatus = jobResult.getStatus();
            ++wait;
        } while ((jobStatus.equals(JobStatus.ACCEPTED) || jobStatus.equals(JobStatus.APPROVED)) && wait < maxTries);

        if( wait < maxTries ) {
            assertEquals("Job does not have expected status", expectedStatus, jobStatus);
        }
        assertTrue("Too many tries!", wait < maxTries);
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
        // rest/organizaionalunits GET
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl);
       
        int origUnitsSize;
        List<OrganizationalUnit> ouList = new ArrayList<OrganizationalUnit>(2);
        {
            // rest/organizationalunits GET
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits");
            Collection<OrganizationalUnit> orgUnits = get(restRequest, mediaType, Collection.class);
            origUnitsSize = orgUnits.size();
        }

        {
            // rest/organizationalunits POST
            for( int i = 0; i < 2; ++i ) { 
                ClientRequest restRequest = requestHelper.createRequest("organizationalunits");
                OrganizationalUnit orgUnit = new OrganizationalUnit();
                orgUnit.setDescription("Smoke Tests OU");
                orgUnit.setName(UUID.randomUUID().toString());
                orgUnit.setOwner(this.getClass().getSimpleName());
                addToRequestBody(restRequest, orgUnit);
                CreateOrganizationalUnitRequest createOURequest = post(restRequest, mediaType, 202, CreateOrganizationalUnitRequest.class);

                // rest/jobs/{jobId}
                waitForJobToComplete(deploymentUrl, createOURequest.getJobId(), createOURequest.getStatus(), requestHelper);

                ouList.add(orgUnit);
            }
        }

        {
            // rest/organizaionalunits GET
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits");
            Collection<OrganizationalUnit> orgUnits = get(restRequest, mediaType, Collection.class);
            assertEquals("Exepcted an OU to be added.", origUnitsSize + 2, orgUnits.size());
        }

        String repoName = UUID.randomUUID().toString();
        {
            // rest/repositories POST
            ClientRequest restRequest = requestHelper.createRequest("repositories");
            RepositoryRequest newRepo = new RepositoryRequest();
            newRepo.setName(repoName);
            newRepo.setDescription("repo for testing rest services");
            newRepo.setRequestType("new");
            newRepo.setOrganizationlUnitName(ouList.get(0).getName());
            addToRequestBody(restRequest, newRepo);

            CreateOrCloneRepositoryRequest createRepoRequest = post(restRequest, mediaType, 202, CreateOrCloneRepositoryRequest.class);
            logger.debug("]] " + convertObjectToJsonString(createRepoRequest));
            assertNotNull("create repo job request", createRepoRequest);
            assertEquals("job request status", JobStatus.APPROVED, createRepoRequest.getStatus());

            // rest/jobs/{jobId}
            waitForJobToComplete(deploymentUrl, createRepoRequest.getJobId(), createRepoRequest.getStatus(), requestHelper);
        }
       
        {
            // rest/organizationalunits/{ou}/repositories/{repoName} POST
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits/" + ouList.get(1).getName() + "/repositories/" + repoName);

            AddRepositoryToOrganizationalUnitRequest addRepoToOuRequest = post(restRequest, mediaType, 202, AddRepositoryToOrganizationalUnitRequest.class);
            logger.debug("]] " + convertObjectToJsonString(addRepoToOuRequest));
            assertNotNull("add repo to ou job request", addRepoToOuRequest);
            assertEquals("job request status", JobStatus.APPROVED, addRepoToOuRequest.getStatus());

            // rest/jobs/{jobId}
            waitForJobToComplete(deploymentUrl, addRepoToOuRequest.getJobId(), addRepoToOuRequest.getStatus(), requestHelper);
        }

        {
            // rest/organizationalunits/{ou} GET
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits/" + ouList.get(1).getName());

            OrganizationalUnit orgUnitRequest = get(restRequest, mediaType, OrganizationalUnit.class);
            logger.debug("]] " + convertObjectToJsonString(orgUnitRequest));
            assertNotNull("organizational unit request", orgUnitRequest);

            assertTrue("repository has not been added to organizational unit", orgUnitRequest.getRepositories().contains(repoName));
        }

        {
            // rest/organizationalunits/{ou}/repositories/{repoName} DELETE
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits/" + ouList.get(1).getName() + "/repositories/" + repoName);
            RemoveRepositoryFromOrganizationalUnitRequest remRepoFromOuRquest = delete(restRequest, mediaType, 202, RemoveRepositoryFromOrganizationalUnitRequest.class);
            logger.debug("]] " + convertObjectToJsonString(remRepoFromOuRquest));
            assertNotNull("delete repo from ou job request", remRepoFromOuRquest);
            assertEquals("job request status", JobStatus.APPROVED, remRepoFromOuRquest.getStatus());

            // rest/jobs/{jobId}
            waitForJobToComplete(deploymentUrl, remRepoFromOuRquest.getJobId(), remRepoFromOuRquest.getStatus(), requestHelper);
        }

        {
            // rest/organizationalunits/{ou} GET
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits/" + ouList.get(1).getName());
            OrganizationalUnit orgUnitRequest = get(restRequest, mediaType, OrganizationalUnit.class);
            logger.debug("]] " + convertObjectToJsonString(orgUnitRequest));
            assertNotNull("organizational unit request", orgUnitRequest);

            assertFalse("repository should have been deleted from organizational unit", orgUnitRequest.getRepositories().contains(repoName));
        }
        
        {
            // rest/organizationalunits/{ou} DELETE
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits/" + ouList.get(1).getName());
            RemoveOrganizationalUnitRequest removeOrgUnitRequest = delete(restRequest, mediaType, 202, RemoveOrganizationalUnitRequest.class);
            logger.debug("]] " + convertObjectToJsonString(removeOrgUnitRequest));
            assertNotNull("organizational unit request", removeOrgUnitRequest);
            waitForJobToComplete(deploymentUrl, removeOrgUnitRequest.getJobId(), removeOrgUnitRequest.getStatus(), requestHelper);

        }
        
        {
            // verify the OU was deleted - the GET request should return 404
            ClientRequest restRequest = requestHelper.createRequest("organizationalunits/" + ouList.get(1).getName());
            get(restRequest, mediaType, 404, String.class);
        }
    }
}
