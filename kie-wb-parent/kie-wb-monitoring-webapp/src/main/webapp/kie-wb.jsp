<%--
  ~ Copyright 2017 Red Hat, Inc. and/or its affiliates.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~       http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang3.LocaleUtils" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
  Locale locale= null;
  try{
    locale = LocaleUtils.toLocale( request.getParameter( "locale" ) );
  } catch(Exception e){
    locale= request.getLocale();
  }
%>
<i18n:bundle id="bundle" baseName="org.kie.workbench.client.resources.i18n.LoginConstants"
             locale='<%= locale%>' />
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <title>KIE Workbench</title>

     <!--[if lt IE 9]>
     <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
     <![endif]-->

    <script src="org.kie.workbench.KIEWebapp/zeroclipboard/ZeroClipboard.min.js"></script>
    <script src="org.kie.workbench.KIEWebapp/zeroclipboard/ZeroClipboardLoader.js"></script>

    <link rel="shortcut icon" href="images/drools.gif" type="image/gif"/>
    <link rel="icon" href="images/drools.gif" type="image/gif"/>

</head>
<body>
  <iframe id="__gwt_historyFrame" style="width: 0; height: 0; border: 0"></iframe>

  <!--add loading indicator while the app is being loaded-->
  <div id="loading" class="container-fluid">
      <div class="row">
          <div class="col-lg-12">
              <div class="center-block text-center">
                  <div class="spinner spinner-lg"></div>
              </div>
          </div>
          <div class="col-lg-12">
              <div class="center-block text-center">
                  <h3><i18n:message key="loadingPleaseWait">Please wait</i18n:message></h3>
              </div>
          </div>
          <div class="col-lg-12">
              <div class="center-block text-center">
                  <span><i18n:message key="loadingApplication">Loading application...</i18n:message></span>
              </div>
          </div>
      </div>
  </div>

  <!-- The GWT js file generated at run time -->
  <script type="text/javascript" src='org.kie.workbench.KIEWebapp/org.kie.workbench.KIEWebapp.nocache.js'></script>

</body>
</html>
