<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.kie.workbench.drools.KIEDroolsWebapp/KIEDroolsWebapp.html?message=Login failed: Not Authorized";
  response.sendRedirect(redirectURL);
%>