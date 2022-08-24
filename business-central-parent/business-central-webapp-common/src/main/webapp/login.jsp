<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang3.LocaleUtils" %>
<%@ page import="org.uberfire.ext.security.server.SecureHeadersFilter" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<%
  Locale locale = null;
  try {
    locale = LocaleUtils.toLocale(request.getParameter("locale"));
  } catch (Exception e) {
    locale = request.getLocale();
  }
  SecureHeadersFilter.applyHeaders(request,
                                   response);
%>
<i18n:bundle id="bundle" baseName="${login.bundle.name}"
             locale='<%= locale%>' />
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
  <img id="logo" src="images/business-central.png" alt="Business Central Logo" title="Powered By Drools/jBPM"/>

  <div id="login-wrapper" class="png_bg">
    <div id="login-top">
    </div>

    <div id="login-content" class="png_bg">      
      <form action="j_security_check?locale=<%=locale%>" method="POST">
        <fieldset>
          <c:if test="${param.message != null}">
            <h3><i18n:message key="loginFailed"></i18n:message></h3><br/>
          </c:if>
          <label style="white-space: nowrap;"><i18n:message key="UserName"></i18n:message></label><input value="" name="j_username" class="text-input" type="text" autofocus/>
          <br style="clear: both;"/>
          <label style="white-space: nowrap;"><i18n:message key="Password"></i18n:message></label><input name="j_password" class="text-input" type="password"/>
          <br style="clear: both;"/>
          <% if (request.getParameter("gwt.codesvr") != null) { %>
          <input type="hidden" name="gwt.codesvr" value="<%= org.owasp.encoder.Encode.forHtmlAttribute(request.getParameter("gwt.codesvr")) %>"/>
          <% } %>
          <input class="button login" type="submit" value='<i18n:message key="SignIn"></i18n:message>'/>
        </fieldset>
      </form>
    </div>
  </div>
</div>
</body>
</html>
