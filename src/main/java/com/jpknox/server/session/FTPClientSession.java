package com.jpknox.server.session;

import com.jpknox.server.response.ClientViewCommunicator;
import com.jpknox.server.state.SessionState;
import com.jpknox.server.state.StateNotLoggedIn;
import com.jpknox.server.storage.DataStore;
import com.jpknox.server.storage.FTPLocalFileDataStore;
import com.jpknox.server.transfer.DataConnectionController;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class FTPClientSession implements ClientSession {

    private static final String DEFAULT_USERNAME = "client";
    private final DataConnectionController dataConnectionController = new DataConnectionController(this);
    private final DataStore fileSystem = new FTPLocalFileDataStore(this);
    private final ClientViewCommunicator viewCommunicator;

    private SessionState context;
    private String username = DEFAULT_USERNAME;

    public FTPClientSession(ClientViewCommunicator viewCommunicator) {
        this.viewCommunicator = viewCommunicator;
        this.context = new StateNotLoggedIn(this);
    }

    @Override
    public SessionState getState() {
        return this.context;
    }

    @Override
    public void setState(SessionState nextState) {
        log("Switching state to " + nextState.getClass().getSimpleName());
        this.context = nextState;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void resetUsername() {
        this.username = DEFAULT_USERNAME;
    }

    @Override
    public DataConnectionController getDataConnectionController() { return dataConnectionController; }

    @Override
    public DataStore getFileSystem() {
        return fileSystem;
    }

    @Override
    public ClientViewCommunicator getViewCommunicator() {
        return viewCommunicator;
    }

    @Override
    public void sendResponse(final String text) {
        getViewCommunicator().write(text);
    }


}
