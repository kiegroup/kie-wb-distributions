<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.kie.KIEShowcase/KIE.html?message=Login failed: Not Authorized";
  response.sendRedirect(redirectURL);
%>