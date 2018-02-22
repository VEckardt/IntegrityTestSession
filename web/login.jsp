<%-- 
    Document   : login
    Created on : 19-Jan-2018, 14:16:15
    Author     : veckardt
--%>

<%@page import ="api.*" %>
<%@page import ="com.mks.api.response.*" %>
<%
    String uname = request.getParameter("uname");
    String pword = request.getParameter("pword");
    // Class.forName("com.mysql.jdbc.Driver");

    ConnectionDetails connection = new ConnectionDetails(uname, pword);
    try {
        IntegritySession integritySession = new IntegritySession(connection);
        session.setAttribute("uname", uname);
        session.setAttribute("pword", pword);
        //out.println("welcome " + userid);
        //out.println("<a href='logout.jsp'>Log out</a>");
        integritySession.release();
        String destination = "/IntegrityTestSession/TestSession.jsp";
        // request.setAttribute("uname", uname);
        // request.setAttribute("pword", pword);
        response.sendRedirect(response.encodeRedirectURL(destination));
    } catch (APIException apiEx) {

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Test Session Servlet</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"images/layout.css\" />");
        out.println("</head>");
        out.println("<body>");
        out.println("Invalid login<a href='index.jsp'>try again</a>");
        out.println("</body>");
        out.println("</html>");
    }
%>
