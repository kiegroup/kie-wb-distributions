<%--
  ~ Copyright 2012 JBoss Inc
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


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/base.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/forms.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/login-screen.css">
    <title>jBPM Console NG</title>
</head>
<body id="login">
<div id="rcue-login-screen">
    <img id="logo" src="<%=request.getContextPath()%>/images/login-screen-logo.png" alt="Red Hat Logo">

    <div id="login-wrapper" class="png_bg">

        <div id="login-top">

            <%--            <img src="<%=request.getContextPath()%>/images/kie-ide.png" alt="KIE IDE Logo" title="Powered By Drools/jBPM"/> --%>

        </div>

        <div id="login-content" class="png_bg">
            <h3><%=request.getParameter("message") == null ? "" : request.getParameter("message")%>
            </h3>

            <form action="j_security_check" method="POST">
                <fieldset>
                    <legend><img src="<%=request.getContextPath()%>/images/RH-Product-Name.png" alt="Provisional, temporary logo" title="Provisional, temporary logo"/></legend>
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
