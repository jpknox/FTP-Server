package com.jpknox.server.state;

import com.jpknox.server.response.ClientViewCommunicator;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.storage.internaltransfer.FileQueue;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by joaok on 24/09/2017.
 */
public class StateLoggedIn extends AbstractSessionState {

    public StateLoggedIn(ClientSession session) {
        super(session);
    }

    @Override
    public void pasv() {
        //"TODO: Pick port and start listening to it"
        // (h1,h2,h3,h4,p1,p2)
        int[] encodedDataPort = session.getDataConnectionController().createDataConnectionListener();
        session.getViewCommunicator().write("227 Entering Passive Mode (" + config.IP_FIRST_OCTET + "," +
                                             config.IP_SECOND_OCTET + "," +
                                             config.IP_THIRD_OCTET + "," +
                                             config.IP_FOURTH_OCTET + "," +
                                             encodedDataPort[0] + "," +
                                             encodedDataPort[1] + ")");
    }

    public void nlst() {
        if (!checkIfListening()) return;
        session.getDataConnectionController().send(session.getFileSystem().getNameList("/"));
    }

    @Override
    public void list() {
        if (!checkIfListening()) return;
        String data = session.getFileSystem().getFileList("Dummy URL");
        session.getDataConnectionController().send(data);
    }

    @Override
    public void stor(String Url) {
        if (!checkIfListening()) return;
        System.out.println("State logged in has entered 'stor'");
        FileQueue fileQueue = session.getFileSystem().store(Url);
        session.getDataConnectionController().receive(fileQueue, Url);
        System.out.println("State logged is leaving left 'stor'");
    }

    @Override
    public void pwd() {
        String currentDirectory = session.getFileSystem().getCurrentDirectory();
        session.getViewCommunicator().write(responseFactory.createResponse(257, currentDirectory));
    }

    @Override
    public void cwd(String Url) {
        session.getFileSystem().changeWorkingDirectory(Url);
    }

    private boolean checkIfListening() {
        if (!session.getDataConnectionController().isListening()) {
            session.getViewCommunicator().write(responseFactory.createResponse(425));
            return false;
        }
        return true;
    }
}
