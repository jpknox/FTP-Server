package com.jpknox.server.session;

import com.jpknox.server.response.ClientViewCommunicator;
import com.jpknox.server.state.SessionState;
import com.jpknox.server.storage.DataStore;
import com.jpknox.server.transfer.DataConnectionController;

/**
 * Created by joaok on 29/01/2018.
 */
public interface  ClientSession {
    SessionState getState();

    void setState(SessionState nextState);

    String getUsername();

    void setUsername(String username);

    void resetUsername();

    DataConnectionController getDataConnectionController();

    DataStore getFileSystem();

    ClientViewCommunicator getViewCommunicator();

    void sendResponse(String text);
}
