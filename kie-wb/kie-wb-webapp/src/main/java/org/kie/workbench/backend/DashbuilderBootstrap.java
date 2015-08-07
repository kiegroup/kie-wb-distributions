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

import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.console.ng.es.client.editors.requestlist.RequestListViewImpl;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridViewImpl;
import org.jbpm.console.ng.pr.client.editors.instance.list.dash.DataSetProcessInstanceListViewImpl;
import org.uberfire.commons.services.cdi.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

@Startup
@ApplicationScoped
public class DashbuilderBootstrap {
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

    @Inject
    protected EntityManagerFactory emf;

    @PostConstruct
    protected void init() {
        // figure out data source JNDI name
        Object ds = emf.getProperties().get("hibernate.connection.datasource");
        if (ds != null && ds instanceof String) {
            jbpmDatasource = (String) ds;
        } else if (ds != null && ds instanceof javax.naming.Referenceable) {
            try {
                jbpmDatasource = ((javax.naming.Referenceable) ds).getReference().getClassName();
            } catch (Exception e) {

            }
        }
        registerDataSetDefinitions();
    }

    protected void registerDataSetDefinitions() {


        dataSetDefRegistry.registerDataSetDef(
                DataSetFactory.newSQLDataSetDef()
                        .uuid( HUMAN_TASKS_DATASET )
                        .name( "Human tasks" )
                        .dataSource(jbpmDatasource)
                        .dbTable( HUMAN_TASKS_TABLE, false )
                        .date( DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME )
                        .label( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER )
                        .label( DataSetTasksListGridViewImpl.COLUMN_CREATEDBY )
                        .date( DataSetTasksListGridViewImpl.COLUMN_CREATEDON )
                        .label( DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID )
                        .text( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION )
                        .date( DataSetTasksListGridViewImpl.COLUMN_DUEDATE )
                        .label( DataSetTasksListGridViewImpl.COLUMN_NAME )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PARENTID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PRIORITY )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_STATUS )
                        .label( DataSetTasksListGridViewImpl.COLUMN_TASKID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_WORKITEMID )

                        .buildDef() );

        dataSetDefRegistry.registerDataSetDef(
                DataSetFactory.newSQLDataSetDef()
                        .uuid( PROCESS_INSTANCE_DATASET)
                        .name( "Process Instances" )
                        .dataSource(jbpmDatasource)
                        .dbTable( PROCESS_INSTANCE_TABLE, false )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEID )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSID )
                        .date( DataSetProcessInstanceListViewImpl.COLUMN_START )
                        .date( DataSetProcessInstanceListViewImpl.COLUMN_END )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_STATUS )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_PARENTPROCESSINSTANCEID )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_OUTCOME )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_DURATION )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_IDENTITY )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSVERSION )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSNAME )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_CORRELATIONKEY )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_EXTERNALID )
                        .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEDESCRIPTION )
                        .buildDef() );

        dataSetDefRegistry.registerDataSetDef(
                DataSetFactory.newSQLDataSetDef()
                        .uuid( HUMAN_TASKS_WITH_USER_DATASET )
                        .name( "Human tasks and users" )
                        .dataSource(jbpmDatasource)
                        .dbSQL("select  t.activationtime, t.actualowner, t.createdby, "
                                + "t.createdon, t.deploymentid, t.description, t.duedate, "
                                + "t.name, t.parentid, t.priority, t.processid, t.processinstanceid, "
                                + "t.processsessionid, t.status, t.taskid, t.workitemid, oe.id oeid "
                                + "from AuditTaskImpl t, "
                                + "peopleassignments_potowners po, "
                                + "organizationalentity oe "
                                + "where t.id = po.task_id and po.entity_id = oe.id", false)
                        .date( DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME )
                        .label( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER )
                        .label( DataSetTasksListGridViewImpl.COLUMN_CREATEDBY )
                        .date( DataSetTasksListGridViewImpl.COLUMN_CREATEDON )
                        .label( DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID )
                        .text( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION )
                        .date( DataSetTasksListGridViewImpl.COLUMN_DUEDATE )
                        .label( DataSetTasksListGridViewImpl.COLUMN_NAME )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PARENTID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PRIORITY )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_STATUS )
                        .label( DataSetTasksListGridViewImpl.COLUMN_TASKID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_WORKITEMID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY )
                        .buildDef() );

        dataSetDefRegistry.registerDataSetDef(
                DataSetFactory.newSQLDataSetDef()
                        .uuid( HUMAN_TASKS_WITH_ADMIN_DATASET )
                        .name( "Human tasks and admins" )
                        .dataSource(jbpmDatasource)
                        .dbSQL("select t.activationtime, t.actualowner, t.createdby, "
                                + "t.createdon, t.deploymentid, t.description, t.duedate, "
                                + "t.name, t.parentid, t.priority, t.processid, t.processinstanceid, "
                                + "t.processsessionid, t.status, t.taskid, t.workitemid, oe.id oeid "
                                + "from AuditTaskImpl t, "
                                + "peopleassignments_bas bas, "
                                + "organizationalentity oe "
                                + "where t.id = bas.task_id and bas.entity_id = oe.id", false)
                         .date( DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME )
                        .label( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER )
                        .label( DataSetTasksListGridViewImpl.COLUMN_CREATEDBY )
                        .date( DataSetTasksListGridViewImpl.COLUMN_CREATEDON )
                        .label( DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID )
                        .text( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION )
                        .date( DataSetTasksListGridViewImpl.COLUMN_DUEDATE )
                        .label( DataSetTasksListGridViewImpl.COLUMN_NAME )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PARENTID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PRIORITY )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_STATUS )
                        .label( DataSetTasksListGridViewImpl.COLUMN_TASKID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_WORKITEMID )
                        .label( DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY )
                        .buildDef() );

        dataSetDefRegistry.registerDataSetDef(
                DataSetFactory.newSQLDataSetDef()
                        .uuid( REQUEST_LIST_DATASET )
                        .name( "Request List" )
                        .dataSource(jbpmDatasource)
                        .dbTable( REQUEST_LIST_TABLE, false )
                        .label( RequestListViewImpl.COLUMN_ID )
                        .date( RequestListViewImpl.COLUMN_TIMESTAMP )
                        .label( RequestListViewImpl.COLUMN_STATUS )
                        .label( RequestListViewImpl.COLUMN_COMMANDNAME )
                        .label( RequestListViewImpl.COLUMN_MESSAGE )
                        .label( RequestListViewImpl.COLUMN_BUSINESSKEY )
                        .buildDef() );
         dataSetDefRegistry.registerDataSetDef(
                        DataSetFactory.newSQLDataSetDef()
                                .uuid( PROCESS_INSTANCE_WITH_VARIABLES_DATASET)
                                .name( "Variable for Evalution Process Instances" )
                                .dataSource(jbpmDatasource)
                                .dbSQL("select pil.processInstanceId pid, pil.processId pname, v.id varid, v.variableId varname, v.value varvalue from ProcessInstanceLog pil, "
                                        + "( select vil.variableId, max(vil.id) as maxvilid from VariableInstanceLog vil  group by vil.processInstanceId, vil.variableId) "
                                        + "as x inner join VariableInstanceLog as v on "
                                        + "v.variableId = x.variableId and v.processInstanceId = pil.processInstanceId and "
                                        + "v.id = x.maxvilid", false )
                                .label( "pid" )
                                .label( "pname" )
                                .label( "varid" )
                                .label( "varname" )
                                .label( "varvalue" )
                                .buildDef() );
    }
}
