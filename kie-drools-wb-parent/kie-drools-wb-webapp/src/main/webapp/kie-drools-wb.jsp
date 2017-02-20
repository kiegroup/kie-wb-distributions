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
<i18n:bundle id="bundle" baseName="org.kie.workbench.drools.client.resources.i18n.LoginConstants"
             locale='<%= locale%>' />
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>KIE Drools Workbench</title>

    <script src="org.kie.workbench.drools.KIEDroolsWebapp/zeroclipboard/ZeroClipboard.min.js"></script>
    <script src="org.kie.workbench.drools.KIEDroolsWebapp/zeroclipboard/ZeroClipboardLoader.js"></script>
    <link rel="shortcut icon" href="favicon.ico" />

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
  <script type="text/javascript" src='org.kie.workbench.drools.KIEDroolsWebapp/org.kie.workbench.drools.KIEDroolsWebapp.nocache.js'></script>
  <!-- ACE - main .js file -->
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/ace.js" type="text/javascript" charset="utf-8"></script>
  <!-- Get .js files for any needed ACE modes and themes -->
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/theme-chrome.js" type="text/javascript" charset="utf-8"></script>
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/mode-html.js" type="text/javascript" charset="utf-8"></script>
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/mode-css.js" type="text/javascript" charset="utf-8"></script>
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/mode-javascript.js" type="text/javascript" charset="utf-8"></script>
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/mode-drools.js" type="text/javascript" charset="utf-8"></script>
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/mode-text.js" type="text/javascript" charset="utf-8"></script>
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/mode-xml.js" type="text/javascript" charset="utf-8"></script>

  <!--  Needed for autocompletion support. -->
  <script src="org.kie.workbench.drools.KIEDroolsWebapp/ace/ext-language_tools.js" type="text/javascript" charset="utf-8"></script>

</body>
</html>
