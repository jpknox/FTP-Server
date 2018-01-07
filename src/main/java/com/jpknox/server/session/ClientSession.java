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
public class ClientSession {

    private static final String DEFAULT_USERNAME = "client";
    private final DataConnectionController dataConnectionController = new DataConnectionController(this);
    private final DataStore fileSystem = new FTPLocalFileDataStore(this);
    private final ClientViewCommunicator viewCommunicator;

    private SessionState context;
    private String username = DEFAULT_USERNAME;

    public ClientSession(ClientViewCommunicator viewCommunicator) {
        this.viewCommunicator = viewCommunicator;
        this.context = new StateNotLoggedIn(this);
    }

    public SessionState getState() {
        return this.context;
    }

    public void setState(SessionState nextState) {
        log("Switching state to " + nextState.getClass().getSimpleName());
        this.context = nextState;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void resetUsername() {
        this.username = DEFAULT_USERNAME;
    }

    public DataConnectionController getDataConnectionController() { return dataConnectionController; }

    public DataStore getFileSystem() {
        return fileSystem;
    }

    public ClientViewCommunicator getViewCommunicator() {
        return viewCommunicator;
    }
}
