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
public class TMCreateResult extends Command {

    public TMCreateResult(String sessionID, String verdict, String annotation) {
        super.app = Command.TM;
        super.command = "createresult";
        this.addOption(new Option("sessionID", sessionID));
        this.addOption(new Option("verdict", verdict));
        this.addOption(new Option("annotation", annotation));
    }

    public void addStepVerdict(String stepVerdict) {
        this.addOption(new Option("stepVerdict", stepVerdict));
    }

}
