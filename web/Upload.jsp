<%-- 
    Document   : TstSession
    Created on : Feb 20, 2018, 11:38:15 PM
    Author     : veckardt
--%>

<%@page import="excel.ExcelTSImport"%>
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
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.FileItemFactory"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Session Upload</title>
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
                    <input type="radio" name="tabreiter-0" checked="checked" id="tabreiter-0-1" /><label for="tabreiter-0-1">Test Session Upload Log</label>
                    <div>
                        <%
                            try {
                                boolean isMultipart = ServletFileUpload.isMultipartContent(request);

                                if (isMultipart) {
                                    FileItemFactory factory = new DiskFileItemFactory();
                                    ServletFileUpload upload = new ServletFileUpload(factory);

                                    String uploadedFileName; // name of file on user's computer
                                    InputStream uploadedFileStream = null;

                                    List items = upload.parseRequest(request);
                                    Iterator iterator = items.iterator();
                                    while (iterator.hasNext()) {
                                        FileItem item = (FileItem) iterator.next();
                                        if (!item.isFormField()) {
                                            if (item.getSize() < 1) {
                                                throw new Exception("No file was uploaded.");
                                            }

                                            uploadedFileName = item.getName();
                                            uploadedFileStream = item.getInputStream();
                                            out.println("File " + uploadedFileName + " provided with file size of " + item.getSize() + " bytes.<br>");
                                        }
                                    }

                                    XSSFWorkbook wb = new XSSFWorkbook(uploadedFileStream);
                                    String uname = (String) session.getAttribute("uname");
                                    String pword = (String) session.getAttribute("pword");
//                                    out.println("uname = " + uname);
//                                    out.println("pword = " + pword);

                                    ConnectionDetails connection = new ConnectionDetails(uname, pword);
                                    IntegritySession intSession = new IntegritySession(connection);

                                    ExcelTSImport myTask = new ExcelTSImport(intSession, "630", wb);
                                    myTask.importFile();
                                    out.println("<br>" + myTask.getLogText().replaceAll("\n","<br>")+"<br>");
                                    intSession.release();
                                }
                            } catch (FileUploadException ex) {
                                Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                                out.println("ERROR:" + ex.getMessage());
                            } catch (Exception ex) {
                                Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                                out.println("ERROR:" + ex.getMessage());
                            }
                        %>
                    </div>
                </li>
            </ul>
        </div>   
    </body>
</html>        
