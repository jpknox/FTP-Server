package com.jpknox.server.state;

import com.jpknox.server.authentication.LoginAuthentication;
import com.jpknox.server.session.ClientSession;

/**
 * Created by joaok on 03/10/2017.
 */
public class StateNeedPassword extends AbstractSessionState {

    private final LoginAuthentication loginAuthentication = new LoginAuthentication();

    private String username;        //User is in limbo, needs a password

    public StateNeedPassword(String username) {
        this.username = username;
    }

    @Override
    public String pass(ClientSession session, String password) {
        return loginService.login(session, this.username, password);
    }
}
