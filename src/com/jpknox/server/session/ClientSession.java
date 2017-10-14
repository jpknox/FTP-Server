package com.jpknox.server.session;

import com.jpknox.server.state.SessionState;
import com.jpknox.server.state.StateNotLoggedIn;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ClientSession {

    private SessionState context;
    private String clientName = "client";

    public ClientSession() {
        this.context = new StateNotLoggedIn();
    }

    public SessionState getState() {
        return this.context;
    }

    public int setState(SessionState nextState) {
        this.context = nextState;
        return 0;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
