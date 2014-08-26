package org.kie.smoke.kie.drools.wb.base.methods;

import static org.junit.Assert.*;
import static org.kie.smoke.kie.drools.wb.base.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.kie.drools.wb.base.util.TestConstants.MARY_USER;
import static org.kie.smoke.tests.util.RestUtil.delete;
import static org.kie.smoke.tests.util.RestUtil.get;
import static org.kie.smoke.tests.util.RestUtil.post;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
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
import org.guvnor.rest.client.Entity;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.RepositoryResponse;
import org.junit.Test;
import org.kie.remote.client.rest.KieRemoteHttpRequest;
import org.kie.remote.client.rest.KieRemoteHttpRequestException;
import org.kie.remote.client.rest.KieRemoteHttpResponse;
import org.kie.services.client.api.RestRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * These are various tests for the drools-wb-rest module
 */
public class KieDroolsWbRestSmokeIntegrationTestMethods {

    private static Logger logger = LoggerFactory.getLogger(KieDroolsWbRestSmokeIntegrationTestMethods.class);

    private final int maxTries = 10;
   
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    
    protected static void addToRequestBody(KieRemoteHttpRequest restRequest, Object obj) throws Exception { 
        String body = convertObjectToJsonString(obj);
        logger.debug( "]] " + body );
        restRequest.accept(MediaType.APPLICATION_JSON).body(body);
    }
   
    private <T> T get(KieRemoteHttpRequest httpRequest, Class<T> entityClass) { 
        KieRemoteHttpResponse httpResponse = httpRequest.get().response();
        assertEquals("Incorrect response status (" + httpRequest.getUri() + ")", 200, httpResponse.code() );
        return deserialize(httpResponse, entityClass);
    }
   
    private <T> T post(KieRemoteHttpRequest httpRequest, int status, Class<T> entityClass) { 
        KieRemoteHttpResponse httpResponse = httpRequest.post().response();
        assertEquals("Incorrect response status (" + httpRequest.getUri() + ")", status, httpResponse.code() );
        return deserialize(httpResponse, entityClass);
    }
   
    private <T> T delete(KieRemoteHttpRequest httpRequest, int status, Class<T> entityClass) { 
        KieRemoteHttpResponse httpResponse = httpRequest.delete().response();
        assertEquals("Incorrect response status (" + httpRequest.getUri() + ")", status, httpResponse.code() );
        return deserialize(httpResponse, entityClass);
    }
   
