package com.jpknox.server.authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaok on 24/09/2017.
 */
public class LoginAuthentication {

    List<Account> accounts;

    public LoginAuthentication() {
        this.accounts = new ArrayList<Account>();
        this.accounts.add(new Account("user1", "pass1"));
        this.accounts.add(new Account("anonymous"));
        this.accounts.add(new Account("anon"));
    }

    public LoginAuthentication(List<Account> initialUsers) {
        accounts = initialUsers;
    }

    public boolean usernameExists(String username) {
        for (Account accnt : accounts) {
            if (accnt.getUsername().equals(username)) return true;
        }   return false;
    }

    public boolean hasPassword(String username) {
        if (username.length() == 0 || username.equals(null)) return false;
        for (Account accnt : accounts) {
            if (accnt.getUsername().equals(username)) return accnt.hasPassword();
        }   return false;
    }

    public boolean authenticate(String username, String password) {
        if (username.length() == 0 || username.equals(null) ||
            password.length() == 0 || password.equals(null)) return false;
        for (Account accnt : accounts) {
            if (accnt.getUsername().equals(username) && accnt.getPassword().equals(password)) return true;
            //TODO: Override Account's equals method
        }   return false;
    }
}
