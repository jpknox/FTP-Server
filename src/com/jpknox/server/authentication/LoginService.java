package com.jpknox.server.authentication;

import com.jpknox.server.FTPServer;
import com.jpknox.server.state.StateLoggedIn;
import com.jpknox.server.state.StateNeedPassword;
import com.jpknox.server.state.StateNotLoggedIn;

/**
 * Created by joaok on 03/10/2017.
 */
public class LoginService {

    LoginAuthentication loginAuthentication = new LoginAuthentication();

    public int login(FTPServer context, String username) {
        if (loginAuthentication.authenticate(username)) {
            if(!loginAuthentication.hasPassword(username)) {
                context.setState(new StateLoggedIn());
                context.log("Username \"" + username + "\" logged in.");
                context.setClientName(username);    //TODO: what if the client changes, and fails to login?
                context.sendToClient("230 " + firstCharUpper(username) + " logged in, proceed.");
            } else {
                context.setState(new StateNeedPassword(username));
                context.sendToClient("331 User name okay, need password.");
            }
        } else {
            context.setState(new StateNotLoggedIn());
            context.sendToClient("530 Not logged in.");
        }
        return 0;
    }

    public int login(FTPServer context, String username, String password) {
        if (loginAuthentication.authenticate(username, password)) {
            context.setState(new StateLoggedIn());
            context.sendToClient("230 "+ firstCharUpper(username) + " logged in, proceed.");
        } else {
            context.setState(new StateNotLoggedIn());
            context.sendToClient("530 Not logged in.");
        }
        return 0;
    }

    public String firstCharUpper(String username) {
        return username.substring(0, 1).toUpperCase() + username.substring(1);
    }
}
