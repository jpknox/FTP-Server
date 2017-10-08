package com.jpknox.server.authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaok on 24/09/2017.
 */
public class LoginAuthentication {

    List<String> users;
    List<String> passwords;

    public LoginAuthentication() {
        this.users = new ArrayList<String>();
        this.users.add("user1");
        this.users.add("anonymous");
        passwords = new ArrayList<String>();
        passwords.add("pass1");
    }

    public LoginAuthentication(List<Account> initialUsers) {
    }

    public boolean authenticate(String username) {
        return users.contains(username);
    }

    public boolean hasPassword(String username) {
        return true;                                                    //TODO: Add password detection
    }

    public boolean authenticate(String username, String password) {
        return users.contains(username) && passwords.contains(password); //TODO: add password validation
    }
}
