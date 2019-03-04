<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang3.LocaleUtils" %>
<%@ page import="org.uberfire.ext.security.server.SecureHeadersFilter" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
  Locale locale= null;
  try{
    locale = LocaleUtils.toLocale( request.getParameter( "locale" ) );
  } catch(Exception e){
    locale= request.getLocale();
  }
  SecureHeadersFilter.applyHeaders( request, response );
%>
<i18n:bundle id="bundle" baseName="org.kie.bc.client.resources.i18n.LoginConstants"
             locale='<%= locale%>' />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="login-pf">
<head>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/org.kie.bc.KIEWebapp/css/rcue.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/org.kie.bc.KIEWebapp/css/rcue-additions.min.css">
    <link rel="shortcut icon" href="favicon.ico" />
    <title>Business Central</title>
</head>
<body>
    <span id="badge">
        <img id="logo" src="<%=request.getContextPath()%>/img/login-screen-logo.png" alt="Red Hat Logo">
    </span>

    <div class="container">
        <div class="row">
            <div class="col-sm-12">
                <div id="brand">
                    <img style="height: auto" src="<%=request.getContextPath()%>/img/BC_Logo.png" alt="BUSINESS CENTRAL" title="BUSINESS CENTRAL"/>
                </div>
            </div>
            <div class="col-sm-7 col-md-6 col-lg-5 login">
                <c:if test="${param.message != null}">
                    <div class="alert alert-danger">
                        <span class="pficon pficon-error-circle-o"></span>
                        <strong><i18n:message key="loginFailed">Login failed: Not Authorized</i18n:message></strong>
                    </div>
                </c:if>
                <form class="form-horizontal" role="form" action="j_security_check?locale=<%=locale%>" method="POST">
                    <div class="form-group">
                        <label for="j_username" class="col-sm-2 col-md-2 control-label"><i18n:message key="UserName">Username</i18n:message></label>
                        <div class="col-sm-10 col-md-10">
                            <input type="text" class="form-control" value="" name="j_username" id="j_username" placeholder="" tabindex="1" autofocus />
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="j_password" class="col-sm-2 col-md-2 control-label"><i18n:message key="Password">Password</i18n:message></label>
                        <div class="col-sm-10 col-md-10">
                            <input type="password" class="form-control" id="j_password" name="j_password" placeholder="" tabindex="2">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-xs-offset-8 col-xs-4 col-sm-offset-8 col-sm-4 col-md-offset-8 col-md-4 submit">
                            <button type="submit" class="btn btn-primary btn-lg" tabindex="3"><i18n:message key="SignIn">Sign In</i18n:message></button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
