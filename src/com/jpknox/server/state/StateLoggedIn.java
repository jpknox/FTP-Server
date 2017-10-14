package com.jpknox.server.state;

import com.jpknox.server.FTPServerConfig;
import com.jpknox.server.FileManager;
import com.jpknox.server.authentication.LoginService;
import com.jpknox.server.session.ClientSession;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 24/09/2017.
 */
public class StateLoggedIn implements SessionState {

    private FTPServerConfig config = new FTPServerConfig();

    private FileManager fileManager = FileManager.getInstance();

    private LoginService loginService = new LoginService();

    public StateLoggedIn() {
    }

    @Override
    public String user(ClientSession session, String username) {
        return loginService.login(session, username);
    }

    @Override
    public String pass(ClientSession session, String password) {
        return "503 Bad sequence of commands.";
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
        log(session.getClientName() + ": 502 - command not implemented.");
        return "502 AUTH command not implemented.";
    }

    @Override
    public String syst(ClientSession session) {
        log(session.getClientName() + ": 215 " + config.OPERATING_SYSTEM + ": " + config.SERVER_NAME);
        return "215 " + config.OPERATING_SYSTEM + ": " + config.SERVER_NAME;
    }

    @Override
    public String feat(ClientSession session) {
        log(session.getClientName() + ": 502 FEAT command not implemented.");
        return "502 FEAT command not implemented.";
    }

    @Override
    public String pwd(ClientSession session) {
        log(session.getClientName() + ": 257 " + fileManager.getCurrentDirectory());
        return "257 " + fileManager.getCurrentDirectory();
    }

}
