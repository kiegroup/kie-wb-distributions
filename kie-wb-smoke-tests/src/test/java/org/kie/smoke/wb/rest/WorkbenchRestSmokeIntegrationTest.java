package org.kie.smoke.wb.rest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.guvnor.rest.client.AddRepositoryToOrganizationalUnitRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.Entity;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.RepositoryResponse;
import org.jboss.resteasy.client.ClientRequest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.smoke.wb.AbstractWorkbenchIntegrationTest;
import org.kie.smoke.wb.category.KieDroolsWbSmoke;
import org.kie.smoke.wb.category.KieWbSmoke;
import org.kie.smoke.wb.util.RestRequestHelper;
import org.kie.smoke.wb.util.TestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.smoke.wb.util.RestUtil.delete;
import static org.kie.smoke.wb.util.RestUtil.get;
import static org.kie.smoke.wb.util.RestUtil.post;

@Category({KieWbSmoke.class, KieDroolsWbSmoke.class})
public class WorkbenchRestSmokeIntegrationTest extends AbstractWorkbenchIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(WorkbenchRestSmokeIntegrationTest.class);

    private final int maxTries = 10;

    private final MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

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
     * ../rest/repostitories POST
     * ../rest/jobs/{id} GET
     * ../rest/repositories/{repo}/projects POST
     *
     * @throws Exception When things go wrong..
     */
    @Test
    public void testManipulatingRepositories() throws Exception {
        // rest/repositories GET
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl);
        ClientRequest restRequest = requestHelper.createRequest("repositories");
        Collection<RepositoryResponse> repoResponses = get(restRequest, mediaType, Collection.class);
        assertTrue(repoResponses.size() > 0);
        String ufPlaygroundUrl = null;
        Iterator<?> iter = repoResponses.iterator();
        while (iter.hasNext()) {
            Map<String, String> repoRespMap = (Map<String, String>) iter.next();
            if ("uf-playground".equals(repoRespMap.get("name"))) {
                ufPlaygroundUrl = repoRespMap.get("gitURL");
            }
        }
        assertEquals("UF-Playground Git URL", "git://uf-playground", ufPlaygroundUrl);

        // rest/repositories POST
        restRequest = requestHelper.createRequest("repositories");
        RepositoryRequest newRepo = new RepositoryRequest();
        String repoName = UUID.randomUUID().toString();
        newRepo.setName(repoName);
        newRepo.setDescription("repo for testing rest services");
        newRepo.setRequestType("new");
        addToRequestBody(restRequest, newRepo);

        CreateOrCloneRepositoryRequest createJobRequest = post(restRequest, mediaType, 202, CreateOrCloneRepositoryRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createJobRequest));
        assertNotNull("create repo job request", createJobRequest);
        assertEquals("job request status", JobStatus.APPROVED, createJobRequest.getStatus());
        String jobId = createJobRequest.getJobId();

        // rest/jobs/{jobId} GET
        waitForJobToComplete(deploymentUrl, jobId, createJobRequest.getStatus(), requestHelper);

        // rest/repositories/{repoName}/projects POST
        restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects");
        Entity project = new Entity();
        project.setDescription("test project");
        String testProjectName = "test-project";
        project.setName(testProjectName);
        addToRequestBody(restRequest, project);
        CreateProjectRequest createProjectRequest = post(restRequest, mediaType, 202, CreateProjectRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createProjectRequest));

        // rest/jobs/{jobId} GET
        waitForJobToComplete(deploymentUrl, jobId, createProjectRequest.getStatus(), requestHelper);
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
        assertEquals("Initial status of request should be APPROVED", JobStatus.APPROVED, jobStatus);
        int wait = 0;
        while (jobStatus.equals(JobStatus.APPROVED) && wait < maxTries) {
            ClientRequest restRequest = requestHelper.createRequest("jobs/" + jobId);
            JobResult jobResult = get(restRequest, mediaType, JobResult.class);
            logger.debug("]] " + convertObjectToJsonString(jobResult));
            assertEquals(jobResult.getJobId(), jobId);
            jobStatus = jobResult.getStatus();
            ++wait;
            Thread.sleep(3 * 1000);
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
        ClientRequest restRequest = requestHelper.createRequest("organizationalunits");
        Collection<OrganizationalUnit> orgUnits = get(restRequest, mediaType, Collection.class);
        int origUnitsSize = orgUnits.size();

        // rest/organizaionalunits POST
        restRequest = requestHelper.createRequest("organizationalunits");
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setDescription("Test OU");
        orgUnit.setName(UUID.randomUUID().toString());
        orgUnit.setOwner(this.getClass().getSimpleName());
        addToRequestBody(restRequest, orgUnit);
        CreateOrganizationalUnitRequest createOURequest = post(restRequest, mediaType, 202, CreateOrganizationalUnitRequest.class);

        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, createOURequest.getJobId(), createOURequest.getStatus(), requestHelper);

        // rest/organizaionalunits GET
        restRequest = requestHelper.createRequest("organizationalunits");
        orgUnits = get(restRequest, mediaType, Collection.class);
        assertEquals("Exepcted an OU to be added.", origUnitsSize + 1, orgUnits.size());

        // rest/repositories POST
        restRequest = requestHelper.createRequest("repositories");
        RepositoryRequest newRepo = new RepositoryRequest();
        String repoName = UUID.randomUUID().toString();
        newRepo.setName(repoName);
        newRepo.setDescription("repo for testing rest services");
        newRepo.setRequestType("new");
        addToRequestBody(restRequest, newRepo);

        CreateOrCloneRepositoryRequest createRepoRequest = post(restRequest, mediaType, 202, CreateOrCloneRepositoryRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createRepoRequest));
        assertNotNull("create repo job request", createRepoRequest);
        assertEquals("job request status", JobStatus.APPROVED, createRepoRequest.getStatus());

        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, createRepoRequest.getJobId(), createRepoRequest.getStatus(), requestHelper);

        // rest/organizationalunits/{ou}/repositories/{repoName} POST
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName() + "/repositories/" + repoName);

        AddRepositoryToOrganizationalUnitRequest addRepoToOuRequest = post(restRequest, mediaType, 202, AddRepositoryToOrganizationalUnitRequest.class);
        logger.debug("]] " + convertObjectToJsonString(addRepoToOuRequest));
        assertNotNull("add repo to ou job request", addRepoToOuRequest);
        assertEquals("job request status", JobStatus.APPROVED, addRepoToOuRequest.getStatus());

        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, addRepoToOuRequest.getJobId(), addRepoToOuRequest.getStatus(), requestHelper);

        // rest/organizationalunits/{ou} GET
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName());

        OrganizationalUnit orgUnitRequest = get(restRequest, mediaType, OrganizationalUnit.class);
        logger.debug("]] " + convertObjectToJsonString(orgUnitRequest));
        assertNotNull("organizational unit request", orgUnitRequest);

        assertTrue("repository has not been added to organizational unit", orgUnitRequest.getRepositories().contains(repoName));

        // rest/organizationalunits/{ou}/repositories/{repoName} DELETE
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName() + "/repositories/" + repoName);
        RemoveRepositoryFromOrganizationalUnitRequest remRepoFromOuRquest = delete(restRequest, mediaType, 202, RemoveRepositoryFromOrganizationalUnitRequest.class);
        logger.debug("]] " + convertObjectToJsonString(remRepoFromOuRquest));
        assertNotNull("add repo to ou job request", remRepoFromOuRquest);
        assertEquals("job request status", JobStatus.APPROVED, remRepoFromOuRquest.getStatus());

        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, remRepoFromOuRquest.getJobId(), remRepoFromOuRquest.getStatus(), requestHelper);

        // rest/organizationalunits/{ou} GET
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName());
        orgUnitRequest = get(restRequest, mediaType, OrganizationalUnit.class);
        logger.debug("]] " + convertObjectToJsonString(orgUnitRequest));
        assertNotNull("organizational unit request", orgUnitRequest);

        assertFalse("repository should have been deleted from organizational unit", orgUnitRequest.getRepositories().contains(repoName));
    }
}
