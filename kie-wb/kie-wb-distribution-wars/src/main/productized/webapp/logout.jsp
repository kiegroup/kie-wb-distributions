<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
  request.logout();
  javax.servlet.http.HttpSession httpSession = request.getSession(false);
  if (httpSession != null) {
    httpSession.invalidate();
  }
  Locale locale = null;
  try {
    locale = new Locale( request.getParameter( "locale" ) );
  } catch ( Exception e ) {
    locale = request.getLocale();
  }
%>
<i18n:bundle id="bundle" baseName="org.kie.workbench.client.resources.i18n.LoginConstants"
             locale='<%= locale%>'/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <link rel="stylesheet" href="styles/base.css">
  <link rel="stylesheet" href="styles/forms.css">
  <link rel="stylesheet" href="styles/login-screen.css">
  <link rel="shortcut icon" href="favicon.ico" />
  <title>Red Hat JBoss BPM Suite :: Business central</title>
</head>
<body id="login">
<div id="rcue-login-screen">
  <img id="logo" src="images/login-screen-logo.png" alt="Red Hat Logo">

  <div id="login-wrapper" class="png_bg">

    <div id="login-top">
      <%--<img src="images/kie-ide.png" alt="KIE IDE Logo" title="Powered By Drools/jBPM"/>--%>
    </div>

    <div id="login-content" class="png_bg">
      <form action="<%= request.getContextPath() %>" method="POST">
        <fieldset>
          <legend><img src="images/RH_JBoss_BPMS_Logo.png" alt="RED HAT JBOSS BPM SUITE" title="RED HAT JBOSS BPM SUITE"/></legend>

          <h3 id="logout"><i18n:message key="logoutSuccssful">Logout successful</i18n:message></h3>
          <p>
            <input class="button login" type="submit" value='<i18n:message key="loginAgain">Login again</i18n:message>'/>
          </p>
        </fieldset>

      </form>
    </div>
  </div>
</div>
</body>
</html>
