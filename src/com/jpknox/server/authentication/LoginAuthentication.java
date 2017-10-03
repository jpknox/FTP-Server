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
        users = new ArrayList<String>();
        users.add("user1");
        users.add("anonymous");
        passwords = new ArrayList<String>();
        passwords.add("pass1");
    }

    public boolean doesExist(String username) {
        return users.contains(username);
    }

    public boolean hasPassword(String username) {
        return true;
    }

    public boolean doesExist(String username, String password) {
        return users.contains(username) && passwords.contains(password); //TODO: add password validation
    }
}
