<%
    String queryString = request.getQueryString();
    String redirectURL = request.getContextPath()  +"/org.kie.KIEShowcase/KIE.html?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>