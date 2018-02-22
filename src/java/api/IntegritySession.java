/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.4 $
 * Last changed:   $Date: 2017/05/13 22:22:54CEST $
 */
package api;

import com.mks.api.CmdRunner;
import com.mks.api.Command;
import com.mks.api.IntegrationPoint;
import com.mks.api.IntegrationPointFactory;
import com.mks.api.Option;
import com.mks.api.Session;
import com.mks.api.response.APIConnectionException;
import com.mks.api.response.APIException;
import com.mks.api.response.ApplicationConnectionException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IntegritySession {

    private IntegrationPoint integrationPoint = null;
    private Session apiSession = null;
    private ConnectionDetails connection;

//    public IntegritySession() throws APIException, IOException {
//        connection = new ConnectionDetails();
//        IntegrationPointFactory ipf = IntegrationPointFactory.getInstance();
//        try {
//
//            integrationPoint = ipf.createIntegrationPoint(connection.getHostname(), connection.getPort(), 4, 11);
//            apiSession = integrationPoint.createNamedSession(null, null, connection.getUser(), connection.getPassword());
//            apiSession.setDefaultUsername(connection.getUser());
//            apiSession.setDefaultPassword(connection.getPassword());
//
//            out.println(connection.getLoginInfo());
//            // System.out.println("IMConfig: Current Dir: " + System.getProperty("user.dir"));
//
//            Command cmd = new Command(Command.IM, "connect");
//            // cmd.addOption(new Option("gui"));
//            execute(cmd);
//
//        } catch (APIConnectionException ce) {
//            out.println("IMConfig: Unable to connect to Integrity Server");
//            throw ce;
//        } catch (ApplicationConnectionException ace) {
//            out.println("IMConfig: Integrity Client unable to connect to Integrity Server");
//            throw ace;
//        } catch (APIException apiEx) {
//            out.println("IMConfig: Unable to initialize");
//            throw apiEx;
//        }
//    }
    public IntegritySession(ConnectionDetails connectionDetails) throws APIException {
        connection = connectionDetails;
        IntegrationPointFactory ipf = IntegrationPointFactory.getInstance();
        try {
            integrationPoint = ipf.createIntegrationPoint(connection.getHostname(), connection.getPort(), false, 4, 16);
            apiSession = integrationPoint.createNamedSession(null, null, connection.getUser(), connection.getPassword());
            apiSession.setDefaultUsername(connection.getUser());
            apiSession.setDefaultPassword(connection.getPassword());

            // out.println(connection.getLoginInfo());
            // System.out.println("IMConfig: Current Dir: " + System.getProperty("user.dir"));
            Command cmd = new Command(Command.IM, "connect");
            // cmd.addOption(new Option("gui"));
            execute(cmd);

        } catch (APIConnectionException ce) {
            // out.println("IMConfig: Unable to connect to Integrity Server");
            throw ce;
        } catch (ApplicationConnectionException ace) {
            // out.println("IMConfig: Integrity Client unable to connect to Integrity Server");
            throw ace;
        } catch (APIException apiEx) {
            // out.println("IMConfig: Unable to initialize");
            throw apiEx;
        }
    }

//    public IntegritySession() throws APIException, IOException {
//
//        IntegrationPointFactory ipf = IntegrationPointFactory.getInstance();
//        try {
//
//            integrationPoint = ipf.createIntegrationPoint(hostname, port, 4, 11);
//            apiSession = integrationPoint.createNamedSession(null, null, username, password);
//            apiSession.setDefaultUsername(username);
//            apiSession.setDefaultPassword(password);
//
//            System.out.println("IMConfig: Logging in " + hostname + ":" + port + " with " + username + " ..");
//            // System.out.println("IMConfig: Current Dir: " + System.getProperty("user.dir"));
//
//            // cmdRunner = apiSession.createCmdRunner();
//            // cmdRunner.setDefaultHostname(host);
//            // cmdRunner.setDefaultPort(port);
//            // cmdRunner.setDefaultUsername("veckardt");
//            // cmdRunner.setDefaultPassword("");
//            // }
//            // apiSession = integrationPoint.getCommonSession();
//            Command cmd = new Command(Command.IM, "connect");
//            // cmd.addOption(new Option("gui"));
//            this.execute(cmd);
//
//        } catch (APIConnectionException ce) {
//            System.out.println("IMConfig: Unable to connect to Integrity Server");
//            throw ce;
//        } catch (ApplicationConnectionException ace) {
//            System.out.println("IMConfig: Integrity Client unable to connect to Integrity Server");
//            throw ace;
//        } catch (APIException apiEx) {
//            System.out.println("IMConfig: Unable to initialize");
//            throw apiEx;
//        }
//    }
    public Response execute(Command cmd) throws APIException {
        long timestamp = System.currentTimeMillis();
        CmdRunner cmdRunner = apiSession.createCmdRunner();
        cmdRunner.setDefaultUsername(connection.getUser());
        cmdRunner.setDefaultPassword(connection.getPassword());
        cmdRunner.setDefaultHostname(connection.getHostname());
        cmdRunner.setDefaultPort(connection.getPort());
        // commandUsed = cmd.getCommandName();
        // OptionList ol = cmd.getOptionList();
        // for (int i=0; i<ol.size(); i++);
        //    Iterator o = ol.getOptions();
        //    o.

        Response response = cmdRunner.execute(cmd);
        cmdRunner.release();
        timestamp = System.currentTimeMillis() - timestamp;
        System.out.println("TestSession - API: " + response.getCommandString() + " [" + timestamp + "ms]");
        return response;
    }

    /**
     * Get WorkItem object for the named Item ID
     *
     * @param itemID
     * @return A workitem
     */
    public WorkItem getWorkItem(String itemID, String fields) {

        Command cmd = new Command(Command.IM, "issues");
        if (fields != null && !fields.isEmpty()) {
            cmd.addOption(new Option("fields", fields));
        }
        // if (itemID.contains(".")) {
        cmd.addOption(new Option("includeVersionedItems"));
        // }
        // Item #1
        cmd.addSelection(itemID);
        // WorkItem wi1 = null;
        try {
            Response response = this.execute(cmd);
            // CmdRunner cmdRunner = apiSession.createCmdRunner();
            // Response response = cmdRunner.execute(cmd);
            // cmdRunner.release();
            // return response.getWorkItem(itemID);
            while (response.getWorkItems().hasNext()) {
                return response.getWorkItems().next();
            }

            return response.getWorkItem(itemID);

        } catch (APIException ex) {
            // logger.exception(IntegrityAPI.class.getName(), MKSLogger.ERROR, 1, ex);
        }
        return null;
    }

    public List<WorkItem> getAllItemsByQuery(String queryName// , String fieldList
    )
            throws APIException {
        // if (fieldList == null || fieldList.isEmpty()) {
        // fieldList = getQueryFields(queryName);
        // }
        List<WorkItem> list = new ArrayList<>();
        try {
            Command cmd = new Command(Command.IM, "issues");
            cmd.addOption(new Option("query", queryName));
            // cmd.addOption(new Option("fields", fieldList));
            Response response = execute(cmd);

            WorkItemIterator wii = response.getWorkItems();
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                list.add(wi);
            }
        } catch (APIException ex) {
            // Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return list;
    }

    public void release() throws APIException, IOException {
        if (1 == 1) {
            if (apiSession != null) {
                apiSession.release();
            }
            if (integrationPoint != null) {
                integrationPoint.release();
            }
        }
    }
}
