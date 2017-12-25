package com.jpknox.server.authentication;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.response.ResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.state.StateLoggedIn;
import com.jpknox.server.state.StateNeedPassword;
import com.jpknox.server.state.StateNotLoggedIn;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 03/10/2017.
 */
public class LoginService {

    private LoginAuthentication loginAuthentication = new LoginAuthentication();

    private ResponseFactory responseFactory = new FTPResponseFactory();

    public LoginService() {
    }

    public LoginService(LoginAuthentication loginAuthentication) {
        this.loginAuthentication = loginAuthentication;
    }

    /**
     * Orchestrates the client's login attempt by forwarding their {@code username}
     * towards the authenticator.
     * @param session
     * @param username
     * @return
     */
    public String login(ClientSession session, String username) {
        if (username.length() == 0 || username.equals(null))
            return "501 Syntax error in parameters or arguments.";

        if (loginAuthentication.usernameExists(username)) {
            session.setClientName(username);    //TODO: what if the client changes, and fails to login?
            if(!loginAuthentication.hasPassword(username)) {
                session.setState(new StateLoggedIn(session));
                log("Username \"" + username + "\" logged in.");
                return responseFactory.createResponse(230, username);
            } else {
                session.setState(new StateNeedPassword(session, username));
                return responseFactory.createResponse(331);
            }
        } else {
            session.setState(new StateNotLoggedIn(session));
            return responseFactory.createResponse(530);
        }
    }

    /**
     * Orchestrates the client's login attempt by forwarding their {@code username}
     * and {&code password} to the authenticator.
     * @param session
     * @param username
     * @param password
     * @return
     */
    public String login(ClientSession session, String username, String password) {
        if (loginAuthentication.authenticate(username, password)) {
            session.setState(new StateLoggedIn(session));
            log(username + " logged in successfully.");
            return responseFactory.createResponse(230, username);
        } else {
            log(username + " has entered their password incorrectly.");
            session.setState(new StateNotLoggedIn(session));
            return responseFactory.createResponse(530);
        }
    }

    public String firstCharUpper(String username) {
        return username.substring(0, 1).toUpperCase() + username.substring(1);
    }
}
