/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import api.IntegritySession;
import api.commands.TMCreateResult;
import api.commands.TMEditResult;
import api.commands.TMTestCases;
import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import excel.tm.TestResult;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author veckardt
 */
public class ExcelTSImport {

//    private final static IntegrityMessages MC
//            = new IntegrityMessages(Servlet.class);
    private final IntegritySession apiSession;
    private final String sessionID;
    // private TextArea logArea;
    private final XSSFWorkbook workbook;
    // private PrintWriter out;
    private String logText = "";

    public ExcelTSImport(IntegritySession apiSession, String sessionID, XSSFWorkbook workbook) {
        this.apiSession = apiSession;
        // this.logArea = logArea;
        this.sessionID = sessionID;
        this.workbook = workbook;
        // this.out = out;
    }

    public void importFile() {
        logText = "Importing test results for session " + sessionID + " ...\n";
        // updateProgress(1, 20);

        int cntTestCases = 0;
        int cntTestSteps = 0;
        // updateProgress(1, 20);

        //Access the worksheet, so that we can update / modify it.
        XSSFSheet sheet = workbook.getSheetAt(0);

        String testCaseID = "";
        String tcResult = "";
        String tcAnnotation = "";
        int count = 1;
        int errCnt = 0;

        HashMap stepMap = new HashMap();
        Row tcRow = null;

        // loop through all rows in the excel sheet
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {

            // updateProgress(count++, sheet.getLastRowNum() + 1);
            Row row = rowIterator.next();

            if (row.getCell(0).getStringCellValue().startsWith("TC-")) {
                cntTestCases++;
                if (!testCaseID.isEmpty()) {
                    try {
                        logText += "Importing Case " + tcRow.getCell(0).getStringCellValue() + " with result '" + tcResult + "' ... ";
                        importResult(sessionID, testCaseID, stepMap, tcResult.contentEquals("-") ? "" : tcResult, tcAnnotation);
                        logText += "ok\n";
                    } catch (APIException ex) {
                        logText += ex.getMessage() + "\n";
                        errCnt++;
                    }
                    stepMap.clear();
                }

                testCaseID = row.getCell(0).getStringCellValue().replace("TC-", "");
                tcResult = row.getCell(4).getStringCellValue();
                tcAnnotation = row.getCell(5).getStringCellValue();
                tcRow = row;

            } else if (row.getCell(1).getStringCellValue().startsWith("TS-")) {
                cntTestSteps++;
                String testStepID = row.getCell(1).getStringCellValue().replace("TS-", "");
                String result = row.getCell(4).getStringCellValue();
                String annotation = row.getCell(5).getStringCellValue();
                // log("Importing Step " + row.getCell(1).getStringCellValue() + " with verdict '" + result + "' ...", 2);
                // log("Adding Step " + row.getCell(1).getStringCellValue() + " with verdict '" + result + "' ...", 2);
                stepMap.put(testCaseID + "*" + testStepID, new TestResult(testStepID, result, annotation));
            }
        }

        if (tcRow != null) {
            try {
                logText += "Importing Case " + tcRow.getCell(0).getStringCellValue() + " with result '" + tcResult + "' ... ";
                importResult(sessionID, testCaseID, stepMap, tcResult.contentEquals("-") ? "" : tcResult, tcAnnotation);
                logText += "ok\n";
            } catch (APIException ex) {
                logText += ex.getMessage() + "\n";
                errCnt++;
            }
        }

        logText += "Import finished.\n";
//
//        // updateProgress(1, 1);
        if (errCnt == 0) {
            if (cntTestSteps > 0) {
                logText += "INFO: " + cntTestCases + " Test Cases and " + cntTestSteps + " Test Steps imported successfully.\n";
            } else {
                logText += "INFO: " + cntTestCases + " Test Cases imported successfully.\n";
            }
        } else {
            logText += "ERROR: " + cntTestCases + " Test Cases and " + cntTestSteps + " Test Steps imported. " + errCnt + " errors detected.\n";
            logText += "Please check the Excel file provided for further error hints!\n";
        }

    }

    // logs the text
//    public void log(String text, int level) {
//        // logArea.appendText("\n" + text);
//        out.println(text);
//    }
    public String getLogText() {
        return logText;
    }

