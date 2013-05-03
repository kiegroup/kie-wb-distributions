<%
    String queryString = request.getQueryString();
    String redirectURL = request.getContextPath()  +"/org.kie.workbench.KIEWebapp/KIEWebapp.html?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>