/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.commands;

import com.mks.api.Command;
import com.mks.api.Option;

/**
 *
 * @author veckardt
 */
public class TMTestCases extends Command {

    public TMTestCases(String fields) {
        command = "testcases";
        app = Command.TM; 
        this.addOption(new Option("substituteParams"));
        this.addOption(new Option("fields", fields));
    }

    public void addFields(String fields) {
        this.addOption(new Option("fields", fields));
    }

}
