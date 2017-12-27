package com.jpknox.server.state;

import com.jpknox.server.session.ClientSession;

/**
 * Created by joaok on 24/09/2017.
 */
public class StateLoggedIn extends AbstractSessionState {

    public StateLoggedIn(ClientSession session) {
        super(session);
    }

    @Override
    public String pasv() {
        //"TODO: Pick port and start listening to it"
        // (h1,h2,h3,h4,p1,p2)
        int[] encodedDataPort = session.getDataTransferController().listen();
        return "227 Entering Passive Mode (" + config.IP_FIRST_OCTET + "," +
                                             config.IP_SECOND_OCTET + "," +
                                             config.IP_THIRD_OCTET + "," +
                                             config.IP_FOURTH_OCTET + "," +
                                             encodedDataPort[0] + "," +
                                             encodedDataPort[1] + ")";
    }

    public String nlst() {
        return "";
    }

    @Override
    public String pwd() {
        String currentDirectory = fileManager.getCurrentDirectory();
        return responseFactory.createResponse(257, currentDirectory);
    }
}
