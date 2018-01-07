package com.jpknox.server.authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaok on 24/09/2017.
 */
public class LoginService {

    List<Account> accounts;

    public LoginService() {
        this.accounts = new ArrayList<Account>();
        this.accounts.add(new Account("user1", "pass1"));
        this.accounts.add(new Account("anonymous"));
        this.accounts.add(new Account("anon"));
    }

    public LoginService(List<Account> initialUsers) {
        accounts = initialUsers;
    }

    public boolean usernameExists(String username) {
        for (Account accnt : accounts) {
            if (accnt.getUsername().equals(username)) return true;
        }
        return false;
    }

    public boolean needsPassword(String username) {
        if (username.length() == 0 || username.equals(null)) return false;
        for (Account accnt : accounts) {
            if (accnt.getUsername().equals(username)) return accnt.hasPassword();
        }
        return false;
    }

    public boolean authenticate(String username, String password) {
        if (illegalCredentials(username, password)) return false;
        for (Account acct : accounts) {
            if (acct.getUsername().equals(username) && acct.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean illegalCredentials(String username, String password) {
        return illegalUsername(username) || illegalPassword(password);
    }

    public boolean illegalUsername(String username) {
        return username.length() == 0 || username.equals(null);
    }

    public boolean illegalPassword(String password) {
        return password.length() == 0 || password.equals(null);
    }
}
