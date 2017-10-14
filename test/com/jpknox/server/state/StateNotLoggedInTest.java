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
public class StateNotLoggedInTest {

    private StateNotLoggedIn stateNotLoggedIn;
    private ClientSession clientSession;
    private String username;
    private LoginService loginService;
    private FileManager fileManager;

    @Before
    public void setup() {
        stateNotLoggedIn = new StateNotLoggedIn();
        loginService = Mockito.mock(LoginService.class);
        fileManager = Mockito.mock(FileManager.class);
        when(fileManager.getCurrentDirectory()).thenReturn("/");
        stateNotLoggedIn.setLoginService(loginService);
        stateNotLoggedIn.setFileManager(fileManager);
        clientSession = Mockito.mock(ClientSession.class);
    }

    @After
    public void teardown() {
        stateNotLoggedIn = null;
        username = null;
        loginService = null;
        fileManager = null;
        clientSession = null;
    }

    @Test
    public void testUser() {
        username = "anonymous";
        stateNotLoggedIn.user(clientSession, username);
        verify(loginService).login(clientSession, username);
    }

    @Test
    public void testPass() {
        String password = "password";
        String message = stateNotLoggedIn.pass(clientSession, password);
        assertEquals("503 Bad sequence of commands.", message);
    }

    @Test
    public void testQuit() {
        String message = stateNotLoggedIn.quit(clientSession);
        assertEquals("221 Service closing control connection.", message);
    }

    @Test
    public void testPort() {
        String message = stateNotLoggedIn.port(clientSession, 9001);
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testType() {
        String message = stateNotLoggedIn.type(clientSession, "NON PRINT");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testMode() {
        String message = stateNotLoggedIn.mode(clientSession, "S");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStru() {
        String message = stateNotLoggedIn.stru(clientSession, "F");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testRetr() {
        String message = stateNotLoggedIn.retr(clientSession, "/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStor() {
        String message = stateNotLoggedIn.stor(clientSession, "/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testNoop() {
        String message = stateNotLoggedIn.noop(clientSession);
        assertEquals("200 Command okay.", message);
    }

    @Test
    public void testAuth() {
        String message = stateNotLoggedIn.auth(clientSession);
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testSyst() {
        String message = stateNotLoggedIn.syst(clientSession);
        assertEquals("215 " + FTPServerConfig.OPERATING_SYSTEM + ": " + FTPServerConfig.SERVER_NAME, message);
    }

    @Test
    public void testFeat() {
        String message = stateNotLoggedIn.feat(clientSession);
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testPwd() {
        String message = stateNotLoggedIn.pwd(clientSession);
        assertEquals("257 /", message);
        verify(fileManager, times(2)).getCurrentDirectory();
    }

}
