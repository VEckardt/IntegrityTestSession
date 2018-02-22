<%-- 
    Document   : index
    Created on : Feb 19, 2018, 8:53:25 PM
    Author     : veckardt
--%>

<%@page import="api.IntegritySession"%>
<%@page import="api.ConnectionDetails"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Session</title>
        <link rel="stylesheet" href="images/acls.css" type="text/css" />
        <link rel="stylesheet" href="images/layout.css" type="text/css" />
        <style>
            body {
                background-image: url(./images/IntegrityWithSymbol.png);            
                background-origin: content-box;
                background-repeat: no-repeat;
                background-position: left 10px top 10px;
            }
        </style>          
    </head>
    <body>
        <%@ page import="utils.*" isThreadSafe="false"%>
        <div align=right><%=new java.util.Date()%></div>
        <h1>Test Session Loader</h1>
        <a href='/IntegrityTestSession'>Logout</a>
        <hr>
        <div class="tabreiter">
            <ul>
                <li>
                    <input type="radio" name="tabreiter-0" checked="checked" id="tabreiter-0-0" /><label for="tabreiter-0-0">Test Session Download</label>
                    <div>
                        <%
                            out.println("<form name='frm' action='/IntegrityTestSession/Download.jsp;jsessionid=" + session.getId() +
                            "' method='get' enctype='multipart/form-data'>");
                        %>
                        <p>Please select the Test Session that you want to download:</p>
                        <% 

            String uname = (String) session.getAttribute("uname");
            String pword = (String) session.getAttribute("pword");
            
            out.println(TestSessionData.getValidTestSessions(uname, pword));

                        %>
                        <input type="submit" value="Download">
                        <% 
                            out.println("</form>");
                        %>                         
                    </div>
                </li><li>
                    <input type="radio" name="tabreiter-0" id="tabreiter-0-1" /><label for="tabreiter-0-1">Test Session Upload</label>
                    <div>
                        <%
                            out.println("<form name='frm' action='/IntegrityTestSession/Upload.jsp;jsessionid=" + session.getId() +
                            "' method='post' enctype='multipart/form-data'>");
                        %> 
                        <p>Please select the Excel file that you want to upload:</p>
                        <input type="file" name="fl_upload">
                        <input type="submit" value="Upload">
                        <input type="reset" value="cancel">
                        <% 
                          out.println("</form>");
                        %>         
                    </div>
                </li>
            </ul>
        </div>
    </body>
</html>
