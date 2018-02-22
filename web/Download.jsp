<%-- 
    Document   : TstSession
    Created on : Feb 20, 2018, 11:38:15 PM
    Author     : veckardt
--%>

<%@page import="excel.ExcelTSExport"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="org.apache.commons.fileupload.FileUploadException"%>
<%@page import="api.IntegritySession"%>
<%@page import="api.ConnectionDetails"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.io.InputStream"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Session Download</title>
        <link rel="stylesheet" type="text/css" href="images/layout.css" />
    </head>
    <body>
        <a href='/IntegrityTestSession'>Logout</a>&nbsp;&gt;&nbsp;
        <%
            out.println("<a href='/IntegrityTestSession/TestSession.jsp;jsessionid=" + session.getId() + "'>Back</a>");
        %> 
        <hr>
        <div class="tabreiter">
            <ul>
                <li>
                    <input type="radio" name="tabreiter-0" checked="checked" id="tabreiter-0-1" /><label for="tabreiter-0-1">Test Session Download Log</label>
                    <div>
                        <%
                            try {
                                    String uname = (String) session.getAttribute("uname");
                                    String pword = (String) session.getAttribute("pword");
//                                    out.println("uname = " + uname);
//                                    out.println("pword = " + pword);

                                    ConnectionDetails connection = new ConnectionDetails(uname, pword);
                                    IntegritySession intSession = new IntegritySession(connection);

                                    String testSession = request.getParameter("session").replaceAll("\\D+","");

                                    ExcelTSExport myTask = new ExcelTSExport(intSession, 1, testSession, connection.getProtcolAndHost()+"/TestSessionTemplate.xlsx", null);
                                    myTask.export();
                                    intSession.release();
                                    
                                    out.println("<br>" + myTask.getLogText().replaceAll("\n","<br>")+"<br>");
                                    out.println("<a href='/"+myTask.getTargetfileName()+"'>Download Excel File</a>");

                            } catch (Exception ex) {
                                Logger.getLogger(ExcelTSExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                                out.println("ERROR:" + ex.getMessage());
                            }
                        %>
                    </div>
                </li>
            </ul>
        </div>   
        <br>



    </body>
</html>        
