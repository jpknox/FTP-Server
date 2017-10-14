package com.jpknox.server.state;

import com.jpknox.server.authentication.LoginAuthentication;
import com.jpknox.server.authentication.LoginService;
import com.jpknox.server.session.ClientSession;

/**
 * Created by joaok on 03/10/2017.
 */
public class StateNeedPassword implements SessionState {

    private final LoginAuthentication loginAuthentication = new LoginAuthentication();

    private final LoginService loginService = new LoginService();

    String username;        //User is in limbo, needs a password

    public StateNeedPassword(String username) {
        this.username = username;
    }

    @Override
    public String user(ClientSession session, String username) {
        return loginService.login(session, username);
    }

    @Override
    public String pass(ClientSession session, String password) {
        return loginService.login(session, this.username, password);
    }

    @Override
    public String quit(ClientSession session) {
        return "Not Implemented";
    }

    @Override
    public String port(ClientSession session, int portToUse) {
        return "Not Implemented";
    }

    @Override
    public String type(ClientSession session, String format) {
        return "Not Implemented";
    }

    @Override
    public String mode(ClientSession session, String modeToUse) {
        return "Not Implemented";
    }

    @Override
    public String stru(ClientSession session, String structureToUse) {
        return "Not Implemented";
    }

    @Override
    public String retr(ClientSession session, String pathToFile) {
        return "Not Implemented";
    }

    @Override
    public String stor(ClientSession session, String pathToFile) {
        return "Not Implemented";
    }

    @Override
    public String noop(ClientSession session) {
        return "Not Implemented";
    }

    @Override
    public String auth(ClientSession session) {
        return "Not Implemented";
    }

    @Override
    public String syst(ClientSession session) {
        return "Not Implemented";
    }

    @Override
    public String feat(ClientSession session) {
        return "Not Implemented";
    }

    @Override
    public String pwd(ClientSession session) {
        return "Not Implemented";
    }
}
