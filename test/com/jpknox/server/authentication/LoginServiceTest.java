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
public class LoginServiceTest {

    private LoginService loginService;
    private List<Account> initialUsers;

    @Before
    public void setup() {
        initialUsers = new ArrayList<Account>();
        initialUsers.add(new Account("user1", "pass1"));
        initialUsers.add(new Account("userNoPwd"));
        loginService = new LoginService(initialUsers);
    }

    @After
    public void teardown() {
        loginService = null;
        initialUsers = null;
    }

    @Test
    public void testUsernameExistsValidUserNoPassword() {
        boolean doesExist = loginService.usernameExists("user1");
        assertTrue(doesExist);
    }

    @Test
    public void testAuthenticateValidCredentials() {
        boolean doesExist = loginService.authenticate("user1", "pass1");
        assertTrue(doesExist);
    }

    @Test
    public void testAuthenticateValidUsernameWithInvalidPassword() {
        boolean loggedIn = loginService.authenticate("user1", "wrongPwd");
        assertFalse(loggedIn);
    }

    @Test
    public void testAuthenticateInvalidUsernameWithValidPassword() {
        boolean loggedIn = loginService.authenticate("wrongUsername", "pass1");
        assertFalse(loggedIn);
    }

    @Test
    public void testHasPasswordExistingUserHasPassword() {
        boolean hasPassword = loginService.needsPassword("user1");
        assertTrue(hasPassword);
    }

    @Test
    public void testHasPasswordNonexistentUserHasNoPassword() {
        boolean hasPassword = loginService.needsPassword("userNoPwd");
        assertFalse(hasPassword);
    }

}
