/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import api.IntegritySession;
import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import api.ExceptionHandler;
import api.commands.TMTestCases;
import static excel.ExcelWorkbook.copyRow;
import static excel.ExcelWorkbook.deleteRow;
import excel.tm.TestResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author veckardt
 */
public class ExcelTSExport {

//    private final static IntegrityMessages MC
//            = new IntegrityMessages(ExcelTestSession.class);
    private final IntegritySession apiSession;
    private final String sessionID;
    private final String templateFile;
    private final String targetfile;
    private final String targetpath;
    private final Date asOfDate;
    private final int mode;
    private String logText = "";

    public ExcelTSExport(IntegritySession apiSession, int mode, String sessionID, String templateFile, Date asOfDate) {
        this.apiSession = apiSession;
        this.sessionID = sessionID;
        this.templateFile = templateFile;
        this.targetpath = "..\\data\\tmp\\web\\";
        this.targetfile = "TestSession_" + sessionID + ".xlsx";
        this.asOfDate = asOfDate;
        this.mode = mode;
    }

    public String getTargetfileName() {
        return targetfile;
    }

    public void export() {
        logText = "Exporting Test Session " + sessionID + " into an Excel file ...\n";
        // String[] resultFilter = {""};

        try {

            // updateProgress(1, 20);
//            if (!FileUtils.canWriteFile(targetfile)) {
//                // updateProgress(1, 1);
//                log("ERROR: Please close the file " + targetfile + " first, can not write to it!", 1);
//                return;
//            }
            //Read the spreadsheet that needs to be updated
            // FileInputStream input_document = new FileInputStream(new File(templateFile));
            URL u = new URL(templateFile);
            InputStream in = u.openStream();

            //Access the workbook
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            //Access the worksheet, so that we can update / modify it.
            XSSFSheet sheet = workbook.getSheetAt(0);

            // rename sheet name
            String sheetName = "Session " + sessionID;
            workbook.setSheetName(workbook.getSheetIndex(sheet), sheetName);

            // add Excel Rigion Name
            // Name name = workbook.createName();
            // name.setNameName("TableBegin");
            // name.setRefersToFormula("'"+sheetName+"'!$A$5");
            // XSSFWorkbook workbook = new XSSFWorkbook();
            // XSSFSheet sheet = workbook.createSheet("Session " + sessionID);
            // setStyles(workbook);
            WorkItem wi = apiSession.getWorkItem(sessionID, "Summary,State");
            sheet.getRow(0).getCell(2).setCellValue(sessionID);
            sheet.getRow(1).getCell(2).setCellValue(wi.getField("Summary").getString());
            sheet.getRow(2).getCell(2).setCellValue(wi.getField("State").getValueAsString());

            int cntTestCases = 0;
            int cntTestSteps = 0;
            // First Target Row = 7th
            int rownum = 6;
            // Get all test cases in the session
            TMTestCases tc = new TMTestCases("Text,Test Steps,Expected Results");

            // if (mode == 2) {
            //     cmd.addOption(new Option("queryDefinition", "(field[Last Result] contains \"Not Run\")"));
            // } else {
            // cmd.addOption(new Option("AsOf", dfDayTimeUS.format(asOfDate)));
            // }
            // cmd.addOption(new Option("fields", "ID,Text::rich,Test Steps"));
            tc.addSelection(sessionID);
            Response response = apiSession.execute(tc);

            int count = 2;

            WorkItemIterator witDoc = response.getWorkItems();

            while (witDoc.hasNext()) {
                // updateProgress(count++, response.getWorkItemListSize() + 2);
                // 
                WorkItem workItem = witDoc.next();
                TestResult trc = new TestResult(apiSession, "viewresult", sessionID + ":" + workItem.getId(), null);
                if ((mode == 2 && getString(trc.getVerdict()).contentEquals("-"))
                        || (mode == 2 && getString(trc.getVerdict()).contentEquals(""))
                        || (mode == 2 && getString(trc.getVerdict()).contentEquals("Not Tested"))
                        || (mode == 1)) {
                    rownum++;
                    cntTestCases++;
                    String text = workItem.getField("Text").getString();
                    String expectedResults = workItem.getField("Expected Results").getString();

                    // 1: enter the test case data
                    copyRow(workbook, sheet, 5, rownum);
                    sheet.getRow(rownum).getCell(0).setCellValue("TC-" + workItem.getId());
                    sheet.getRow(rownum).getCell(1).setCellValue((String) null);
                    sheet.getRow(rownum).getCell(2).setCellValue(text);
                    sheet.getRow(rownum).getCell(3).setCellValue(expectedResults);
                    sheet.getRow(rownum).getCell(4).setCellValue(trc.getVerdict());
                    sheet.getRow(rownum).getCell(5).setCellValue(trc.getAnnotation());

                    try {
                        for (String step : workItem.getField("Test Steps").getValueAsString().split(",")) {
                            // log("step.trim() '" + step.trim() + "'", 1);
                            if (!step.trim().isEmpty()) {
                                TestResult trs = new TestResult(apiSession, "stepresults", sessionID + ":" + workItem.getId() + ":" + step.trim(), step.trim());

                                rownum++;
                                cntTestSteps++;
                                // 2: enter the test step data
                                copyRow(workbook, sheet, 6, rownum);
                                sheet.getRow(rownum).getCell(1).setCellValue("TS-" + step.trim());
                                sheet.getRow(rownum).getCell(2).setCellValue(trs.getSummary());
                                sheet.getRow(rownum).getCell(3).setCellValue(trs.getDescription());
                                sheet.getRow(rownum).getCell(4).setCellValue(trs.getVerdict());
                                sheet.getRow(rownum).getCell(5).setCellValue(trs.getAnnotation());
                            }
                        }
                    } catch (Exception ex) {
                        logText += "No test steps assigned for Test Case " + workItem.getId() + ".";
                    }
                }
            }

            if (cntTestCases == 0) {
                logText += "INFO: No file written, all Test Cases have already a verdict assigned.\n";
            } else {
                try {
                    in.close();

                    // delete the template rows
                    if (cntTestCases > 0) {
                        deleteRow(sheet, 6);
                        deleteRow(sheet, 5);
                        try (FileOutputStream out = new FileOutputStream(new File(targetpath + targetfile))) {
                            workbook.write(out);
                        }
                        logText += "Excel file '" + targetfile + "' written.\n";
                        if (cntTestSteps > 0) {
                            logText += cntTestCases + " Test Cases and " + cntTestSteps + " Steps exported successfully.\n";
                        } else {
                            logText += cntTestCases + " Test Cases exported successfully.\n";
                        }

                        // updateProgress(1, 1);
                        // MessageBox.show(ExcelTestSession.stage,
                        //         MC.getMessage("EXCEL_FILE_GENERATED").replace("{0}", targetfile),
                        //         "Result",
                        //         MessageBox.ICON_ERROR | MessageBox.OK);
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ExcelTSExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    logText += "ERROR: " + ex.getMessage() + ": FileNotFoundException";
                } catch (IOException ex) {
                    Logger.getLogger(ExcelTSExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    logText += "ERROR: " + ex.getMessage() + ": IOException";
                }
            }
            // updateProgress(1, 1);
            // System.exit(1);

        } catch (APIException ex) {
            // Logger.getLogger(ExcelTestSessionController.class.getName()).log(Level.SEVERE, null, ex);
            ExceptionHandler eh = new ExceptionHandler(ex);
            // INTEGRITY_API_ERROR
//            MessageBox.show(ExcelTestSession.stage,
//                    MC.getMessage("INTEGRITY_API_ERROR").replace("{0}", eh.getMessage()),
//                    "API Error",
//                    MessageBox.ICON_ERROR | MessageBox.OK);

            logText += "ERROR: " + eh.getMessage() + ": APIException";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelTSExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            logText += "ERROR: " + ex.getMessage() + ": FileNotFoundException";
        } catch (IOException ex) {
            Logger.getLogger(ExcelTSExport.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            logText += "ERROR: " + ex.getMessage() + ": IOException";
        }
    }

//    @Override
//    protected Void call() throws Exception {
//        export();
//        Thread.sleep(1);
//        return null;
//    }
    // logs the text
//    public void log(String text, int level) {
//        // logArea.appendText("\n" + text);
//        System.out.println(text);
//    }
    private String getString(String text) {
        if (text == null) {
            return "";
        } else {
            return text;
        }
    }

    public String getLogText() {
        return logText;
    }
}
