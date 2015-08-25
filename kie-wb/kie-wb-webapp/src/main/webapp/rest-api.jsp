<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>jBPM Execution server REST API</title>
  <style type="text/css">
    table.t-table {
      border: 1px solid #CCC;
      font-size: 12px;
    }
    .t-table td {
      padding: 4px;
      margin: 3px;
      border: 1px solid #ccc;
    }
    .t-table th {
      background-color: #104E8B;
      color: #FFF;
      font-weight: bold;
    }
  </style>
 </head>
<body>
<h2>REST API description</h2>
Entry point for REST interface is at <%= request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+""+request.getContextPath()+"/"%>rest/
<br/>

<h3>URL parameters used in the REST resource URLs below:</h3>

<table class="t-table" width="100%" id="parameters">
  <tr class="t-firstrow">
    <th>Parameter</th>
    <th>Regular Expression</th>
    <th>Description</th>
  </tr>
  <tr><td><code>contentId</code></td><td><code>[0-9-]+</code></td><td>id of a task content</d></tr>
  <tr><td><code>deploymentId</code></td><td><code>[\w\.-]+(:[\w\.-]+){2,2}(:[\w\.-]*){0,2}</code></td><td>deployment id</td></tr>
  <tr><td><code>logId</code></td><td><code>[a-zA-Z0-9-:\._]+</code></td><td>id used to find a log</td></tr>
  <tr><td><code>oper</code></td><td><code>[a-zA-Z]+</code></td><td>keyword used to specify an operation</td></tr>
  <tr><td><code>procInstId</code></td><td><code>[0-9]+</code></td><td>process instance id</td></tr>
  <tr><td><code>processDefId</code></td><td><code>[a-zA-Z0-9-:\._]+</code></td><td>process (definition) id</td></tr>
  <tr><td><code>taskId</code></td><td><code>[0-9-]+</code></td><td>task id</td></tr>
  <tr><td><code>type</code></td><td><code>[a-zA-Z]+</code></td><td>type of log</td></tr>
  <tr><td><code>value</code></td><td><code>[a-zA-Z0-9-:\._]+</code></td><td>string value (of a variable)</td></tr>
  <tr><td><code>varId</code></td><td><code>[a-zA-Z0-9-:\._]+</code></td><td>variable id</td></tr>
  <tr><td><code>varName</code></td><td><code>[\w\.-]+</code></td><td>variable name (equivalent to the variable id)</td></tr>
  <tr><td><code>workItemId</code></td><td><code>[0-9-]+</code></td><td>work item id</td></tr>
 </table>

<h3>Available resources for managing repositories, projects and organizational units:</h3>

<table class="t-table" width="100%">
  <tr class="t-firstrow">
    <th>Resource</th>
    <th>HTTP Op</th>
    <th>Description</th>
  </tr>
  <tr><td>/jobs/{jobId}</td><td>GET</td><td>get the status of a job</td></tr>
  <tr><td>/jobs/{jobId}</td><td>DELETE</td><td>attempt to remove a job before it has been executed</td></tr>
  <tr><td>/organizationalunits</td><td>GET</td><td>get details for all organizational units</td></tr>
  <tr><td>/organizationalunits</td><td>POST</td><td>create an organizational unit</td></tr>
  <tr><td>/organizationalunits/{organizationalUnitName}</td><td>GET</td><td>get the details for an organizational unit</td></tr>
  <tr><td>/organizationalunits/{organizationalUnitName}</td><td>DELETE</td><td>delete an organizational unit</td></tr>
  <tr><td>/organizationalunits/{organizationalUnitName}/</td><td>POST</td><td>update an existing organizational unit</td></tr>
  <tr><td>/organizationalunits/{organizationalUnitName}/repositories/{repositoryName}</td><td>POST</td><td>add a repository to an organizational unit</td></tr>
  <tr><td>/organizationalunits/{organizationalUnitName}/repositories/{repositoryName}</td><td>DELETE</td><td>remove a repository from an organizational unit</td></tr>
  <tr><td>/repositories</td><td>GET</td><td>get details for  repositories</td></tr>
  <tr><td>/repositories</td><td>POST</td><td>create a repository</td></tr>
  <tr><td>/repositories/{repositoryName}</td><td>DELETE</td><td>delete a repository</td></tr>
  <tr><td>/repositories/{repositoryName}</td><td>GET</td><td>get repository details</td></tr>
  <tr><td>/repositories/{repositoryName}/projects</td><td>GET</td><td>get details for all projects in a repository</td></tr>
  <tr><td>/repositories/{repositoryName}/projects</td><td>POST</td><td>create a project in a repository</td></tr>
  <tr><td>/repositories/{repositoryName}/projects/{projectName}</td><td>DELETE</td><td>remove a project</td></tr>
  <tr><td>/repositories/{repositoryName}/projects/{projectName}/maven/compile</td><td>POST</td><td>build a project</td></tr>
  <tr><td>/repositories/{repositoryName}/projects/{projectName}/maven/test</td><td>POST</td><td>run all tests in a project</td></tr>
  <tr><td>/repositories/{repositoryName}/projects/{projectName}/maven/install</td><td>POST</td><td>build and deploy a project</td></tr>
  <tr><td>/repositories/{repositoryName}/projects/{projectName}/maven/deploy</td><td>POST</td><td>build and deploy a project [<em>deprecated</em>]</td></tr>
