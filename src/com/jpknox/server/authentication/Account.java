package com.jpknox.server.authentication;

/**
 * Created by JoaoPaulo on 08-Oct-17.
 */
public class Account {

    private String username;
    private String password;
    private boolean hasPassword;

    public Account(String username, String password) {

        this.username = username;

        if (password.length() == 0 || password.equals(null)) {
            hasPassword = false;
        } else {
            hasPassword = true;
            this.password = password;
        }
    }

    public Account(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }
}
