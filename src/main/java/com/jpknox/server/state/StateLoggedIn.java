package com.jpknox.server.state;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.storage.DataStore;
import com.jpknox.server.storage.internaltransfer.FileQueue;
import com.jpknox.server.transfer.DataConnectionController;

import java.io.File;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by João Paulo Knox on 24/09/2017.
 */
public class StateLoggedIn extends AbstractSessionState {
    public StateLoggedIn(ClientSession session) {
        super(session);
    }

    private boolean isDataConnectionListening() {
        if (!session.getDataConnectionController().isListening()) {
            getClientCommunicator().write(FTPResponseFactory.createResponse(425));
            return false;
        }
        return true;
    }

    @Override
    public void pasv() {
        int[] encodedDataPort = session.getDataConnectionController().createDataConnectionListener();
        getClientCommunicator().write("227 Entering Passive Mode (" +
                                                config.IP_FIRST_OCTET + "," +
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
    public void retr(String Url) {
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

    @Override
    public void dele(String filePath) {
        DataStore dataStore = session.getFileSystem();
        if (!dataStore.exists(filePath)) {
            getClientCommunicator().write(FTPResponseFactory.createResponse(550));
            //TODO: Add more detail to response.
        }
        boolean deleted = dataStore.delete(filePath);
        if (deleted) {
            getClientCommunicator().write(FTPResponseFactory.createResponse(250));
        } else {
            getClientCommunicator().write(FTPResponseFactory.createResponse(550));
            //TODO: Add more detail to response.
        }
    }

    @Override
    public void stor(String filePath) {
        if (!isDataConnectionListening()) return;
        DataStore dataStore = session.getFileSystem();
        if (dataStore.exists(filePath)) {
            getClientCommunicator().write(FTPResponseFactory.createResponse(550));
            return;
            //TODO: Add more detail to response.
        }
        log("Current directory, within 'stor' method is '"+dataStore.getCurrentDirectory()+"'");
        //TODO: Get destination folder from filePath
        //TODO: Refactor
        FileQueue fileQueue = session.getFileSystem().store(filePath);
        session.getDataConnectionController().receive(fileQueue, filePath);
    }

    @Override
    public void rmd(String pathToFolder) {
        dele(pathToFolder);
    }

    @Override
    public void pwd() {
        String currentDirectory = session.getFileSystem().getCurrentDirectory();
        getClientCommunicator().write(FTPResponseFactory.createResponse(257, currentDirectory));
        log(String.format("Sent the current directory to the client '%s'", currentDirectory));
    }

    @Override
    public void cwd(String path) {
        DataStore fileSystem = session.getFileSystem();
        if (!fileSystem.validUrl(path)) {
            getClientCommunicator().write(FTPResponseFactory.createResponse(501));
        }

        session.getFileSystem().changeWorkingDirectory(path);
    }
}