</table>

<h3>Available resources for managing deployments:</h3>

<table class="t-table" width="100%">
  <tr class="t-firstrow">
    <th>Resource</th>
    <th>HTTP Op</th>
    <th>Description</th>
  </tr>
  <tr><td>/deployment</td><td>GET</td><td>get details for all deployments</td></tr>
  <tr><td>/deployment/processes</td><td>GET</td><td>get details for all process definitions (across all deployments)</td></tr>
  <tr><td>/deployment/{deploymentId}</td><td>GET</td><td>get deployment details</td></tr>
  <tr><td>/deployment/{deploymentId}/processes</td><td>GET</td><td>get details for all process definitions in a deployment</td></tr>
  <tr><td>/deployment/{deploymentId}/deploy</td><td>POST</td><td>deploy a deployment</td></tr>
  <tr><td>/deployment/{deploymentId}/activate</td><td>POST</td><td>activate a (deployed) deployment</td></tr>
  <tr><td>/deployment/{deploymentId}/deactivate</td><td>POST</td><td>deactivate a (deployed) deployment</td></tr>
  <tr><td>/deployment/{deploymentId}/undeploy</td><td>POST</td><td>undeploy a deployment</td></tr>
</table>

<h3>Available resources for interacting with the runtime engine:</h3>

<table class="t-table" width="100%">
  <tr class="t-firstrow">
    <th>Resource</th>
    <th>HTTP Op</th>
    <th>Description</th>
  </tr>
  <tr><td>/runtime/{deploymentId}/process/instance/{procInstId}</td><td>GET</td><td>get process instance details</td></tr>
  <tr><td>/runtime/{deploymentId}/process/instance/{procInstId}/abort</td><td>POST</td><td>abort process instance</td></tr>
  <tr><td>/runtime/{deploymentId}/process/instance/{procInstId}/signal</td><td>POST</td><td>signal process instance</td></tr>
  <tr><td>/runtime/{deploymentId}/process/instance/{procInstId}/variable/{varName}</td><td>GET</td><td>get a process instance variable</td></tr>
  <tr><td>/runtime/{deploymentId}/process/{processDefId}/</td><td>GET</td><td>get process definition details</td></tr>
  <tr><td>/runtime/{deploymentId}/process/{processDefId}/start</td><td>POST</td><td>start an instance of a process</td></tr>
  <tr><td>/runtime/{deploymentId}/process/{processDefId}/startform</td><td>GET</td><td>get the start form for starting a instance of a process</td></tr>
  <tr><td>/runtime/{deploymentId}/signal</td><td>POST</td><td>signal the deployment</td></tr>

  <tr><td>/runtime/{deploymentId}/withvars/process/instance/{procInstId}</td><td>GET</td><td>get process instance details along with all process instance variables</td></tr>
  <tr><td>/runtime/{deploymentId}/withvars/process/instance/{procInstId}/signal</td><td>POST</td><td>signal the process instance and then retrieve the process instance details along with all process variables</td></tr>
  <tr><td>/runtime/{deploymentId}/withvars/process/{processDefId}/start</td><td>POST</td><td>start an instance of a process and then retrieve the process instance details along with all process variables</td></tr>

  <tr><td>/runtime/{deploymentId}/workitem/{workItemId}</td><td>GET</td><td>get a workitem</td></tr>
  <tr><td>/runtime/{deploymentId}/workitem/{workItemId}/{oper}</td><td>POST</td><td>modify a work item, where
  <code>oper</code> is one of the following:</p>
    <table border="0">
      <tr><td><code>complete</code></td><td>complete the work item</td></tr>
      <tr><td><code>abort</code></td><td>abort the work item</td></tr>
    </table>
  </td></tr>

</table>

<h3>Available resources for doing queries:</h3>

<table class="t-table" width="100%">
  <tr class="t-firstrow">
    <th>Resource</th>
    <th>HTTP Op</th>
    <th>Description</th>
  </tr>
  <tr><td>/query/runtime/process</td><td>GET</td><td>query for details on process instances and process variables</td></tr>
  <tr><td>/query/runtime/task</td><td>GET</td><td>query for details on task and process variables</td></tr>
  <tr><td>/query/task</td><td>GET</td><td>query task summaries</td></tr>
  <tr><td>/task/query</td><td>GET</td><td>query task summaries [<em>deprecated</em>]</td></tr>
