<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/base.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/forms.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/login-screen.css">
    <title>Red Hat JBoss BPM Suite :: Business central</title>
</head>
<body id="login">
<div id="rcue-login-screen">
    <img id="logo" src="<%=request.getContextPath()%>/images/login-screen-logo.png" alt="Red Hat Logo">

    <div id="login-wrapper" class="png_bg">

        <div id="login-top">
            <%--<img src="<%=request.getContextPath()%>/images/kie-ide.png" alt="KIE IDE Logo" title="Powered By Drools/jBPM"/>--%>
        </div>

        <div id="login-content" class="png_bg">
            <form action="j_security_check" method="POST">
                <fieldset>
                    <legend><img src="<%=request.getContextPath()%>/images/RH_JBoss_BPMS_Logo.png" alt="RED HAT JBOSS BPM SUITE" title="RED HAT JBOSS BPM SUITE"/></legend>
                    <c:if test="${param.message != null}">
                      <h3><c:out value="${param.message}"/></h3>
                    </c:if>
                    <label>Username</label><input value="" name="j_username" class="text-input" type="text" autofocus/>
                    <br style="clear: both;"/>
                    <label>Password</label><input name="j_password" class="text-input" type="password"/>
                    <br style="clear: both;"/>
                    <input class="button login" type="submit" value="Sign In"/>
                </fieldset>

            </form>
        </div>
    </div>
</div>
</body>
</html>
