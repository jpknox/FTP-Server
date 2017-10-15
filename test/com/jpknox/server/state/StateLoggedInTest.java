package com.jpknox.server.state;

import com.jpknox.server.FTPServerConfig;
import com.jpknox.server.FileManager;
import com.jpknox.server.authentication.LoginService;
import com.jpknox.server.session.ClientSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class StateLoggedInTest {

    private StateLoggedIn stateLoggedIn;
    private ClientSession clientSession;
    private LoginService loginService;
    private FileManager fileManager;

    @Before
    public void setup() {
        stateLoggedIn = new StateLoggedIn();
        loginService = Mockito.mock(LoginService.class);
        fileManager = Mockito.mock(FileManager.class);
        when(fileManager.getCurrentDirectory()).thenReturn("/");
        stateLoggedIn.setLoginService(loginService);
        stateLoggedIn.setFileManager(fileManager);
        clientSession = Mockito.mock(ClientSession.class);
    }

    @After
    public void teardown() {
        stateLoggedIn = null;
        loginService = null;
        fileManager = null;
        clientSession = null;
    }

    @Test
    public void testUser() {
        String username = "username";
        stateLoggedIn.user(clientSession, username);
        verify(loginService).login(clientSession, username);
    }

    @Test
    public void testPass() {
        String password = "password";
        String message = stateLoggedIn.pass(clientSession, password);
        assertEquals("503 Bad sequence of commands.", message);
    }

    @Test
    public void testQuit() {
        String message = stateLoggedIn.quit(clientSession);
        assertEquals("221 Service closing control connection.", message);
    }

    @Test
    public void testPort() {
        String message = stateLoggedIn.port(clientSession, 9001);
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testType() {
        String message = stateLoggedIn.type(clientSession, "NON PRINT");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testMode() {
        String message = stateLoggedIn.mode(clientSession, "S");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStru() {
        String message = stateLoggedIn.stru(clientSession, "F");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testRetr() {
        String message = stateLoggedIn.retr(clientSession, "/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStor() {
        String message = stateLoggedIn.stor(clientSession, "/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testNoop() {
        String message = stateLoggedIn.noop(clientSession);
        assertEquals("200 Command okay.", message);
    }

    @Test
    public void testAuth() {
        String message = stateLoggedIn.auth(clientSession);
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testSyst() {
        String message = stateLoggedIn.syst(clientSession);
        assertEquals("215 " + FTPServerConfig.OPERATING_SYSTEM + ": " + FTPServerConfig.SERVER_NAME, message);
    }

    @Test
    public void testFeat() {
        String message = stateLoggedIn.feat(clientSession);
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testPwd() {
        String message = stateLoggedIn.pwd(clientSession);
        assertEquals("257 /", message);
        verify(fileManager, times(2)).getCurrentDirectory();
    }

}
