<%
  String queryString = request.getQueryString();
  String redirectURL = "login?"+(queryString==null?"":queryString);
  response.sendRedirect(redirectURL);
%>
