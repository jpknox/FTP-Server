package com.jpknox.server.state;

import com.jpknox.server.authentication.LoginService;
import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.response.ResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.utility.FTPServerConfig;
import com.jpknox.server.storage.FileManager;

/**
 * Created by joaok on 23/12/2017.
 */
public abstract class AbstractSessionState implements SessionState {

    private ResponseFactory responseFactory = new FTPResponseFactory();
    
    protected ClientSession session;

    protected FTPServerConfig config = new FTPServerConfig();

    protected FileManager fileManager = new FileManager();

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
        return responseFactory.createResponse(503);
    }

    @Override
    public String quit() {
        return responseFactory.createResponse(221);
    }

    @Override
    public String port(int portToUse) {
        return responseFactory.createResponse(530);
    }

    @Override
    public String type(String format) {
        return responseFactory.createResponse(530);
    }
    
    @Override
    public String mode(String modeToUse) {
        return responseFactory.createResponse(530);
    }

    @Override
    public String stru(String structureToUse) {
        return responseFactory.createResponse(530);
    }

    @Override
    public String retr(String pathToFile) {
        return responseFactory.createResponse(530);
    }

    @Override
    public String stor(String pathToFile) {
        return responseFactory.createResponse(530);
    }

    @Override
    public String noop() {
        return responseFactory.createResponse(200);
    }

    @Override
    public String auth() {
        return responseFactory.createResponse(502);
    }

    @Override
    public String syst() {
        return responseFactory.createResponse(215, FTPServerConfig.SERVER_NAME, FTPServerConfig.OPERATING_SYSTEM);
    }

    @Override
    public String feat() {
        return responseFactory.createResponse(502);
    }

    @Override
    public String pwd() {
        return responseFactory.createResponse(257, fileManager.getCurrentDirectory());
    }

    @Override
    public String pasv() { return responseFactory.createResponse(530); }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
}
