<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.kie.KIEShowcase/KIE.html?message=Login failed: Invalid UserName or Password";
  response.sendRedirect(redirectURL);
%>