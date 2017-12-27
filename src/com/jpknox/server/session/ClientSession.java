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

    private final DataConnectionController dataConnectionController = new DataConnectionController(this);
    private final DataStore fileSystem = new FTPLocalFileDataStore(this);
    private final ClientViewCommunicator viewCommunicator;

    private SessionState context;
    private String clientName = "client";

    public ClientSession(ClientViewCommunicator viewCommunicator) {
        this.viewCommunicator = viewCommunicator;
        this.context = new StateNotLoggedIn(this);
    }

    public SessionState getState() {
        return this.context;
    }

    public int setState(SessionState nextState) {
        log("Switching state to " + nextState.getClass().getSimpleName());
        this.context = nextState;
        return 0;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public DataConnectionController getDataConnectionController() { return dataConnectionController; }

    public DataStore getFileSystem() {
        return fileSystem;
    }

    public ClientViewCommunicator getViewCommunicator() {
        return viewCommunicator;
    }
}
