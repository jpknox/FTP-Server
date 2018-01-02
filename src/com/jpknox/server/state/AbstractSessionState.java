package com.jpknox.server.state;

import com.jpknox.server.authentication.LoginService;
import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.response.ResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.utility.FTPServerConfig;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 23/12/2017.
 */
public abstract class AbstractSessionState implements SessionState {

    protected ResponseFactory responseFactory = new FTPResponseFactory();
    
    protected ClientSession session;

    protected FTPServerConfig config = new FTPServerConfig();

    protected LoginService loginService = new LoginService();

    public AbstractSessionState(ClientSession s) {
        this.session = s;
    }

    @Override
    public void user(String username) {
        if (loginService.illegalUsername(username)) {
            session.getViewCommunicator().write(responseFactory.createResponse(501));
            return;
        }
        if (loginService.usernameExists(username)) {
            session.setUsername(username);
            if(!loginService.needsPassword(username)) {
                log("Username \"" + username + "\" logged in.");
                session.setState(new StateLoggedIn(session));
                session.getViewCommunicator().write(responseFactory.createResponse(230, username));
            } else {
                session.setState(new StateNeedPassword(session));
                session.getViewCommunicator().write(responseFactory.createResponse(331));
            }
        } else {
            session.setState(new StateNotLoggedIn(session));
            session.getViewCommunicator().write(responseFactory.createResponse(530));
        }
    }

    @Override
    public void pass(String password) {
        session.getViewCommunicator().write(responseFactory.createResponse(503));
    }

    @Override
    public void quit() {
        session.getViewCommunicator().write(responseFactory.createResponse(221));
    }

    @Override
    public void port(int portToUse) {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }

    @Override
    public void type(String format) {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }
    
    @Override
    public void mode(String modeToUse) {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }

    @Override
    public void stru(String structureToUse) {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }

    @Override
    public void retr(String Url) {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }

    @Override
    public void stor(String Url) {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }

    @Override
    public void noop() {
        session.getViewCommunicator().write(responseFactory.createResponse(200));
    }

    @Override
    public void auth() {
        session.getViewCommunicator().write(responseFactory.createResponse(502));
    }

    @Override
    public void syst() {
        session.getViewCommunicator().write(
                responseFactory.createResponse(215, FTPServerConfig.SERVER_NAME, FTPServerConfig.OPERATING_SYSTEM));
    }

    @Override
    public void feat() {
        session.getViewCommunicator().write(responseFactory.createResponse(502));
    }

    @Override
    public void pwd() {
        session.getViewCommunicator().write(responseFactory.createResponse(257, session.getFileSystem().getCurrentDirectory()));
    }

    @Override
    public void pasv() {
        session.getViewCommunicator().write(responseFactory.createResponse(530)); 
    }

    @Override
    public void nlst() {
        session.getViewCommunicator().write(responseFactory.createResponse(530)); 
    }

    @Override
    public void list() {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }

    @Override
    public void cwd(String Url) {
        session.getViewCommunicator().write(responseFactory.createResponse(530));
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
}
