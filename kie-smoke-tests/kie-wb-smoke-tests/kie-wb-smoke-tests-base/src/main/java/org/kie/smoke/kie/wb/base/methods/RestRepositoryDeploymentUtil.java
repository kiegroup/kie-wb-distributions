package org.kie.smoke.kie.wb.base.methods;

import static org.junit.Assert.fail;
import static org.kie.smoke.tests.util.RestUtil.delete;
import static org.kie.smoke.tests.util.RestUtil.get;
import static org.kie.smoke.tests.util.RestUtil.post;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

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
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.services.client.api.RestRequestHelper;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentJobResult;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnit;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnit.JaxbDeploymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * With thanks to Ivo Bek, Radovan Synek, Marek Baluch, Jiri Locker, Luask Petrovicky.
 * </p>
 * Copied from the RestWorkbenchClient and BusinessCentral classes and then modified.
 */
public class RestRepositoryDeploymentUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestRepositoryDeploymentUtil.class);

    private final RuntimeStrategy strategy;

    private RestRequestHelper requestHelper;
    private int sleepSecs = 5;
    private int totalTries = 10;
    
    private final static MediaType jsonMediaType = MediaType.APPLICATION_JSON_TYPE;

    public RestRepositoryDeploymentUtil(URL deploymentUrl, String user, String password, RuntimeStrategy strategy) {
        requestHelper = RestRequestHelper.newInstance(deploymentUrl, user, password);
        this.strategy = strategy;
    }

    public void setSleepSeconds(int numSleepSecs) { 
        this.sleepSecs = numSleepSecs;
    }
    
    public void setTotalTries(int totalNumTries) { 
        this.totalTries = totalNumTries;
    }
    
    public void createAndDeployRepository(String repoUrl, String repositoryName, String project, String deploymentId,
            String orgUnit, String user) throws InterruptedException {
        try {
            deleteRepository(repositoryName);
        } catch (Exception ex) {
            // just ignore, we only need to have working
            // environment created by the steps below
        }

        JobRequest createRepoJob = createRepository(repositoryName, repoUrl);
        JobRequest createOrgUnitJob = createOrganizationalUnit(orgUnit, user, repositoryName);
        waitForJobsToFinish(sleepSecs, totalTries, createRepoJob, createOrgUnitJob);
        JobRequest installProjectJob = installProject(repositoryName, project);
        waitForJobsToFinish(sleepSecs, totalTries, installProjectJob);

        JaxbDeploymentJobResult deployJob = createDeploymentUnit(deploymentId, strategy);
        JaxbDeploymentUnit deployUnit = deployJob.getDeploymentUnit();
        waitForDeploymentToFinish(sleepSecs, totalTries, deployUnit);
        
        int sleep = 5;
        logger.info("Waiting {} more seconds to make sure deploy is done..", sleep);
        Thread.sleep(sleep * 1000);
    }

    // submethods ------------------------------------------------------------------------------------------------------------

    /**
     * Delete the repository with the given repository name
     * 
     * @param repositoryName
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest deleteRepository(String repositoryName) {
        logger.debug("Deleting repository '{}'", repositoryName);
        RemoveRepositoryRequest entity = 
                delete(createRequest("repositories/" + repositoryName), jsonMediaType, 202, RemoveRepositoryRequest.class);
        if (entity.getStatus() == JobStatus.ACCEPTED || entity.getStatus() == JobStatus.SUCCESS) {
            return entity;
        } else {
            throw new IllegalStateException("Delete request failed with status " + entity.getStatus());
        }
    }

    /**
     * Clone a repository in kie-wb with the given name from the given URL
     * 
     * @param repositoryName The name of the repository
     * @param cloneRepoUrl The location of the repository
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest createRepository(String repositoryName, String cloneRepoUrl) {
        logger.debug("Cloning repo '{}' from URL '{}'", repositoryName, cloneRepoUrl);
        RepositoryRequest repoRequest = new RepositoryRequest();
        repoRequest.setName(repositoryName);
        repoRequest.setRequestType("clone");
        repoRequest.setGitURL(cloneRepoUrl);
        String input = serializeToJsonString(repoRequest);
        ClientRequest request = createRequest("repositories/", input);
        return post(request, jsonMediaType, 202, CreateOrCloneRepositoryRequest.class);
    }

    /**
     * Create an organizational unit in order to manage the repository
     * 
     * @param name The name of the organizational unit
     * @param owner The owner of the organizational unit
     * @param repositories The list of repositories that the org unit should own
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest createOrganizationalUnit(String name, String owner, String... repositories) {
        logger.debug("Creating organizational unit '{}' owned by '{}' containing [{}]", name, owner, repositories);
        OrganizationalUnit ou = new OrganizationalUnit();
        ou.setRepositories(new ArrayList<String>());
        for (int i = 0; repositories != null && i < repositories.length; ++i) {
            ou.getRepositories().add(repositories[i]);
        }
        ou.setName(name);
        ou.setOwner(owner);
        String input = serializeToJsonString(ou);
        return post(createRequest("organizationalunits/", input), jsonMediaType, 202, CreateOrganizationalUnitRequest.class);
    }

    /**
     * Serialize an object to a JSON string
     * 
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
     * Do a "maven install" operation on the given project in the given repository
     * 
     * @param repositoryName The name of the repository that the project is located in
     * @param project The project to be installed
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest installProject(String repositoryName, String project) {
        logger.debug("Installing project '{}' from repo '{}'", project, repositoryName);
        ClientRequest request = createMavenOperationRequest(repositoryName, project, "install");
        return post(request, jsonMediaType, 202, InstallProjectRequest.class);
    }

    /**
     * Create a {@link ClientRequest} to do a maven operation
     * 
     * @param repositoryName The name of the repository where the project is located
     * @param project The project to do the maven operation on
     * @param operation The maven operation to be executed
     * @return The {@link ClientRequest} to be called
     */
    private ClientRequest createMavenOperationRequest(String repositoryName, String project, String operation) {
        logger.debug("Calling maven '{}' operation on project '{}' in repo '{}'", operation, project, repositoryName);
        return createRequest("repositories/" + repositoryName + "/projects/" + project + "/maven/" + operation);
    }

    /**
     * Remove (undeploy) the deployment unit specificed
     * 
     * @param deploymentId The deployment unit id
     * @return A {@link JaxbDeploymentJobResult} with the initial status of the request
     */
    private JaxbDeploymentJobResult removeDeploymentUnit(String deploymentId) {
        logger.debug("Undeploying '{}'", deploymentId);
        ClientRequest request = createRequest("deployment/" + deploymentId + "/undeploy");
        return post(request, jsonMediaType, 202, JaxbDeploymentJobResult.class);
    }

    /**
     * Create (deploy) the deployment unit specified
     * 
     * @param deploymentId The deployment unit id
     * @param strategy The strategy to deploy the deployment unit with
     * @return A {@link JaxbDeploymentJobResult} with the initial status of the request
     */
    private JaxbDeploymentJobResult createDeploymentUnit(String deploymentId, RuntimeStrategy strategy) {
        logger.debug("Deploying '{}'", deploymentId);
        String opUrl = "deployment/" + deploymentId + "/deploy";
        if (strategy != null) {
            opUrl += "?strategy=" + strategy.toString();
        }

        ClientRequest request = createRequest(opUrl);
        JaxbDeploymentJobResult jr = post(request, jsonMediaType, 202, JaxbDeploymentJobResult.class);

        return jr;
    }

    /**
     * Repeatedly query the remote REST API to check whether or not all of the jobs specified in the given
     * list of {@link JobRequest}'s is complete.
     * 
     * @param sleepSecs The number of seconds to sleep between each round of querying.
     * @param totalTries The number of times to query the remote API in total
     * @param requests A list of {@link JobRequest} instances
     */
    // With java 8, this would be SOOOO much shorter and easier.. :/
    private <R, S> void waitForJobsToFinish(int sleepSecs, int totalTries, JobRequest... requests) {
        Map<String, JobStatus> requestStatusMap = new HashMap<String, JobStatus>();

        int allDone = 0;
        int tryCount = 0;
        Set<String> incompleteRequests = new HashSet<String>();

        while (allDone < requests.length && tryCount < totalTries) {
            for (JobRequest request : requests) {
                if (tryCount == 0) {
                    incompleteRequests.add(request.getJobId());
                }
                String jobId = request.getJobId();
                JobStatus jobStatus = requestStatusMap.get(jobId);
                if (JobStatus.SUCCESS.equals(jobStatus)) {
                    ++allDone;
                    incompleteRequests.remove(request.getJobId());
                    continue;
                }
                ClientRequest restRequest = createRequest("jobs/" + jobId);
                JobResult jobResult = get(restRequest, jsonMediaType, JobResult.class);
                requestStatusMap.put(jobId, jobResult.getStatus());
            }
            ++tryCount;
            try {
                Thread.sleep(sleepSecs * 1000);
            } catch (Exception e) {
                logger.error("Unable to sleep: " + e.getMessage(), e);
            }
        }

        if (tryCount == totalTries) {
            StringBuilder requestIds = new StringBuilder();
            Object[] incompleteReqArr = incompleteRequests.toArray();
            if( incompleteReqArr.length > 0 ) { 
                requestIds.append(incompleteReqArr[0]);
                if (incompleteReqArr.length > 1) {
                    for (int i = 1; i < incompleteReqArr.length; ++i) {
                        requestIds.append(", " + incompleteReqArr[i]);
                    }
                }
                fail("Waiting for the following job requests to complete timed out: " + requestIds.toString());
            }
        }
    }

    /**
     * Repeatedly query the remote REST API to check whether or not all of the deployment units specified in the given
     * list of {@link JaxbDeploymentUnit}'s have been deployed.
     * 
     * @param sleepSecs The number of seconds to sleep between each round of querying.
     * @param totalTries The number of times to query the remote API in total
     * @param requests A list of {@link JobRequest} instances
     */
    // With java 8, this would be SOOOO much shorter and easier.. :/
    private void waitForDeploymentToFinish(int sleepSecs, int totalTries, JaxbDeploymentUnit... deployUnits) {
        Map<String, JaxbDeploymentStatus> requestStatusMap = new HashMap<String, JaxbDeploymentStatus>();

        int allDone = 0;
        int tryCount = 0;
        Set<String> incompleteRequests = new HashSet<String>();

        while (allDone < deployUnits.length && tryCount < totalTries) {
            for (JaxbDeploymentUnit deployUnit : deployUnits) {
                String deployId = deployUnit.getIdentifier();
                if (tryCount == 0) {
                    incompleteRequests.add(deployId);
                }
                JaxbDeploymentStatus jobStatus = requestStatusMap.get(deployId);
                if (JaxbDeploymentStatus.DEPLOYED.equals(jobStatus)) {
                    incompleteRequests.remove(deployId);
                    ++allDone;
                    continue;
                }
                ClientRequest restRequest = createRequest("deployment/" + deployId);
                JaxbDeploymentUnit requestedDeployUnit = get(restRequest, jsonMediaType, JaxbDeploymentUnit.class);
                requestStatusMap.put(deployId, requestedDeployUnit.getStatus());
            }
            ++tryCount;
            try {
                Thread.sleep(sleepSecs * 1000);
            } catch (Exception e) {
                logger.error("Unable to sleep: " + e.getMessage(), e);
            }
        }

        if (tryCount == totalTries) {
            StringBuilder deployIds = new StringBuilder();
            Object[] incompleteDepArr = incompleteRequests.toArray();
            deployIds.append(incompleteDepArr[0]);
            if (incompleteDepArr.length > 1) {
                for (int i = 1; i < incompleteDepArr.length; ++i) {
                    deployIds.append(", " + incompleteDepArr[i]);
                }
            }
            fail("Waiting for the following deployments to deploy timed out: " + deployIds.toString());
        }
    }

    // Helper methods -------------------------------------------------------------------------------------------------------------

    /**
     * Create a {@link ClientRequest} to be called
     * 
     * @param relativeUrl The url of the REST call to be made, relative to the ../rest/ base
     * @return
     */
    private ClientRequest createRequest(String relativeUrl) {
        return requestHelper.createRequest(relativeUrl);
    }

    private ClientRequest createRequest(String resourcePath, String body) {
        return createRequest(resourcePath).body(jsonMediaType, body);
    }

}
