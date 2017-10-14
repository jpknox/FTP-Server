package com.jpknox.server.authentication;

import com.jpknox.server.session.ClientSession;
import com.jpknox.server.state.StateLoggedIn;
import com.jpknox.server.state.StateNeedPassword;
import com.jpknox.server.state.StateNotLoggedIn;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 03/10/2017.
 */
public class LoginService {

    LoginAuthentication loginAuthentication = new LoginAuthentication();

    public LoginService() {
    }

    public LoginService(LoginAuthentication loginAuthentication) {
        this.loginAuthentication = loginAuthentication;
    }

    public String login(ClientSession context, String username) {
        if (loginAuthentication.usernameExists(username)) {
            if(!loginAuthentication.hasPassword(username)) {
                context.setState(new StateLoggedIn());
                log("Username \"" + username + "\" logged in.");
                context.setClientName(username);    //TODO: what if the client changes, and fails to login?
                return "230 " + firstCharUpper(username) + " logged in, proceed.";
            } else {
                context.setState(new StateNeedPassword(username));
                return "331 User name okay, need password.";
            }
        } else {
            context.setState(new StateNotLoggedIn());
            return "530 Not logged in.";
        }
    }

    public String login(ClientSession context, String username, String password) {
        if (loginAuthentication.authenticate(username, password)) {
            context.setState(new StateLoggedIn());
            return "230 "+ firstCharUpper(username) + " logged in, proceed.";
        } else {
            context.setState(new StateNotLoggedIn());
            return "530 Not logged in.";
        }
    }

    public String firstCharUpper(String username) {
        return username.substring(0, 1).toUpperCase() + username.substring(1);
    }
}
