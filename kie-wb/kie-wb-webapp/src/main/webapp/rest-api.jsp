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
<h2>jBPM REST API description</h2>
Entry point for REST interface is at <%= request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+""+request.getContextPath()+"/"%>rest/
<br/>
<h3>Following is list of available resources for runtime engine:</h3>

<table class="t-table">
  <tr class="t-firstrow"><th>Resource</th><th>Comments</th></tr>
  <tr><td>/runtime</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")</td><td>// deploymentId</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/execute</td><td>execute the given command [POST] </td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/process/{id: [a-zA-Z0-9-:\\.]+}/start</td><td>start process [POST] - accepts "query map parameters"</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/process/instance/{id: [0-9]+}</td><td>process instance details [GET]</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/process/instance/{id: [0-9]+}/start</td><td>start process instance [POST]</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/process/instance/{id: [0-9]+}/signal</td><td>signal event [POST] (accepts query map params)</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/process/instance/{id: [0-9]+}/abort</td><td>abort process instance [POST]</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/signal/{id: [a-zA-Z0-9-]+}</td><td> signal event [POST] (accepts query map params)</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/workitem/{id: [0-9]+}/complete</td><td>complete work item [POST] (accepts query map params) - accepts "query map parameters"</td></tr>
  <tr><td>/runtime/{id: [a-zA-Z0-9-:\\.]+}")/workitem/{id: [0-9]+}/abort</td><td>abort work item [POST]</td></tr>
</table>

<h3>Following is list of available resources for task service:</h3>
<table class="t-table">
  <tr class="t-firstrow"><th>Resource</th><th>Comments</th></tr>
  <tr><td>/task</td></tr>
  <tr><td>/task/query</td><td>query tasks TaskSummary returned) [GET] </td></tr>
  <tr><td>/task/execute/{id: \\d+}</td><td> execute the given (task) command [POST] return (jaxb) task [GET]- operations below are case insensitive</td></tr>
  <tr><td>/task/execute{id: \\d+}/activate</td><td>activate task (taskId as query param.. )</td></tr>
  <tr><td>/task/execute{id: \\d+}/claim</td><td>claim task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/claimnextavailable</td><td> claim next available task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/complete</td><td>complete task [POST] - accepts "query map parameters"</td></tr>
  <tr><td>/task/execute{id: \\d+}/delegate</td><td>delegate task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/exit</td><td>exit task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/fail</td><td>fail task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/forward</td><td>forward task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/release</td><td>release task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/resume</td><td>resume task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/skip</td><td>skip task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/start</td><td>start task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/stop</td><td>stop task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/suspend</td><td>suspend task [POST]</td></tr>
  <tr><td>/task/execute{id: \\d+}/nominate</td><td>nominate task [POST]</td></tr>
</table>

</body>
</html>