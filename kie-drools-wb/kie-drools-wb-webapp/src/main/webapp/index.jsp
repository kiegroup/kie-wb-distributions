<%
    String queryString = request.getQueryString();
    String redirectURL = request.getContextPath()  +"/org.kie.workbench.drools.KIEDroolsWebapp/KIEDroolsWebapp.html?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>