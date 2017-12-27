package com.jpknox.server.state;

import com.jpknox.server.session.ClientSession;

/**
 * Created by joaok on 03/10/2017.
 */
public class StateNeedPassword extends AbstractSessionState {

    private String username;        //User is in limbo, needs a password

    public StateNeedPassword(ClientSession session, String username) {
        super(session);
        this.username = username;
    }

    @Override
    public void pass(String password) {
        loginService.login(session, this.username, password);
    }
}
