package org.kie.smoke.wb.util;

import static org.kie.smoke.wb.util.RestUtil.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.RemoveRepositoryRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.ReaderException;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentJobResult;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnit;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnit.JaxbDeploymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * With thanks to Ivo Bek, Radovan Synek, Marek Baluch, Jiri Locker, Lukas Petrovicky.
 * </p> 
 * Copied from the RestWorkbenchClient and BusinessCentral classes and then modified. 
 */
public class RestRepositoryDeploymentUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestRepositoryDeploymentUtil.class);
   
    public final RuntimeStrategy strategy;
    private RestRequestHelper requestHelper;
    private static final MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
    
    private int sleepSecs = 3;
    private int totalTries = 3;
    
    public RestRepositoryDeploymentUtil(URL deploymentUrl, String user, String password, RuntimeStrategy strategy) { 
        requestHelper = RestRequestHelper.newInstance(deploymentUrl, user, password);
        if( strategy != null ) { 
            this.strategy = strategy;
        } else { 
            this.strategy = RuntimeStrategy.SINGLETON;
        }
    }
    
    public void createAndDeployRepository(String repoUrl, String repositoryName, String project, String deploymentId, String orgUnitName, String user) { 
        try {
            deleteRepository(repositoryName);
        } catch (Exception ex) {
            // just ignore, we only need to have working
            // environment created by the steps below
        }

        JobRequest createOrgUnitJob = createOrganizationalUnit(orgUnitName, this.getClass().getSimpleName(), orgUnitName, user);
        waitForJobToFinish(createOrgUnitJob.getJobId(), "jobs/", JobResult.class, JobStatus.SUCCESS);
        JobRequest createRepoJob = createRepository(repositoryName, orgUnitName, repoUrl);
        waitForJobToFinish(createRepoJob.getJobId(), "jobs/", JobResult.class, JobStatus.SUCCESS);
        
        JaxbDeploymentJobResult deployJob = createDeploymentUnit(deploymentId, strategy);
        JaxbDeploymentUnit deployUnit = deployJob.getDeploymentUnit();    
        waitForJobToFinish(deployUnit.getIdentifier(), "deployment/", JaxbDeploymentJobResult.class, JaxbDeploymentStatus.DEPLOYED);
    }
  
    public void setSleepSeconds(int sleepSecs) { 
        this.sleepSecs = sleepSecs;
    }
    
    public void setTotalTries(int totalTries) { 
       this.totalTries = totalTries; 
    }
    
    // submethods ------------------------------------------------------------------------------------------------------------
   
    /**
     * Delete the repository with the given repository name
     * @param repositoryName
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest deleteRepository(String repositoryName) { 
        logger.info("Deleting repository '{}'", repositoryName);
        ClientRequest restRequest = createRequest("repositories/" + repositoryName);
        RemoveRepositoryRequest entity = delete(restRequest, mediaType, 202, RemoveRepositoryRequest.class);
        if (entity.getStatus() == JobStatus.ACCEPTED || entity.getStatus() == JobStatus.SUCCESS) {
            return entity;
        } else {
            throw new IllegalStateException("Delete request failed with status " +  entity.getStatus() );
        }
    }
   
    /**
     * Clone a repository in kie-wb with the given name from the given URL
     * @param repositoryName The name of the repository
     * @param cloneRepoUrl The location of the repository
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest createRepository(String repositoryName, String orgUnit, String cloneRepoUrl) {
        logger.info("Cloning repo '{}' from URL '{}'", repositoryName, cloneRepoUrl);
        RepositoryRequest repoRequest = new RepositoryRequest();
        repoRequest.setName(repositoryName);
        repoRequest.setRequestType("clone");
        repoRequest.setGitURL(cloneRepoUrl);
        repoRequest.setOrganizationlUnitName(orgUnit);
        String input = serializeToJsonString(repoRequest);
        ClientRequest request = createRequest("repositories/", input);
        return post( request, mediaType, 202, CreateOrCloneRepositoryRequest.class);
    }
  
    /**
     * Create an organizational unit in order to manage the repository
     * @param name The name of the organizational unit
     * @param owner The owner of the organizational unit
     * @param repositories The list of repositories that the org unit should own
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest createOrganizationalUnit(String name, String owner, String defaultGroupId, String... repositories) {
        logger.info("Creating organizational unit '{}' owned by '{}' with default group id {}, containing [{}]",
                        name, owner, defaultGroupId, repositories);
        OrganizationalUnit ou = new OrganizationalUnit();
        ou.setRepositories(new ArrayList<String>());
        for (int i = 0; repositories != null && i < repositories.length; ++i) {
            ou.getRepositories().add(repositories[i]);
        }
        ou.setName(name);
        ou.setOwner(owner);
        ou.setDefaultGroupId( defaultGroupId );
        String input = serializeToJsonString(ou);
        ClientRequest restRequest = createRequest("organizationalunits/", input);
        return post(restRequest, mediaType, 202, CreateOrganizationalUnitRequest.class);
    }

    /**
     * Serialize an object to a JSON string
     * @param object The object to be serialized
     * @return The JSON {@link String} instance
     */
    private String serializeToJsonString(Object object) { 
        String input = null;
        try {
            input = new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize " + object.getClass().getSimpleName(), e);
        }
        return input;
    }
   
    /**
     * Create (deploy) the deployment unit specified
     * @param deploymentId The deployment unit id
     * @param strategy The strategy to deploy the deployment unit with 
     * @return A {@link JaxbDeploymentJobResult} with the initial status of the request
     */
    private JaxbDeploymentJobResult createDeploymentUnit(String deploymentId, RuntimeStrategy strategy) { 
        logger.info("Deploying '{}'", deploymentId);
        String opUrl = "deployment/" + deploymentId + "/deploy";
        if (strategy != null ) { 
            opUrl += "?strategy=" + strategy.toString();
        }
       
        ClientRequest request = createRequest(opUrl);
        JaxbDeploymentJobResult jr = post(request, mediaType, 202, JaxbDeploymentJobResult.class); 
        
        return jr;
    }
 
    private <T,S> void waitForJobToFinish(String jobId, String url, Class<T> resultClass, S expectedStatus ) { 
       S lastStatus = null;
       
       boolean notDone = true;
       int tryCount = 0;
       while( notDone && tryCount < totalTries ) { 
           if( expectedStatus.equals(lastStatus) ) { 
                  notDone = false;
                  continue;
           }
           ClientRequest restRequest = createRequest( url + jobId );
           T jobResult = get(restRequest, mediaType, resultClass);
           try { 
               Method getStatusMethod = resultClass.getMethod("getStatus");
               lastStatus = (S) getStatusMethod.invoke(jobResult);
           } catch( Exception e ) { 
               fail("Unable to get status from request response: "  + e.getMessage());
           }
       }
       ++tryCount;
       try { 
           logger.info("Sleeping {} while waiting for GET {}{} => {} (expecting {})", 
                   sleepSecs, url, jobId, resultClass.getSimpleName(), expectedStatus );
           Thread.sleep(sleepSecs*1000);
       } catch( Exception e ) { 
           logger.error("Unable to sleep: " + e.getMessage(), e);
       }
    }
   
    // Helper methods -------------------------------------------------------------------------------------------------------------
   
    /**
     * Create a {@link ClientRequest} to be called
     * @param relativeUrl The url of the REST call to be made, relative to the ../rest/ base
     * @return
     */
    private ClientRequest createRequest(String relativeUrl) { 
       requestHelper.setMediaType(mediaType);
       return requestHelper.createRequest(relativeUrl);
    }
    
    private ClientRequest createRequest(String resourcePath, String body) {
        return createRequest(resourcePath).body(mediaType, body);
    }

}
