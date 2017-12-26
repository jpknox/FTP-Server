package com.jpknox.server.state;

import com.jpknox.server.storage.FileManager;
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
        loginService = Mockito.mock(LoginService.class);
        fileManager = Mockito.mock(FileManager.class);
        when(fileManager.getCurrentDirectory()).thenReturn("/");
        clientSession = Mockito.mock(ClientSession.class);
        stateLoggedIn = new StateLoggedIn(clientSession);
        stateLoggedIn.setLoginService(loginService);
        stateLoggedIn.setFileManager(fileManager);
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
        stateLoggedIn.user(username);
        verify(loginService).login(clientSession, username);
    }

    @Test
    public void testPass() {
        String password = "password";
        String message = stateLoggedIn.pass(password);
        assertEquals("503 Bad sequence of commands.", message);
    }

    @Test
    public void testQuit() {
        String message = stateLoggedIn.quit();
        assertEquals("221 Service closing control connection.", message);
    }

    @Test
    public void testPort() {
        String message = stateLoggedIn.port(9001);
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testType() {
        String message = stateLoggedIn.type("NON PRINT");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testMode() {
        String message = stateLoggedIn.mode("S");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStru() {
        String message = stateLoggedIn.stru("F");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testRetr() {
        String message = stateLoggedIn.retr("/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStor() {
        String message = stateLoggedIn.stor("/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testNoop() {
        String message = stateLoggedIn.noop();
        assertEquals("200 Command okay.", message);
    }

    @Test
    public void testAuth() {
        String message = stateLoggedIn.auth();
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testSyst() {
        String message = stateLoggedIn.syst();
        assertEquals("215 Jay's FTP Server V1.0: Windows 10", message);
    }

    @Test
    public void testFeat() {
        String message = stateLoggedIn.feat();
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testPwd() {
        String message = stateLoggedIn.pwd();
        assertEquals("257 /", message);
        verify(fileManager, times(1)).getCurrentDirectory();
    }

}
