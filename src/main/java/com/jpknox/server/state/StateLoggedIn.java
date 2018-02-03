package com.jpknox.server.state;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.storage.DataStore;
import com.jpknox.server.storage.internaltransfer.FileQueue;
import com.jpknox.server.transfer.DataConnectionController;

import java.io.File;

/**
 * Created by joaok on 24/09/2017.
 */
public class StateLoggedIn extends AbstractSessionState {

    public StateLoggedIn(ClientSession session) {
        super(session);
    }

    private boolean isDataConnectionListening() {
        if (!session.getDataConnectionController().isListening()) {
            session.getViewCommunicator().write(FTPResponseFactory.createResponse(425));
            return false;
        }
        return true;
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
        if (!isDataConnectionListening()) return;
        //session.getDataConnectionController().send(session.getFileSystem().getNameList("/"));
    }

    @Override
    public void list() {
        if (!isDataConnectionListening()) return;
        String data = session.getFileSystem().getFileList();
        session.getDataConnectionController().send(data);
    }

    @Override
    public void stor(String Url) {
        if (!isDataConnectionListening()) return;
        System.out.println("State logged in has entered 'stor'");
        FileQueue fileQueue = session.getFileSystem().store(Url);
        session.getDataConnectionController().receive(fileQueue, Url);
        System.out.println("State logged is leaving left 'stor'");
    }

    @Override
    public void pwd() {
        String currentDirectory = session.getFileSystem().getCurrentDirectory();
        session.getViewCommunicator().write(FTPResponseFactory.createResponse(257, currentDirectory));
    }

    @Override
    public void cwd(String Url) {
        DataStore fileSystem = session.getFileSystem();
        if (!fileSystem.validUrl(Url)) {
            session.getViewCommunicator().write(FTPResponseFactory.createResponse(501));
        }

        session.getFileSystem().changeWorkingDirectory(Url);
    }

    @Override
    public void get(String Url) {
        DataStore dataStore = session.getFileSystem();
        DataConnectionController dataController = session.getDataConnectionController();

        //TODO: Response for an attempt to get a file which does not exist.
        if (!dataStore.exists(Url)) return;

        //TODO: Responses for 'get' command without a prior-listening data connection having already been established.
        if (!isDataConnectionListening()) return;

        File file = dataStore.get(Url);

        dataController.send(file);
        //TODO: Give a timely response when the data transfer completes successfully.
    }
}