    private <T> T deserialize(KieRemoteHttpResponse httpResponse, Class<T> entityClass) { 
        String acceptHeader = httpResponse.header(HttpHeaders.CONTENT_TYPE);
        assertTrue( "Incorrect content type header: " + acceptHeader,  acceptHeader == null || MediaType.APPLICATION_JSON.equals(acceptHeader) );
        String content = httpResponse.body();
        
        T result = null;
        try {
            result = mapper.readValue(content, entityClass);
        } catch( Exception e ) {
            logger.error( "Unable to deserialze {} instance:\n{}", entityClass.getName(), content);
            fail( "Unable to deserialize json string, see log.");
        }
        return result;
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
                MARY_USER, MARY_PASSWORD,
                500, 
                MediaType.APPLICATION_JSON_TYPE);
    }
    
    /**
     * Tests the following REST urls: 
     * 
     * ../rest/repostitories GET
     * ../rest/repostitories POST
     * ../rest/jobs/{id} GET
     * ../rest/repositories/{repo}/projects POST
     * 
     * @param deploymentUrl URL of deployment
     * @throws Exception When things go wrong.. 
     */
    public void manipulatingRepositories(URL deploymentUrl) throws Exception {
        // rest/repositories GET
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl);
        KieRemoteHttpRequest restRequest = requestHelper.createRequest("repositories");
        Collection<RepositoryResponse> repoResponses = get(restRequest, Collection.class);
        assertTrue( repoResponses.size() > 0 );
        String ufPlaygroundUrl = null;
        Iterator<?> iter = repoResponses.iterator();
        while( iter.hasNext() ) { 
            Map<String, String> repoRespMap = (Map<String, String>) iter.next();
            if( "uf-playground".equals(repoRespMap.get("name")) ) { 
                ufPlaygroundUrl = repoRespMap.get("gitURL");
            }
        }
        assertEquals( "UF-Playground Git URL", "git://uf-playground", ufPlaygroundUrl );
        
        // rest/repositories POST
        restRequest = requestHelper.createRequest("repositories");
        RepositoryRequest newRepo = new RepositoryRequest();
        String repoName = UUID.randomUUID().toString();
        newRepo.setName(repoName);
        newRepo.setDescription("repo for testing rest services");
        newRepo.setRequestType("new");
        addToRequestBody(restRequest, newRepo);
        
        CreateOrCloneRepositoryRequest createJobRequest = post(restRequest, 202, CreateOrCloneRepositoryRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createJobRequest));
        assertNotNull( "create repo job request", createJobRequest);
        assertEquals( "job request status", JobStatus.ACCEPTED, createJobRequest.getStatus() );
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
        CreateProjectRequest createProjectRequest = post(restRequest, 202, CreateProjectRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createProjectRequest));
        
        // rest/jobs/{jobId} GET
        waitForJobToComplete(deploymentUrl, jobId, createProjectRequest.getStatus(), requestHelper);
    }
   
    /**
     * Tests the following REST urls: 
     * 
     * ../rest/repositories GET
     * ../rest/repositories/{repo}/projecst POST
     * ../rest/jobs/{id} GET
     *
     * @param deploymentUrl
     * @throws Exception
     */
    @Test
    public void mavenOperations(URL deploymentUrl) throws Exception { 
        // rest/repositories GET
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl);
        KieRemoteHttpRequest restRequest = requestHelper.createRequest("repositories");
        Collection<Map<String, String>> repoResponses = get(restRequest, Collection.class);
        assertTrue( repoResponses.size() > 0 );
        String repoName = repoResponses.iterator().next().get("name");
       
        // rest/repositories/{repoName}/projects POST
        restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects");
        Entity project = new Entity();
        project.setDescription("test project");
        String projectName = UUID.randomUUID().toString();
        project.setName(projectName);
        addToRequestBody(restRequest, project);

        CreateProjectRequest createProjectRequest = post(restRequest, 202, CreateProjectRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createProjectRequest));
        
        // rest/jobs/{jobId} GET
        waitForJobToComplete(deploymentUrl, createProjectRequest.getJobId(), createProjectRequest.getStatus(), requestHelper);

        // rest/repositories/{repoName}/projects POST
        restRequest = requestHelper.createRequest("repositories/" + repoName + "/projects/" + projectName + "/maven/compile");
        CompileProjectRequest compileRequest = post(restRequest, 202, CompileProjectRequest.class);
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
        assertEquals( "Initial status of request should be ACCEPTED", JobStatus.ACCEPTED, jobStatus );
        int wait = 0;
        while( jobStatus.equals(JobStatus.ACCEPTED) && wait < maxTries ) {
            KieRemoteHttpRequest restRequest = requestHelper.createRequest("jobs/" + jobId);
            JobResult jobResult = get(restRequest, JobResult.class);
            logger.debug( "]] " + convertObjectToJsonString(jobResult) );
            assertEquals( jobResult.getJobId(), jobId );
            jobStatus = jobResult.getStatus();
            ++wait;
            Thread.sleep(3*1000);
        }
        assertTrue( "Too many tries!", wait < maxTries );
    }
   
    /**
     * Tests the following REST urls: 
     * 
     * ../rest/organizationalunits GET
     * ../rest/organizationalunits POST
     * 
     * @param deploymentUrl
     * @throws Exception
     */
    @Test
    public void manipulatingOUs(URL deploymentUrl) throws Exception { 
        // rest/organizaionalunits GET
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl);
        KieRemoteHttpRequest restRequest = requestHelper.createRequest("organizationalunits");
        Collection<OrganizationalUnit> orgUnits = get(restRequest, Collection.class);
        int origUnitsSize = orgUnits.size();
        
        // rest/organizaionalunits POST
        restRequest = requestHelper.createRequest("organizationalunits");
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setDescription("Test OU");
        orgUnit.setName(UUID.randomUUID().toString());
        orgUnit.setOwner(this.getClass().getSimpleName());
        addToRequestBody(restRequest, orgUnit);
        CreateOrganizationalUnitRequest createOURequest = post(restRequest, 202, CreateOrganizationalUnitRequest.class);

        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, createOURequest.getJobId(), createOURequest.getStatus(), requestHelper);
       
        // rest/organizaionalunits GET
        restRequest = requestHelper.createRequest("organizationalunits");
        orgUnits = get(restRequest, Collection.class);
        assertEquals( "Exepcted an OU to be added.", origUnitsSize + 1, orgUnits.size());
        
        // rest/repositories POST
        restRequest = requestHelper.createRequest("repositories");
        RepositoryRequest newRepo = new RepositoryRequest();
        String repoName = UUID.randomUUID().toString();
        newRepo.setName(repoName);
        newRepo.setDescription("repo for testing rest services");
        newRepo.setRequestType("new");
        addToRequestBody(restRequest, newRepo);
        
        CreateOrCloneRepositoryRequest createRepoRequest = post(restRequest, 202, CreateOrCloneRepositoryRequest.class);
        logger.debug("]] " + convertObjectToJsonString(createRepoRequest));
        assertNotNull( "create repo job request", createRepoRequest);
        assertEquals( "job request status", JobStatus.ACCEPTED, createRepoRequest.getStatus() );
                
        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, createRepoRequest.getJobId(), createRepoRequest.getStatus(), requestHelper);
       
        // rest/organizationalunits/{ou}/repositories/{repoName} POST
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName() + "/repositories/" + repoName);
        
        AddRepositoryToOrganizationalUnitRequest addRepoToOuRequest = post(restRequest, 202, AddRepositoryToOrganizationalUnitRequest.class);
        logger.debug("]] " + convertObjectToJsonString(addRepoToOuRequest));
        assertNotNull( "add repo to ou job request", addRepoToOuRequest);
        assertEquals( "job request status", JobStatus.ACCEPTED, addRepoToOuRequest.getStatus() );
        
        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, addRepoToOuRequest.getJobId(), addRepoToOuRequest.getStatus(), requestHelper);
       
        // rest/organizationalunits/{ou} GET
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName() );
        
        OrganizationalUnit orgUnitRequest = get(restRequest, OrganizationalUnit.class);
        logger.debug("]] " + convertObjectToJsonString(orgUnitRequest));
        assertNotNull( "organizational unit request", orgUnitRequest);
        
        assertTrue( "repository has not been added to organizational unit", orgUnitRequest.getRepositories().contains(repoName));
        
        // rest/organizationalunits/{ou}/repositories/{repoName} DELETE
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName() + "/repositories/" + repoName);
        RemoveRepositoryFromOrganizationalUnitRequest remRepoFromOuRquest = delete(restRequest, 202, RemoveRepositoryFromOrganizationalUnitRequest.class);
        logger.debug("]] " + convertObjectToJsonString(remRepoFromOuRquest));
        assertNotNull( "add repo to ou job request", remRepoFromOuRquest);
        assertEquals( "job request status", JobStatus.ACCEPTED, remRepoFromOuRquest.getStatus() );
        
        // rest/jobs/{jobId}
        waitForJobToComplete(deploymentUrl, remRepoFromOuRquest.getJobId(), remRepoFromOuRquest.getStatus(), requestHelper);
        
        // rest/organizationalunits/{ou} GET
        restRequest = requestHelper.createRequest("organizationalunits/" + orgUnit.getName() );
        orgUnitRequest = get(restRequest, OrganizationalUnit.class);
        logger.debug("]] " + convertObjectToJsonString(orgUnitRequest));
        assertNotNull( "organizational unit request", orgUnitRequest);
        
        assertFalse( "repository should have been deleted from organizational unit", orgUnitRequest.getRepositories().contains(repoName));
    }

}
