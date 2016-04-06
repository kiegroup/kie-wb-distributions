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

package org.kie.smoke.wb.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.RemoveRepositoryRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.RepositoryResponse;
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

    private final URL deploymentUrl;
    private final String user;
    private final String password;
    private int sleepSecs;
    private RuntimeStrategy strategy;
    
    private int totalTries = 10;

    private final String contentType = MediaType.APPLICATION_JSON;
    
    public RestRepositoryDeploymentUtil(URL deploymentUrl, String user, String password, int sleepSecs, RuntimeStrategy strategy) { 
        this.sleepSecs = sleepSecs;
        assertTrue( "Sleep/timeout period is too short: " + this.sleepSecs, this.sleepSecs > 2 );
        this.deploymentUrl = deploymentUrl;
        this.user = user;
        this.password = password;
        this.strategy = strategy;
    }
   
    public void setTotalTries(int tries) { 
        this.totalTries = tries;
    }
   
    public void createRepositoryAndDeployProject(String repoUrl, String repositoryName, String project, String deploymentId, String orgUnitName, String user) { 
        if( repositoryExists(repositoryName) ) {
            JobRequest delRepoJob = deleteRepository(repositoryName);
            waitForJobsToFinish(sleepSecs, delRepoJob);
        }

        if( ! organizationalUnitExists(orgUnitName) ) {
            JobRequest createOrgUnitJob = createOrganizationalUnit(orgUnitName, user);
            waitForJobsToFinish(sleepSecs, createOrgUnitJob);
        }
        
        JobRequest createRepoJob = createRepository(repositoryName, orgUnitName, repoUrl);
        waitForJobsToFinish(sleepSecs, createRepoJob);

        // Extra wait..
        try {
            Thread.sleep(5000); // TODO don't use hardcoded wait, but rather polling
        } catch (Exception e) {
            // no op
        }

        deploy(deploymentId);
    }
  
    public void undeploy(String deploymentId) { 
        JaxbDeploymentJobResult deployJob = removeDeploymentUnit(deploymentId);
        waitForDeploymentToFinish(sleepSecs, false, deployJob.getDeploymentUnit());
    }
    
    public void deploy(String deploymentId) { 
        JaxbDeploymentJobResult deployJob = createDeploymentUnit(deploymentId, strategy);
        waitForDeploymentToFinish(sleepSecs, true, deployJob.getDeploymentUnit());    
    }
    
    // submethods ------------------------------------------------------------------------------------------------------------
  
    /**
     * @param repositoryName
     * @return whether or not the repository with the given name (sometimes called an "alias") exists
     */
    private boolean repositoryExists(String repositoryName) { 
        Collection<RepositoryResponse> repos = get("repositories/", 200, Collection.class, RepositoryResponse.class);
        for( RepositoryResponse repo : repos ) { 
            if( repo.getName().equals(repositoryName) ) { 
                return true;
            }
        }
        return false;
    }
   
    
    /**
     * Delete the repository with the given repository name
     * @param repositoryName
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest deleteRepository(String repositoryName) { 
        logger.info("Deleting repository '{}'", repositoryName);
        RemoveRepositoryRequest entity = delete("repositories/" + repositoryName, 202, RemoveRepositoryRequest.class);
        if (entity.getStatus().equals(JobStatus.ACCEPTED) || entity.getStatus().equals(JobStatus.SUCCESS) || entity.getStatus().equals(JobStatus.APPROVED)) {
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
        repoRequest.setOrganizationalUnitName(orgUnit);
        
        return post( "repositories", repoRequest, 202, CreateOrCloneRepositoryRequest.class);
    }

    public boolean checkRepositoryExistence(String repositoryName) { 
        logger.info("Checking existence of repo '{}'", repositoryName);
      
        try { 
            get( "repositories/" + repositoryName, 200, RepositoryResponse.class );
        } catch( IllegalStateException ise ) { 
            if( ise.getMessage().contains("code: 404") ) { 
                return false;
            }
            throw ise;
        }
        return true;
    }
   
    
    /**
     * Create an organizational unit in order to manage the repository
     * @param name The name of the organizational unit
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private OrganizationalUnit getOrganizaionalUnit(String name) {
        OrganizationalUnit orgUnit = null;
        try { 
            orgUnit = get("organizationalunits/" + name, 200, OrganizationalUnit.class);
        } catch( IllegalStateException ise ) { 
            String errMsg = ise.getMessage();
           assertTrue( errMsg, errMsg.contains("code: 404"));
        }
        return orgUnit;
    }
   
    
    /**
     * Create an organizational unit in order to manage the repository
     * @param orgUnitName The name of the organizational unit
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private boolean organizationalUnitExists(String orgUnitName) { 
        Collection<OrganizationalUnit> orgUnits = get("organizationalunits/", 200, Collection.class, OrganizationalUnit.class);
        for( OrganizationalUnit orgUnit : orgUnits ) { 
           if( orgUnit.getName().equals(orgUnitName) ) { 
               return true;
           }
        }
        return false;
    }
    
    /**
     * Create an organizational unit in order to manage the repository
     * @param name The name of the organizational unit
     * @param owner The owner of the organizational unit
     * @param repositories The list of repositories that the org unit should own
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest createOrganizationalUnit(String name, String owner, String... repositories) {
        logger.info("Creating organizational unit '{}' owned by '{}' containing [{}]", name, owner, repositories);
        OrganizationalUnit ou = new OrganizationalUnit();
        ou.setRepositories(new ArrayList<String>());
        for (int i = 0; repositories != null && i < repositories.length; ++i) {
            ou.getRepositories().add(repositories[i]);
        }
        ou.setName(name);
        ou.setOwner(owner);

        return post("organizationalunits/", ou, 202, CreateOrganizationalUnitRequest.class);
    }

    /**
     * Do a "maven install" operation on the given project in the given repository
     * @param repositoryName The name of the repository that the project is located in
     * @param project The project to be installed
     * @return A {@link JobRequest} instance returned by the request with the initial status of the request
     */
    private JobRequest installProject(String repositoryName, String project) {
        logger.info("Installing project '{}' from repo '{}'", project, repositoryName);
        String mavenOpRelUrl = createMavenOperationRequest(repositoryName, project, "install");
        return post(mavenOpRelUrl, 202, InstallProjectRequest.class);
    }

    /**
     * Creates request URI for specified maven operation
     * @param repositoryName The name of the repository where the project is located
     * @param project The project to do the maven operation on 
     * @param operation The maven operation to be executed
     */
    private String createMavenOperationRequest(String repositoryName, String project, String operation) {
        return "repositories/" + repositoryName + "/projects/" + project + "/maven/" + operation;
    }
    
    /**
     * Remove (undeploy) the deployment unit specificed
     * @param deploymentId The deployment unit id
     * @return A {@link JaxbDeploymentJobResult} with the initial status of the request
     */
    private JaxbDeploymentJobResult removeDeploymentUnit(String deploymentId) {
        logger.info("Undeploying '{}'", deploymentId);
        return post("deployment/" + deploymentId + "/undeploy", 202, JaxbDeploymentJobResult.class);
    }

    public JaxbDeploymentUnit getDeploymentUnit(String deploymentId) { 
        logger.info("Getting info on '{}'", deploymentId);
        return get("deployment/" + deploymentId , 200, JaxbDeploymentUnit.class); 
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
       
        JaxbDeploymentJobResult deployJobResult = post(opUrl, 202, JaxbDeploymentJobResult.class); 
        
        return deployJobResult;
    }
 
    // With java 8, this would be SOOOO much shorter and easier.. :/ 
    private <R,S> void waitForJobsToFinish(int sleepSecs, JobRequest ...requests ) { 
       Map<String, JobStatus> requestStatusMap = new HashMap<String, JobStatus>();
     
       int tryCount = 0;
       List<JobRequest> checkRequests = new ArrayList<JobRequest>(Arrays.asList(requests));
       while( ! checkRequests.isEmpty() && tryCount < totalTries ) { 
           List<JobRequest> done = new ArrayList<JobRequest>(checkRequests.size());
           for( JobRequest request : requests ) { 
               String jobId = request.getJobId();
               JobStatus jobStatus  = requestStatusMap.get(jobId);
               if( jobStatus == null ) { 
                   JobResult jobResult = get( "jobs/" + jobId, 200, JobResult.class); 
                   jobStatus = jobResult.getStatus();
               }
               if( JobStatus.SUCCESS.equals(jobStatus) ) { 
                  done.add(request);
                  continue;
               }
               if( JobStatus.FAIL.equals(jobStatus) ) { 
                   fail( "Job " + jobId + " failed!");
               }
               JobResult jobResult = get( "jobs/" + jobId, 200, JobResult.class);
               requestStatusMap.put(jobId, jobResult.getStatus());
           }
           checkRequests.removeAll(done);
           if( checkRequests.isEmpty()) { 
               break;
           }
           ++tryCount;
           try { 
               Thread.sleep(sleepSecs*1000);
           } catch( Exception e ) { 
               logger.error("Unable to sleep: " + e.getMessage(), e);
           }
       }
    }
   
    // With java 8, this would be SOOOO much shorter and easier.. :/ 
    public void waitForDeploymentToFinish(int sleepSecs, boolean deploy, JaxbDeploymentUnit ...deployUnits ) { 
        Map<String, JaxbDeploymentStatus> requestStatusMap = new HashMap<String, JaxbDeploymentStatus>();
      
        int tryCount = 0;
        List<JaxbDeploymentUnit> deployRequests = new ArrayList<JaxbDeploymentUnit>(Arrays.asList(deployUnits));
        while( ! deployRequests.isEmpty() && tryCount < totalTries ) { 
            List<JaxbDeploymentUnit> done = new ArrayList<JaxbDeploymentUnit>(deployRequests.size());
            for( JaxbDeploymentUnit deployUnit : deployUnits ) { 
                String deployId = deployUnit.getIdentifier();
                JaxbDeploymentStatus jobStatus  = requestStatusMap.get(deployId);
                if( jobStatus == null ) { 
                    JaxbDeploymentUnit requestedDeployUnit = get("deployment/" + deployId, 200, JaxbDeploymentUnit.class);
                    jobStatus = requestedDeployUnit.getStatus();
                }
                if( deploy ) { 
                    if( JaxbDeploymentStatus.DEPLOYED.equals(jobStatus) ) { 
                        done.add(deployUnit);
                        continue;
                    } else if( JaxbDeploymentStatus.DEPLOY_FAILED.equals(jobStatus) ) { 
                        fail( "Deploy of " + deployId + " failed!");
                    }
                } else { 
                    if( JaxbDeploymentStatus.UNDEPLOYED.equals(jobStatus) ) { 
                        done.add(deployUnit);
                        continue;
                    } else if( JaxbDeploymentStatus.UNDEPLOY_FAILED.equals(jobStatus) ) { 
                        fail( "Undeploy of " + deployId + " failed!");
                    }
                }
                JaxbDeploymentUnit requestedDeployUnit = get("deployment/" + deployId, 200, JaxbDeploymentUnit.class);
                requestStatusMap.put(deployId, requestedDeployUnit.getStatus());
            }
            deployRequests.removeAll(done);
            if( deployRequests.isEmpty() ) { 
                break;
            }
            ++tryCount;
            try { 
                Thread.sleep(sleepSecs*1000);
            } catch( Exception e ) { 
                logger.error("Unable to sleep: " + e.getMessage(), e);
            }
        }
     }
    
    // Helper methods -------------------------------------------------------------------------------------------------------------
   
    private <T extends Object> T get(String relativeUrl, int status, Class... returnTypes) {
        return RestUtil.get(deploymentUrl, "rest/" + relativeUrl, contentType,
                status, user, password,
                returnTypes);
    }

    private <T extends Object> T post(String relativeUrl, int status, Class<T> returnType) {
        return RestUtil.post(deploymentUrl, "rest/" + relativeUrl, contentType,
                status, user, password,
                returnType);
    }
    
    private <T extends Object> T post(String relativeUrl, Object entity, int status, Class<T> returnType) {
        return RestUtil.postEntity(deploymentUrl, "rest/" + relativeUrl, contentType,
                status, user, password,
                entity, returnType);
    }


    private <T extends Object> T delete(String relativeUrl, int status, Class<T> returnType) {
        return RestUtil.delete(deploymentUrl, "rest/" + relativeUrl, contentType,
                status, user, password,
                returnType);
    }
    
}