</table>

<h3>Available resources for managing tasks:</h3>

<table class="t-table" width="100%">
  <tr class="t-firstrow">
    <th>Resource</th>
    <th>HTTP Op</th>
    <th>Description</th>
  </tr>
  <tr><td>/task/{taskId}</td><td>GET</td><td>get a task</td></tr>
  <tr><td>/task/{taskId}/{oper}</td><td>POST</td><td>modify a task accordingly, where
  <code>oper</code> is one of the following:</p>
    <table>
      <tr><td><code>activate</code></td><td>activate the task</td></tr>
      <tr><td><code>claim</code></td><td>claim the task</td></tr>
      <tr><td><code>claimnextavailable</code></td><td>claim the next task available to the user</td></tr>
      <tr><td><code>complete</code></td><td>complete the task</td></tr>
      <tr><td><code>delegate</code></td><td>delegate the task</td></tr>
      <tr><td><code>exit</code></td><td>exit the task</td></tr>
      <tr><td><code>fail</code></td><td>fail the task</td></tr>
      <tr><td><code>forward</code></td><td>forward the task</td></tr>
      <tr><td><code>nominate</code></td><td>nominate the task</td></tr>
      <tr><td><code>release</code></td><td>release the task</td></tr>
      <tr><td><code>resume</code></td><td>resume the task</td></tr>
      <tr><td><code>skip</code></td><td>skip the task</td></tr>
      <tr><td><code>start</code></td><td>start the task</td></tr>
      <tr><td><code>stop</code></td><td>stop the task</td></tr>
      <tr><td><code>suspend</code></td><td>suspend the task</td></tr>
    </table>
  </td></tr>
  <tr><td>/task/{taskId}/content</td><td>GET</td><td>get the task content by task id</td></tr>
  <tr><td>/task/{taskId}/showTaskForm</td><td>GET</td><td>get the task form URL for a task</td></tr>

  <tr><td>/task/content/{contentId}</td><td>GET</td><td>get the task content by the content id</td></tr>
  <tr><td>/task/history/bam/clear</td><td>POST</td><td>delete all task summary history instances</td></tr>
</table>

<h3>Available resources for retrieving history (BAM) information:</h3>

<table class="t-table" width="100%">
  <tr class="t-firstrow">
    <th>Resource</th>
    <th>HTTP Op</th>
    <th>Description</th>
  </tr>
  <tr><td>/history/clear</td><td>POST</td><td>delete all history (across <em>all</em> deployments)</td></tr>
  <tr><td>/history/instances</td><td>GET</td><td>get all process instance logs across all deployments</td></tr>
  <tr><td>/history/instance/{procInstId}</td><td>GET</td>get a process instance log<td>
  <tr><td>/history/instance/{procInstId}/{type}</td><td>GET</td><td>get a log, where
  <code>type</code> is one of the following:</p>
    <table>
      <tr><td><code>child</code></td><td>get the process instance logs for the child or subprocess instances of a process instance</td></tr>
      <tr><td><code>node</code></td><td>get the node instance logs for a process instance</td></tr>
      <tr><td><code>variable</code></td><td>get the variable instance logs for a process
      instance</td></tr>
    </table>
  </td></tr>
  <tr><td>/history/instance/{procInstId}/{type}/{logId}</td><td>GET</td><td>get a log
  where <code>type</code> and <code>logId</code> are one of the following:</p>
    <table>
      <tr><td><code>node</code></td><td>get the node instance logs for a process instance
      <ul>
        <li><code>logId</code> is the node id</li>
      </ul></td></tr>
      <tr><td><code>variable</code></td><td>get the variable instance logs for a process instance
      <ul>
        <li><code>logId</code> is the variable id</li>
      </ul></td></tr>
    </table>
  </td></tr>
  <tr><td>/history/process/{processDefId}</td><td>GET</td><td>get all process instance logs for process instances of the given proces</td></tr>
  <tr><td>/history/variable/{varId}</td><td>GET</td><td>get variable instance logs by variable id</td></tr>
  <tr><td>/history/variable/{varId}/instances</td><td>GET</td><td>get process instance logs that
  involve the variable (id) specified</td></tr>
  <tr><td>/history/variable/{varId}/value/{value}</td><td>GET</td><td>get variable instance logs by
  variable id and string value</td></tr>
  <tr><td>/history/variable/{varId}/value/{value}/instances</td><td>GET</td><td>get process instance
  involve the variable (id) and variable (string) value specified</td></tr>
</table>

<h3>Available resources that takes command requests:</h3>

<table class="t-table" width="100%">
  <tr class="t-firstrow">
    <th>Resource</th>
    <th>HTTP Op</th>
    <th>Description</th>
  </tr>
  <tr><td>/execute</td><td>POST</td><td>execute the commands passed on the given deployment</td></tr>
</table>

</body>
</html>
