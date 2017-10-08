package com.jpknox.server.authentication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by JoaoPaulo on 08-Oct-17.
 */
public class LoginAuthenticationTest {

    private LoginAuthentication loginAuthentication;
    private List<Account> initialUsers;

    @Before
    public void setup() {
        initialUsers = new ArrayList<Account>();
        initialUsers.add(new Account("user1", "pass1"));
        initialUsers.add(new Account("userNoPwd"));
        loginAuthentication = new LoginAuthentication(initialUsers);
    }

    @After
    public void teardown() {
        loginAuthentication = null;
        initialUsers = null;
    }

    @Test
    public void testAuthenticateValidUserNoPassword() {
        boolean doesExist = loginAuthentication.authenticate("user1");
        assertTrue(doesExist);
    }

    @Test
    public void testAuthenticateValidCredentials() {
        boolean doesExist = loginAuthentication.authenticate("user1", "pass1");
        assertTrue(doesExist);
    }

    @Test
    public void testAuthenticateValidUsernameWithInvalidPassword() {
        boolean loggedIn = loginAuthentication.authenticate("user1", "wrongPwd");
        assertFalse(loggedIn);
    }

    @Test
    public void testAuthenticateInvalidUsernameWithValidPassword() {
        boolean loggedIn = loginAuthentication.authenticate("wrongUsername", "pass1");
        assertFalse(loggedIn);
    }

    @Test
    public void testUserHasPasswordTrue() {
        boolean hasPassword = loginAuthentication.hasPassword("user1");
        assertTrue(hasPassword);
    }

    @Test
    public void testUserHasPasswordFalse() {
        boolean hasPassword = loginAuthentication.hasPassword("userNoPwd");
        assertFalse(hasPassword);
    }

}
