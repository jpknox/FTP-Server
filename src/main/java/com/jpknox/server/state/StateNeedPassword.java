package com.jpknox.server.state;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by João Paulo Knox on 03/10/2017.
 */
public class StateNeedPassword extends AbstractSessionState {

    public StateNeedPassword(ClientSession session) {
        super(session);
    }

    @Override
    public void pass(String password) {
        String username = session.getUsername();
        if (loginService.authenticate(username, password)) {
            session.setState(new StateLoggedIn(session));
            log(username + " logged in successfully.");
            session.getViewCommunicator().write(FTPResponseFactory.createResponse(230, username));
        } else {
            log(username + " has entered their password incorrectly.");
            session.resetUsername();
            session.setState(new StateNotLoggedIn(session));
            session.getViewCommunicator().write(FTPResponseFactory.createResponse(530));
        }
    }
}
