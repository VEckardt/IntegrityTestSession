/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author veckardt
 */
public class ExcelWorkbook {

    //
    public static String beginTag = "<%";
    public static String endTag = "%>";
    public static int beginContentRow = 0;
    public static int endContentRow = 0;
    public static int rownum = 0;
    public static int cntRows = 0;
    public static String headerRow;
    private static String itemType = "";
    private static String firstItemType = "";

    //
    private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
    private static String fieldNameList = "";

    /**
     * recalcWorkbook
     *
     * @param workbook
     */
    public static void recalcWorkbook(Workbook workbook) {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }
        }
    }

    /**
     *
     * @param sheet
     * @param rowIndex
     */
    public static void deleteRow(XSSFSheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        log("Deleting row " + (rowIndex + 1) + " ...", 2);
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            XSSFRow removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }

    /**
     * removeTags
     *
     * @param string
     * @return
     */
    public static String removeTags(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        Matcher m = REMOVE_TAGS.matcher(string);
        return m.replaceAll("");
    }

    /**
     * setValue
     *
     * @param sheet
     * @param row
     * @param col
     * @param value
     */
    public void setValue(XSSFSheet sheet, int row, int col, String value) {
        try {

            try {
                Integer.parseInt(value);
                sheet.getRow(row).getCell(col).setCellValue(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                sheet.getRow(row).getCell(col).setCellValue(StringEscapeUtils.unescapeHtml4(value));
            }
        } catch (NullPointerException ex) {
            log("Can not set (" + (row + 1) + "," + (col + 1) + "): Value '" + value + "'", 1);
            System.exit(1);
        }
    }

    /**
     *
     * @param worksheet
     * @param rowNumber
     */
    public void retrieveFieldNameList(XSSFSheet worksheet, int rowNumber) {
        // String fieldNameList ="";
        log("worksheet.getRow(" + rowNumber + ").getLastCellNum():" + worksheet.getRow(rowNumber).getLastCellNum(), 3);
        for (int i = 0; i <= worksheet.getRow(rowNumber).getLastCellNum(); i++) {
            try {
                String cellValue = worksheet.getRow(rowNumber).getCell(i).getStringCellValue();

                if (cellValue.startsWith(beginTag) && cellValue.endsWith(endTag)) {
                    cellValue = cellValue.replace(beginTag, "");
                    cellValue = cellValue.replace(endTag, "");
                    fieldNameList = fieldNameList + (fieldNameList.isEmpty() ? "" : ",") + cellValue;
                }
            } catch (NullPointerException ex) {

            }
        }
        log("fieldNameList: " + fieldNameList, 3);
        // return fieldNameList;
    }

    public void clearCellsWithTag(XSSFSheet sheet, int row) {

        for (int col = 0; col <= sheet.getRow(beginContentRow + 1).getLastCellNum(); col++) {
            try {
                // read the tag at col
                String tag = sheet.getRow(row).getCell(col).getStringCellValue();
                // is it a tag?
                if (tag.startsWith(beginTag) && tag.endsWith(endTag)) {
                    // remove the tag
                    setValue(sheet, row, col, "");
                }
            } catch (NullPointerException | IllegalStateException ex) {
            }
        }

    }

    /**
     * Find the section within the document with beginContent and endContent
     *
     * @param worksheet
     */
    public void findDetailBlock(XSSFSheet worksheet) {
        for (int i = 0; i <= worksheet.getLastRowNum(); i++) {
            try {
                if (worksheet.getRow(i).getCell(0).getStringCellValue().contentEquals(beginTag + "beginContent" + endTag)) {
                    log("beginContentRow: " + (i + 1), 2);
                    beginContentRow = i;
                } else if (worksheet.getRow(i).getCell(0).getStringCellValue().contentEquals(beginTag + "endContent" + endTag)) {
                    log("endContentRow: " + (i + 1), 2);
                    endContentRow = i;
                }
            } catch (NullPointerException ex) {
                // skip over any row without any data in it
            }
        }
    }

    /**
     * copyRow
     *
     * @param workbook
     * @param worksheet
     * @param sourceRowNum
     * @param destinationRowNum
     */
    public static void copyRow(XSSFWorkbook workbook, XSSFSheet worksheet, int sourceRowNum, int destinationRowNum) {
        // Get the source / new row
        XSSFRow newRow = worksheet.getRow(destinationRowNum);
        XSSFRow sourceRow = worksheet.getRow(sourceRowNum);

        log("Copying row " + (sourceRowNum + 1) + " to " + (destinationRowNum + 1) + " ...", 2);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
        } else {
            newRow = worksheet.createRow(destinationRowNum);
        }

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            XSSFCell oldCell = sourceRow.getCell(i);
            XSSFCell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                newCell = null;
                continue;
            }

            // Copy style from old cell and apply to new cell
            XSSFCellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());;
            newCell.setCellStyle(newCellStyle);

            // If there is a cell comment, copy
            if (newCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }

        // If there are are any merged regions in the source row, copy to new row
        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
                        (newRow.getRowNum()
                        + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                worksheet.addMergedRegion(newCellRangeAddress);
            }
        }

    }

    public static void log(String text, int indent) {

    }

    /**
     * clearRemainingTags
     *
     * @param sheet
     */
    public void clearRemainingTags(XSSFSheet sheet) {
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            clearCellsWithTag(sheet, i);
        }
    }

}