    private boolean importResult(String sessionID, String testCaseID, HashMap stepMap, String verdict, String annotation) throws APIException {

        // Step one list test steps in session
        // get current results
        // get current step results
        // push all into an array for comparision
        // if equal, then dont upload, else upload
        Map<String, String> currentResults = new HashMap<>();

        // Step one list test steps in session
        TMTestCases cmd4 = new TMTestCases("ID,Test Steps");
        cmd4.addSelection(sessionID);
        Response response = apiSession.execute(cmd4);
        // WorkItemIterator witDoc = response.getWorkItems();
        // while (witDoc.hasNext()) {
        WorkItem workItem = response.getWorkItem(testCaseID);
        // get current results
        TestResult trc = new TestResult(apiSession, "viewresult", sessionID + ":" + testCaseID, null);
        String resultString = getString(trc.getVerdict()) + ";" + getString(trc.getAnnotation());
        try {
            // get current step results
            // ListIterator<Item> li = trc.getTestSteps().getList().listIterator();

            // while (li.hasNext()) {
            //     Item resultItem = li.next();
            //     resultString = resultString + ";" + resultItem.getField("stepVerdict").getValueAsString() + ";" + getString(resultItem.getField("stepAnnotation").getString());
            //     // log("In loop", 3);
            // }
            for (String step : workItem.getField("Test Steps").getValueAsString().split(",")) {
                if (!step.trim().isEmpty()) {
                    TestResult trs = new TestResult(apiSession, "stepresults", sessionID + ":" + workItem.getId() + ":" + step.trim(), step.trim());
                    resultString = resultString + ";" + getString(trs.getVerdict()) + ";" + getString(trs.getAnnotation());
                }
            }
        } catch (Exception ex) {
            // ExceptionHandler eh = new ExceptionHandler(ex);
            // log ("Exception:"+ex.getMessage(),2);
            Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            // log("No test steps assigned for Test Case " + testCaseID + ".", 1);
        }
        currentResults.put(sessionID + ":" + testCaseID, resultString);
        // }
        // push all into an array for comparision
        // if equal, then dont upload, else upload

        // for (Entry entry : currentResults.entrySet()) {
        //     log(">> " + entry.getKey() + ": " + entry.getValue(), 1);
        // }
        try {
            // first, try to create a new result
            // if this works, fine, otherwiese move on with edit instead
            TMCreateResult cmd2 = new TMCreateResult(sessionID, verdict, annotation);

            String newResultString = verdict + ";" + annotation;

            // Set set = 
            // Get an iterator
            Iterator i = stepMap.entrySet().iterator();
            // Display elements
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                TestResult tr = (TestResult) me.getValue();
                // System.out.print(me.getKey() + ": ");
                // System.out.println(me.getValue());

                cmd2.addStepVerdict("stepID=" + tr.getID() + ":verdict=" + tr.getVerdict2() + ":annotation=" + tr.getAnnotation() + "");
                newResultString = newResultString + ";" + tr.getVerdict() + ";" + tr.getAnnotation();
            }

            cmd2.addSelection(testCaseID);
            String key = sessionID + ":" + testCaseID;

            String currentResultString = currentResults.get(sessionID + ":" + testCaseID);
            // log("currentResultString (" + key + "): " + currentResultString, 3);
            // log("newResultString     (" + key + "): " + newResultString, 3);

            if (!currentResultString.contentEquals(newResultString)) {
                Response response2 = apiSession.execute(cmd2);
                response2.getExitCode();
            } else {
                // log("Create result skipped for: " + sessionID + ":" + testCaseID + ", is the same.", 1);
            }

        } catch (APIException ex) {
            // if the creation is not possible, then we will try to edit
            //
            try {
                Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                // try overwrting
                TMEditResult cmd3 = new TMEditResult(sessionID, verdict, annotation);

                String newResultString = verdict + ";" + annotation;

                Set set = stepMap.entrySet();
                // Get an iterator
                Iterator i = set.iterator();
                // Display elements
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    TestResult tr = (TestResult) me.getValue();
                    // System.out.print(me.getKey() + ": ");
                    // System.out.println(me.getValue());

                    cmd3.addStepVerdict("stepID=" + tr.getID() + ":verdict=" + tr.getVerdict2() + ":annotation=" + tr.getAnnotation() + "");
                    newResultString = newResultString + ";" + tr.getVerdict() + ";" + tr.getAnnotation();
                }
                cmd3.addSelection(testCaseID);
                // String key = sessionID + ":" + testCaseID;
                String currentResultString = currentResults.get(sessionID + ":" + testCaseID);
                // log("currentResultString (" + key + "): " + currentResultString, 3);
                // log("newResultString     (" + key + "): " + newResultString, 3);

                if (!currentResultString.contentEquals(newResultString)) {
                    Response response3 = apiSession.execute(cmd3);
                    response3.getExitCode();
                } else {
                    // log("Edit result skipped for: " + sessionID + ":" + testCaseID + ", is the same.", 1);
                }

            } catch (APIException ex1) {
                Logger.getLogger(ExcelTSImport.class.getName()).log(Level.SEVERE, ex1.getMessage(), ex1);
                throw ex1;
            }
        }

        return true;
    }

    private String getString(String text) {
        if (text == null) {
            return "";
        } else {
            return text;
        }
    }
}
