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
import java.io.IOException;
import java.util.List;

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
}
