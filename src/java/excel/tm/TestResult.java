/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package excel.tm;

import api.IntegritySession;
import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import static excel.tm.TestUtils.testResultFields;
import java.util.Date;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 *
 * @author veckardt
 */
public class TestResult {

    private String testCaseID;
    private String testStepID;
    private String verdict;
    private String annotation;
    private String relatedItems;
    private String sessionID;
    private Date modifiedDate;
    private String modifiedUser;
    private String verdictType;
    // private String redmineIds = "";
    private TestResultFieldList additionalFieldValues = new TestResultFieldList();

    private static final String notTestedText = "Not Tested";
    private String summary;
    private String description;
    private String ID;

    public TestResult(WorkItem wi) {
        setTestResult(wi);
    }

    public TestResult(String ID, String verdict, String annotation) {
        this.ID = ID;
        this.verdict = verdict;
        this.annotation = annotation;
    }

    public TestResult(
            String testCaseID,
            String testStepID,
            String verdict,
            String annotation) {
        this.testCaseID = testCaseID;
        this.testStepID = testStepID;
        this.verdict = verdict;
        this.annotation = annotation;
    }

    public TestResult(IntegritySession imSession, String type, String selection) {
        try {
            Command cmd = new Command(Command.TM, type);
            cmd.addSelection(selection);
            Response result = imSession.execute(cmd);
            WorkItem wi = result.getWorkItem(selection);
            setTestResult(wi);

        } catch (APIException ex) {
            verdict = notTestedText;
            annotation = "";
            relatedItems = "";
            // redmineIds = "";
        }
    }

    public TestResult(IntegritySession imSession, String type, String selection, String ID) {
        try {
            if (ID != null) {
                Command cmd = new Command(Command.IM, "issues");

                cmd.addOption(new Option("fields", "Summary,Description"));
                cmd.addSelection(ID);
                Response result = imSession.execute(cmd);
                WorkItem wi = result.getWorkItem(ID);
                summary = wi.getField("Summary").getString();
                description = wi.getField("Description").getString();
            }

            Command cmd = new Command(Command.TM, type);
            if (type.toLowerCase().contentEquals("viewresult")) {
                cmd.addOption(new Option("showSteps"));
            }
            cmd.addSelection(selection);
            Response result = imSession.execute(cmd);
            // ResponseUtil.printResponse(result, 1, System.out);

            WorkItem wi = result.getWorkItem(selection);
            setTestResult(wi);

        } catch (APIException ex) {
            verdict = notTestedText;
            annotation = "";
            relatedItems = "";
        }
    }

    //
    // Get Methods
    //
    public String getID() {
        return (isEmpty(ID) ? "-" : ID);
    }

    public String getTestCaseID() {
        return testCaseID;
    }

    public String getTestStepID() {
        return testStepID;
    }

    public String getStepVerdict() {
        // --stepVerdict=stepID=1667:verdict=Passed:annotation=good
        return "stepID=" + testStepID + ":verdict=" + verdict + ":annotation=" + annotation;
    }

    // public String getVerdict() {
    //     // return (isEmpty(verdict) ? "-" : verdict);
    //     return verdict;
    // }
    public String getVerdict() {
        return (isEmpty(verdict) ? "" : verdict);
    }

    public String getVerdict2() {
        // return (isEmpty(verdict) ? "-" : verdict);
        return (verdict.contentEquals(notTestedText) ? "" : verdict);
    }

    // public String getAnnotation() {
    //     //return (isEmpty(annotation) ? "-" : annotation);
    //     return annotation;
    // }
    public String getAnnotation() {
        return (isEmpty(annotation) ? "" : annotation);
    }

    public String getRelatedItems() {
        return relatedItems;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getSessionID() {
        return sessionID;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public String getRedmineIDs() {

        ListIterator li = additionalFieldValues.listIterator();
        while (li.hasNext()) {
            TestResultField trf = (TestResultField) li.next();
            if (trf.getFieldName().toLowerCase().startsWith("redmine")) {
                return trf.getFieldValue().replace(";", ",") + "";
            }
        }
        return "";
    }

    // public Field getTestSteps() {
    //     return testSteps;
    // }
    public Boolean isEmpty(String string) {
        return (string == null || string.trim().isEmpty());
    }

    public static String getNotTestedText() {
        return notTestedText;
    }

    // 
    // Set Methods
    //
    final void setTestResult(WorkItem wi) {
        verdict = wi.getField("verdict").getValueAsString();
        annotation = wi.getField("annotation").getValueAsString();
        sessionID = wi.getField("sessionID").getValueAsString();
        try {
            modifiedDate = wi.getField("modifiedDate").getDateTime();
        } catch (NoSuchElementException ex) {
            modifiedDate = null;
        }
        try {
            modifiedUser = wi.getField("modifiedUser").getValueAsString();
        } catch (NoSuchElementException ex) {
            modifiedUser = null;
        }
        verdictType = wi.getField("verdictType").getString();
        try {
            relatedItems = wi.getField("relatedItems").getValueAsString();
        } catch (Exception ex) {
            relatedItems = "";
        }

        for (TestResultField trf : testResultFields) {
            try {
                String fieldValue = wi.getField(trf.getFieldName()).getValueAsString();
                additionalFieldValues.add(new TestResultField(trf.getFieldName(), fieldValue));
            } catch (Exception ex) {
            }
        }
    }

    /*
     * <WorkItem id="8136:8129" displayId="8136:8129" modelType="tm.TestResult">
     <Field name="sessionID">
     <Value dataType="int">
     <TokenValue>8136</TokenValue>
     </Value>
     </Field>
     <Field name="caseID">
     <Value dataType="int">
     <TokenValue>8129</TokenValue>
     </Value>
     </Field>
     <Field name="sharesCaseID">
     <Value dataType="string">
     <TokenValue></TokenValue>
     </Value>
     </Field>
     <Field name="modifiedDate">
     <Value dataType="datetime">
     <TokenValue>2014-07-06T09:20:08</TokenValue>
     </Value>
     </Field>
     <Field name="modifiedUser">
     <Item id="veckardt" displayId="veckardt" modelType="im.User">
     </Item>
     </Field>
     <Field name="verdict">
     <Item id="Passed" displayId="Passed" modelType="tm.TestVerdict">
     </Item>
     </Field>
     <Field name="verdictIcon">
     <Item id="Passed" displayId="Passed" modelType="tm.TestVerdict">
     </Item>
     </Field>
     <Field name="verdictType">
     <Value dataType="string">
     <TokenValue>Pass</TokenValue>
     </Value>
     </Field>
     <Field name="verdictTypeIcon">
     <Value dataType="string">
     <TokenValue>Pass</TokenValue>
     </Value>
     </Field>
     <Field name="annotation">
     <Value dataType="string">
     <TokenValue>worked</TokenValue>
     </Value>
     </Field>
     <Field name="hasStepResult">
     <Value dataType="boolean">
     <TokenValue>false</TokenValue>
     </Value>
     </Field>
     <Field name="hasAttachment">
     <Value dataType="boolean">
     <TokenValue>false</TokenValue>
     </Value>
     </Field>
     <Field name="hasRelatedItem">
     <Value dataType="boolean">
     <TokenValue>false</TokenValue>
     </Value>
     </Field>
     <Field name="Test Log">
     <Value></Value>
     </Field>
     </WorkItem>
     */
}
