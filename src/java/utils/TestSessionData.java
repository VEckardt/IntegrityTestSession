/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import api.ConnectionDetails;
import api.IntegritySession;
import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import excel.ExcelTSImport;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author veckardt
 */
public class TestSessionData {

    public static String getValidTestSessions(String uname, String pword) throws APIException, IOException {

        ConnectionDetails connection = new ConnectionDetails(uname, pword);
        IntegritySession intSession = new IntegritySession(connection);

        String text = "";
        text += "<label>Session:&nbsp;";
        text += "<select name=\"session\" size=\"1\">";

        List<WorkItem> workList = intSession.getAllItemsByQuery("My Active Test Sessions");
        for (WorkItem wi : workList) {
            text += "<option>Test Session " + wi.getId() + "</option>";
        }

//        text += "<option>Session 100</option>";
//        text += "<option>Session 101</option>";
//        text += "<option selected>Session 102</option>";
//        text += "<option>Session 103</option>";
//        text += "<option>Session 104</option>";
        text += "</select></label>";

        intSession.release();

        return text;
    }

    public static String uploadFile(List items, String uname, String pword) throws IOException, APIException {

        String textData = "";

        String uploadedFileName; // name of file on user's computer
        InputStream uploadedFileStream = null;

        Iterator iterator = items.iterator();
        while (iterator.hasNext()) {
            FileItem item = (FileItem) iterator.next();
            if (!item.isFormField()) {
                if (item.getSize() < 1) {
                    return ("ERROR: No file was selected.<br>");
                }

                uploadedFileName = item.getName();
                uploadedFileStream = item.getInputStream();
                textData += "File " + uploadedFileName + " provided with file size of " + item.getSize() + " bytes.<br>";
            }
        }

        XSSFWorkbook wb = new XSSFWorkbook(uploadedFileStream);
        // String uname = (String) session.getAttribute("uname");
        // String pword = (String) session.getAttribute("pword");
//                                    out.println("uname = " + uname);
//                                    out.println("pword = " + pword);

        ConnectionDetails connection = new ConnectionDetails(uname, pword);
        IntegritySession intSession = new IntegritySession(connection);

        ExcelTSImport myTask = new ExcelTSImport(intSession, "630", wb);
        myTask.importFile();
        intSession.release();

        textData += "<br>" + myTask.getLogText().replaceAll("\n", "<br>") + "<br>";

        return textData;

//        try {            
//                List items = upload.parseRequest(request);
//                Iterator iterator = items.iterator();
//                while (iterator.hasNext()) {
//                    FileItem item = (FileItem) iterator.next();
//                    if (!item.isFormField()) {
//                        if (item.getSize() < 1) {
//                            throw new Exception("No file was uploaded.");
//                        }
//
//                        uploadedFileName = item.getName();
//                        uploadedFileStream = item.getInputStream();
//                        out.println("File " + uploadedFileName + " provided with file size of " + item.getSize() + " bytes.<br>");
//                    }
//                }
//
//                XSSFWorkbook wb = new XSSFWorkbook(uploadedFileStream);
//                String uname = (String) session.getAttribute("uname");
//                String pword = (String) session.getAttribute("pword");
////                                    out.println("uname = " + uname);
////                                    out.println("pword = " + pword);
//
//                ConnectionDetails connection = new ConnectionDetails(uname, pword);
//                IntegritySession intSession = new IntegritySession(connection);
//
//                ExcelTSImport myTask = new ExcelTSImport(intSession, "630", wb);
//                myTask.importFile();
//                out.println("<br>" + myTask.getLogText().replaceAll("\n", "<br>") + "<br>");
//                intSession.release();
//            }
//        } catch (FileUploadException ex) {
//            // Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            out.println("ERROR 2: " + ex.getMessage());
//        } catch (Exception ex) {
//            // Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            out.println("ERROR 3: " + ex.getMessage());
//        }
    }
}
