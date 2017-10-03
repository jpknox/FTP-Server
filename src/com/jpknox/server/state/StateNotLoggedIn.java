package com.jpknox.server.state;

import com.jpknox.server.FTPServer;
import com.jpknox.server.FTPServerConfig;
import com.jpknox.server.FileManager;
import com.jpknox.server.authentication.LoginAuthentication;
import com.jpknox.server.authentication.LoginService;

/**
 * Created by joaok on 24/09/2017.
 */
public class StateNotLoggedIn implements SessionState {

    private FTPServerConfig config = new FTPServerConfig();

    private FileManager fileManager = FileManager.getInstance();

    private LoginService loginService = new LoginService();

    @Override 
    public int user(FTPServer context, String username) {
        loginService.login(context, username);
        return 0;
    }

    @Override
    public int pass(FTPServer context, String password) {
        context.sendToClient("503 Bad sequence of commands.");
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
        context.sendToClient("502 AUTH command not implemented.");
        context.log(context.getClientName() + ": 502 command not implemented.");
        return 0;
    }

    @Override
    public int syst(FTPServer context) {
        context.sendToClient("215 " + config.OPERATING_SYSTEM + ": " + config.SERVER_NAME);
        context.log(context.getClientName() + ": 215 " + config.OPERATING_SYSTEM + ": " + config.SERVER_NAME);
        return 0;
    }

    @Override
    public int feat(FTPServer context) {
        context.sendToClient("502 FEAT command not implemented.");
        context.log(context.getClientName() + ": 502 FEAT command not implemented.");
        return 0;
    }

    @Override
    public int pwd(FTPServer context) {
        context.sendToClient("257 " + fileManager.getCurrentDirectory());
        context.log(context.getClientName() + ": 257 " + fileManager.getCurrentDirectory());
        return 0;
    }
}
