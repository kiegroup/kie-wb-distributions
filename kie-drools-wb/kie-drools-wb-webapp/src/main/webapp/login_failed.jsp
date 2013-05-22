<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.kie.workbench.drools.KIEDroolsWebapp/KIEDroolsWebapp.html?message=Login failed: Invalid UserName or Password";
  response.sendRedirect(redirectURL);
%>