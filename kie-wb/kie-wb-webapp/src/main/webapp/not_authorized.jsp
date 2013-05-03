<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.kie.workbench.KIEWebapp/KIEWebapp.html?message=Login failed: Not Authorized";
  response.sendRedirect(redirectURL);
%>