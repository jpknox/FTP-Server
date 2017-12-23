package com.jpknox.server.session;

import com.jpknox.server.state.SessionState;
import com.jpknox.server.state.StateNotLoggedIn;
import com.jpknox.server.transfer.DataTransferController;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ClientSession {

    private SessionState context;
    private String clientName = "client";
    private DataTransferController dataTransferController = new DataTransferController();

    public ClientSession() {
        this.context = new StateNotLoggedIn();
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

    public DataTransferController getDataTransferController() { return dataTransferController; }
}
