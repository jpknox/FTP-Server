package com.jpknox.server.state;

import com.jpknox.server.FTPServer;
import com.jpknox.server.authentication.LoginAuthentication;
import com.jpknox.server.authentication.LoginService;

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
    public int user(FTPServer context, String username) {
        loginService.login(context, username);
        return 0;
    }

    @Override
    public int pass(FTPServer context, String password) {
        loginService.login(context, this.username, password);
        return 0;
    }

    @Override
    public int quit(FTPServer context) {
        return 0;
    }

    @Override
    public int port(FTPServer context, int portToUse) {
        return 0;
    }

    @Override
    public int type(FTPServer context, String format) {
        return 0;
    }

    @Override
    public int mode(FTPServer context, String modeToUse) {
        return 0;
    }

    @Override
    public int stru(FTPServer context, String structureToUse) {
        return 0;
    }

    @Override
    public int retr(FTPServer context, String pathToFile) {
        return 0;
    }

    @Override
    public int stor(FTPServer context, String pathToFile) {
        return 0;
    }

    @Override
    public int noop(FTPServer context) {
        return 0;
    }

    @Override
    public int auth(FTPServer context) {
        return 0;
    }

    @Override
    public int syst(FTPServer context) {
        return 0;
    }

    @Override
    public int feat(FTPServer context) {
        return 0;
    }

    @Override
    public int pwd(FTPServer context) {
        return 0;
    }
}
