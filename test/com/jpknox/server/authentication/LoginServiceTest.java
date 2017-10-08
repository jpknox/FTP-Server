package com.jpknox.server.authentication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 08-Oct-17.
 */
public class LoginServiceTest {

    private LoginService loginService;

    @Before
    public void setup() {
        loginService = new LoginService();
    }

    @After
    public void teardown() {
        loginService = null;
    }

    @Test
    public void testCapitaliseFirstCharOfUsername() {
        String username = "username";
        assertEquals("Username", loginService.firstCharUpper(username));
    }

}
