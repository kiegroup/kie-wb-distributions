/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.backend;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.console.ng.es.client.editors.requestlist.dataset.DataSetRequestListViewImpl;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridViewImpl;
import org.jbpm.console.ng.pr.client.editors.instance.list.dash.DataSetProcessInstanceListViewImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class DashbuilderBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(DashbuilderBootstrap.class);

    private String jbpmDatasource = System.getProperty("org.kie.ds.jndi", "java:jboss/datasources/ExampleDS");
    public static final String HUMAN_TASKS_DATASET = "jbpmHumanTasks";
    public static final String HUMAN_TASKS_TABLE = "AuditTaskImpl";

    public static final String PROCESS_INSTANCE_DATASET = "jbpmProcessInstances";
    public static final String PROCESS_INSTANCE_TABLE = "ProcessInstanceLog";

    public static final String HUMAN_TASKS_WITH_USER_DATASET = "jbpmHumanTasksWithUser";
    public static final String HUMAN_TASKS_WITH_ADMIN_DATASET = "jbpmHumanTasksWithAdmin";

    public static final String REQUEST_LIST_DATASET = "jbpmRequestList";
    public static final String REQUEST_LIST_TABLE = "RequestInfo";

    public static final String  PROCESS_INSTANCE_WITH_VARIABLES_DATASET = "jbpmProcessInstancesWithVariables";

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void init() {
        // figure out data source JNDI name
        findDataSourceJNDI();
        registerDataSetDefinitions();
    }

    protected void registerDataSetDefinitions() {

        DataSetDef humanTasksDef = DataSetFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_DATASET)
                .name("Human tasks")
                .dataSource(jbpmDatasource)
                .dbTable(HUMAN_TASKS_TABLE, false)
                .date(DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME)
                .label(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER)
                .label(DataSetTasksListGridViewImpl.COLUMN_CREATEDBY)
                .date(DataSetTasksListGridViewImpl.COLUMN_CREATEDON)
                .label(DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID)
                .text(DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION)
                .date(DataSetTasksListGridViewImpl.COLUMN_DUEDATE)
                .label(DataSetTasksListGridViewImpl.COLUMN_NAME)
                .number(DataSetTasksListGridViewImpl.COLUMN_PARENTID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PRIORITY)
                .label(DataSetTasksListGridViewImpl.COLUMN_PROCESSID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID)
                .label(DataSetTasksListGridViewImpl.COLUMN_STATUS)
                .number(DataSetTasksListGridViewImpl.COLUMN_TASKID)
                .number(DataSetTasksListGridViewImpl.COLUMN_WORKITEMID)
                .buildDef();

        DataSetDef processInstancesDef = DataSetFactory.newSQLDataSetDef()
                .uuid(PROCESS_INSTANCE_DATASET)
                .name("Process Instances")
                .dataSource(jbpmDatasource)
                .dbTable(PROCESS_INSTANCE_TABLE, false)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEID)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSID)
                .date(DataSetProcessInstanceListViewImpl.COLUMN_START)
                .date(DataSetProcessInstanceListViewImpl.COLUMN_END)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_STATUS)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_PARENTPROCESSINSTANCEID)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_OUTCOME)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_DURATION)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_IDENTITY)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSVERSION)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSNAME)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_CORRELATIONKEY)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_EXTERNALID)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEDESCRIPTION)
                .buildDef();

        DataSetDef humanTasksWithUserDef = DataSetFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_USER_DATASET)
                .name("Human tasks and users")
                .dataSource(jbpmDatasource)
                .dbSQL( "select  t.activationTime, t.actualOwner, t.createdBy, "
                        + "t.createdOn, t.deploymentId, t.description, t.dueDate, "
                        + "t.name, t.parentId, t.priority, t.processId, t.processInstanceId, "
                        + "t.processSessionId, t.status, t.taskId, t.workItemId, oe.id oeid "
                        + "from AuditTaskImpl t, "
                        + "PeopleAssignments_PotOwners po, "
                        + "OrganizationalEntity oe "
                        + "where t.id = po.task_id and po.entity_id = oe.id", false )
                .date(DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME)
                .label(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER)
                .label(DataSetTasksListGridViewImpl.COLUMN_CREATEDBY)
                .date(DataSetTasksListGridViewImpl.COLUMN_CREATEDON)
                .label(DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID)
                .text(DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION)
                .date(DataSetTasksListGridViewImpl.COLUMN_DUEDATE)
                .label(DataSetTasksListGridViewImpl.COLUMN_NAME)
                .number(DataSetTasksListGridViewImpl.COLUMN_PARENTID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PRIORITY)
                .label(DataSetTasksListGridViewImpl.COLUMN_PROCESSID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID)
                .label(DataSetTasksListGridViewImpl.COLUMN_STATUS)
                .label(DataSetTasksListGridViewImpl.COLUMN_TASKID)   //declaring as label(even though it's numeric) because needs apply groupby and  Group by number not supported
                .number(DataSetTasksListGridViewImpl.COLUMN_WORKITEMID)
                .label(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY)
                .buildDef();

        DataSetDef humanTaskWithAdminDef = DataSetFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_ADMIN_DATASET)
                .name("Human tasks and admins")
                .dataSource(jbpmDatasource)
                .dbSQL("select t.activationTime, t.actualOwner, t.createdBy, "
                        + "t.createdOn, t.deploymentId, t.description, t.dueDate, "
                        + "t.name, t.parentId, t.priority, t.processId, t.processInstanceId, "
                        + "t.processSessionId, t.status, t.taskId, t.workItemId, oe.id oeid "
                        + "from AuditTaskImpl t, "
                        + "PeopleAssignments_BAs bas, "
                        + "OrganizationalEntity oe "
                        + "where t.id = bas.task_id and bas.entity_id = oe.id", false)
                .date(DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME)
                .label(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER)
                .label(DataSetTasksListGridViewImpl.COLUMN_CREATEDBY)
                .date(DataSetTasksListGridViewImpl.COLUMN_CREATEDON)
                .label(DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID)
                .text(DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION)
                .date(DataSetTasksListGridViewImpl.COLUMN_DUEDATE)
                .label(DataSetTasksListGridViewImpl.COLUMN_NAME)
                .number(DataSetTasksListGridViewImpl.COLUMN_PARENTID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PRIORITY)
                .label(DataSetTasksListGridViewImpl.COLUMN_PROCESSID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID)
                .label(DataSetTasksListGridViewImpl.COLUMN_STATUS)
                .label(DataSetTasksListGridViewImpl.COLUMN_TASKID)     //declaring as label(even though it's numeric) because needs apply groupby and  Group by number not supported
                .number(DataSetTasksListGridViewImpl.COLUMN_WORKITEMID)
                .label(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY)
                .buildDef();

        DataSetDef requestListDef = DataSetFactory.newSQLDataSetDef()
                .uuid(REQUEST_LIST_DATASET)
                .name("Request List")
                .dataSource(jbpmDatasource)
                .dbTable(REQUEST_LIST_TABLE, false)
                .number(DataSetRequestListViewImpl.COLUMN_ID)
                .date(DataSetRequestListViewImpl.COLUMN_TIMESTAMP)
                .label(DataSetRequestListViewImpl.COLUMN_STATUS)
                .label(DataSetRequestListViewImpl.COLUMN_COMMANDNAME)
                .label(DataSetRequestListViewImpl.COLUMN_MESSAGE)
                .label(DataSetRequestListViewImpl.COLUMN_BUSINESSKEY)
                .buildDef();

        DataSetDef processWithVariablesDef = DataSetFactory.newSQLDataSetDef()
                .uuid(PROCESS_INSTANCE_WITH_VARIABLES_DATASET)
                .name("Domain Specific Process Instances")
                .dataSource(jbpmDatasource)
                .dbSQL("select pil.processInstanceId pid,\n" +
                        "       pil.processId pname,\n" +
                        "       v.id varid,\n" +
                        "       v.variableId varname,\n" +
                        "       v.value varvalue\n" +
                        "from ProcessInstancelog pil\n" +
                        "  inner join (select vil.processInstanceId ,vil.variableId, MAX(vil.ID) maxvilid  FROM VariableInstanceLog vil\n" +
                        "  GROUP BY vil.processInstanceId, vil.variableId)  x\n" +
                        "    on (x.processInstanceId =pil.processInstanceId)\n" +
                        "  INNER JOIN VariableInstanceLog v\n" +
                        "    ON (v.variableId = x.variableId  AND v.id = x.maxvilid )", false )
                .number("pid")
                .label("pname" )
                .number("varid")
                .label("varname")
                .label("varvalue")
                .buildDef();

        // Hide all these internal data set from end user view
        humanTasksDef.setPublic(false);
        processInstancesDef.setPublic(false);
        humanTasksWithUserDef.setPublic(false);
        humanTaskWithAdminDef.setPublic(false);
        requestListDef.setPublic(false);
        processWithVariablesDef.setPublic(false);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(humanTasksDef);
        dataSetDefRegistry.registerDataSetDef(processInstancesDef);
        dataSetDefRegistry.registerDataSetDef(humanTasksWithUserDef);
        dataSetDefRegistry.registerDataSetDef(humanTaskWithAdminDef);
        dataSetDefRegistry.registerDataSetDef(requestListDef);
        dataSetDefRegistry.registerDataSetDef(processWithVariablesDef);
    }

    protected void findDataSourceJNDI() {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/persistence.xml"));

            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        if ("jta-data-source".equals(reader.getLocalName())) {

                            jbpmDatasource = reader.getElementText();
                            return;
                        }
                        break;
                }
            }
        } catch (XMLStreamException e) {
            logger.warn("Unable to find out JNDI name fo data source to be used for data sets due to {} using default {}", e.getMessage(), jbpmDatasource, e);
        }
    }
}