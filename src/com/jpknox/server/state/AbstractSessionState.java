package com.jpknox.server.state;

import com.jpknox.server.authentication.LoginService;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.utility.FTPServerConfig;
import com.jpknox.server.utility.FileManager;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 23/12/2017.
 */
public abstract class AbstractSessionState implements SessionState {
    
    protected ClientSession session;

    protected FTPServerConfig config = new FTPServerConfig();

    protected FileManager fileManager = FileManager.getInstance();

    protected LoginService loginService = new LoginService();

    public AbstractSessionState(ClientSession s) {
        this.session = s;
    }

    @Override
    public String user(String username) {
        return loginService.login(session, username);
    }

    @Override
    public String pass(String password) {
        log("Client with name '" + session.getClientName() + "' attempted to enter password '" + password + "' out of sequence.");
        return "503 Bad sequence of commands.";
    }

    @Override
    public String quit() {
        return "221 Service closing control connection.";
    }

    @Override
    public String port(int portToUse) {
        return "530 Not logged in.";
    }

    @Override
    public String type(String format) {
        return "530 Not logged in.";
    }

    @Override
    public String mode(String modeToUse) {
        return "530 Not logged in.";
    }

    @Override
    public String stru(String structureToUse) {
        return "530 Not logged in.";
    }

    @Override
    public String retr(String pathToFile) {
        return "530 Not logged in.";
    }

    @Override
    public String stor(String pathToFile) {
        return "530 Not logged in.";
    }

    @Override
    public String noop() {
        return "200 Command okay.";
    }

    @Override
    public String auth() {
        log(session.getClientName() + ": 502 command not implemented.");
        return "502 Command not implemented.";
    }

    @Override
    public String syst() {
        log(session.getClientName() + ": 215 " + config.OPERATING_SYSTEM + ": " + config.SERVER_NAME);
        return "215 " + config.OPERATING_SYSTEM + ": " + config.SERVER_NAME;
    }

    @Override
    public String feat() {
        log(session.getClientName() + ": 502 FEAT command not implemented.");
        return "502 Command not implemented.";
    }

    @Override
    public String pwd() {
        log(session.getClientName() + ": 257 " + fileManager.getCurrentDirectory());
        return "257 " + fileManager.getCurrentDirectory();
    }

    @Override
    public String pasv() { return "530 Not logged in."; }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
}
