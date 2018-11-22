<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang3.LocaleUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
  final Logger logger = LoggerFactory.getLogger("logout.jsp");
  try {
    request.logout();
    javax.servlet.http.HttpSession httpSession = request.getSession(false);
    if (httpSession != null) {
      httpSession.invalidate();
    }
  } catch (SecurityException e) {
    //The only case we know that this  happens is when java security manager is enabled on EAP.
    logger.debug("Security exception happened, without consequences, during logout.",
                 e);
  }
  Locale locale = null;
  try {
    locale = LocaleUtils.toLocale(request.getParameter("locale"));
  } catch (Exception e) {
    locale = request.getLocale();
  }
%>
<i18n:bundle id="bundle" baseName="${login.bundle.name}"
             locale='<%= locale%>'/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <link rel="stylesheet" href="styles/base.css">
  <link rel="stylesheet" href="styles/forms.css">
  <link rel="stylesheet" href="styles/login-screen.css">
  <link rel="shortcut icon" href="favicon.ico" />
  <title><i18n:message key="LoginTitle"></i18n:message></title>
</head>

<body id="login">
<div id="pfly-login-screen">
  <img id="logo" src="images/business-central.png" alt="KIE IDE Logo" title="Powered By Drools/jBPM"/>
  
  <div id="login-wrapper" class="png_bg">
    <div id="login-top">
    </div>

    <div id="login-content" class="png_bg">
      <form action="<%= request.getContextPath() %>/${login.page}?locale=<%=locale%>" method="POST">
        <fieldset>
          <h3 id="logout"><i18n:message key="logoutSuccssful"></i18n:message></h3>
          <% if (request.getParameter("gwt.codesvr") != null) { %>
          <input type="hidden" name="gwt.codesvr" value="<%= org.owasp.encoder.Encode.forHtmlAttribute(request.getParameter("gwt.codesvr")) %>"/>
          <% } %>
          <p>
              <input class="button" type="submit" value='<i18n:message key="loginAgain"></i18n:message>'/>
          </p>
        </fieldset>
      </form>
    </div>
  </div>
</div>
</body>
</html>
