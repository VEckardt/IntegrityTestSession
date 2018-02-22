/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel.tm;

import api.IntegritySession;
import com.mks.api.response.APIException;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author veckardt
 */
public class TestUtils {

    // Private
    public static Boolean addSeparateDefectSection = true;

    private static Map<String, Boolean> testCaseTypes = new TreeMap<String, Boolean>();
    public static TestResultFieldList testResultFields = new TestResultFieldList();

    public static void init(IntegritySession imSession) throws APIException {
        testResultFields.init(imSession);
    }

    private TestUtils() {

    }

}
