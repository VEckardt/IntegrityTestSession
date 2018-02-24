<%-- 
    Document   : TstSession
    Created on : Feb 20, 2018, 11:38:15 PM
    Author     : veckardt
--%>

<%@page import="api.IntegritySession"%>
<%@page import="api.ConnectionDetails"%>
<%@page import="com.mks.api.response.APIException"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="org.apache.commons.fileupload.FileUploadException"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.FileItemFactory"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="utils.TestSessionData"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Session Upload</title>
        <link rel="stylesheet" type="text/css" href="images/acls.css" />
        <link rel="stylesheet" type="text/css" href="images/layout.css" />
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
        <div align=right><%=new java.util.Date()%></div>
        <h1>Test Session Loader</h1>        
        <a href='/IntegrityTestSession'>Logout</a>&nbsp;&gt;&nbsp;
        <%
            out.println("<a href='/IntegrityTestSession/TestSession.jsp;jsessionid=" + session.getId() + "'>Back</a>");
        %>
        <hr>
        <div class="tabreiter">
            <ul>
                <li>
                    <input type="radio" name="tabreiter-0" checked="checked" id="tabreiter-0-1" /><label for="tabreiter-0-1">Test Session Upload Log</label>
                    <div>
                        <br>
                        <%
                            try {
                                boolean isMultipart = ServletFileUpload.isMultipartContent(request);

                                if (isMultipart) {
                                    FileItemFactory factory = new DiskFileItemFactory();
                                    ServletFileUpload upload = new ServletFileUpload(factory);
                                    List items = upload.parseRequest(request);
                                    String uname = (String) session.getAttribute("uname");
                                    String pword = (String) session.getAttribute("pword");
                                    
                                    out.println (TestSessionData.uploadFile(items, uname, pword));
    
                                }
                            } catch (FileUploadException ex) {
                                Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                                out.println("ERROR 1:" + ex.getMessage());
                            } catch (IOException ex) {
                                Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                                out.println("ERROR 2:" + ex.getMessage());
                            } catch (APIException ex) {
                                Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                                out.println("ERROR 3:" + ex.getMessage());
                            }
                        %>
                    </div>
                </li>
            </ul>
        </div>   
    </body>
</html>        
