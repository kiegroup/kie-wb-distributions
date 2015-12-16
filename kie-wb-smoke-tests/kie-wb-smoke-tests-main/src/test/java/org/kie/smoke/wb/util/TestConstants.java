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

import java.util.Properties;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;

public class TestConstants {
   
    /**
     * Process ids
     */
    
    public static final String HUMAN_TASK_PROCESS_ID        = "org.test.kjar.writedocument";
    public static final String HUMAN_TASK_VAR_PROCESS_ID    = "org.test.kjar.HumanTaskWithForm";
    public static final String SCRIPT_TASK_PROCESS_ID       = "org.test.kjar.scripttask";
    public static final String SCRIPT_TASK_VAR_PROCESS_ID   = "org.test.kjar.scripttask.var";
    public static final String SINGLE_HUMAN_TASK_PROCESS_ID = "org.test.kjar.HumanTask";
    public static final String OBJECT_VARIABLE_PROCESS_ID   = "org.test.kjar.ObjectVariableProcess";
    public static final String RULE_TASK_PROCESS_ID         = "org.test.kjar.RuleTask";
    public static final String TASK_CONTENT_PROCESS_ID      = "org.test.kjar.UserTask";
    public static final String EVALUTAION_PROCESS_ID        = "org.test.kjar.evaluation";
    public static final String GROUP_ASSSIGNMENT_PROCESS_ID = "org.test.kjar.GroupAssignmentHumanTask";
    public static final String GROUP_ASSSIGN_VAR_PROCESS_ID = "org.test.kjar.groupAssign";
    public static final String HUMAN_TASK_OWN_TYPE_ID       = "org.test.kjar.HumanTaskWithOwnType";
    public static final String CLASSPATH_OBJECT_PROCESS_ID  = "org.test.kjar.classpath.process";
   
    /**
     * User/password
     */
    
    public static final String KRIS_USER = "krisv";
    public static final String KRIS_PASSWORD = "krisv123@";
    public static final String MARY_USER = "mary";
    public static final String MARY_PASSWORD = "mary123@";
    public static final String SALA_USER = "salaboy";
    public static final String SALA_PASSWORD = "sala123@";
    public static final String JOHN_USER = "john";
    public static final String JOHN_PASSWORD = "john123@";
   
    /**
     * SSL
     */
    
    public static final String CLIENT_KEY_TRUSTSTORE_LOCATION = "ssl/client_keystore.jks";
    public static final String CLIENT_KEYSTORE_PASSWORD = "CLIENT_KEYSTORE_PASSWORD";
    
    /**
     * Project version (arquillian jars)
     */
    public final static String PROJECT_VERSION;
    static { 
        Properties testProps = new Properties();
        try {
            testProps.load(TestConstants.class.getResourceAsStream("/test.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize projectVersion property: " + e.getMessage(), e);
        }
        PROJECT_VERSION = testProps.getProperty("project.version");
    }
    
    /**
     * Vfs deployment
     */
    
    public static final String VFS_DEPLOYMENT_ID = "test";

    /**
     * Kjar deployment
     */
    
    public static final String GROUP_ID = "org.test";
    public static final String ARTIFACT_ID = "kjar";
    public static final String CLASSPATH_ARTIFACT_ID = "classpath-kjar";
    public static final String VERSION = "1.0";
    public static final String KBASE_NAME = "defaultKieBase";
    public static final String KSESSION_NAME = "defaultKieSession";
 
    public static final String KJAR_DEPLOYMENT_ID;
    static { 
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        KJAR_DEPLOYMENT_ID = deploymentUnit.getIdentifier();
    }

    
}
