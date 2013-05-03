<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.kie.workbench.KIEWebapp/KIEWebapp.html?message=Login failed: Invalid UserName or Password";
  response.sendRedirect(redirectURL);
%>