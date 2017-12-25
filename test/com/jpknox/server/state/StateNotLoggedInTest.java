package com.jpknox.server.state;

import com.jpknox.server.utility.FTPServerConfig;
import com.jpknox.server.utility.FileManager;
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
    private LoginService loginService;
    private FileManager fileManager;

    @Before
    public void setup() {
        loginService = Mockito.mock(LoginService.class);
        fileManager = Mockito.mock(FileManager.class);
        when(fileManager.getCurrentDirectory()).thenReturn("/");
        clientSession = Mockito.mock(ClientSession.class);
        stateNotLoggedIn = new StateNotLoggedIn(clientSession);
        stateNotLoggedIn.setLoginService(loginService);
        stateNotLoggedIn.setFileManager(fileManager);
    }

    @After
    public void teardown() {
        stateNotLoggedIn = null;
        loginService = null;
        fileManager = null;
        clientSession = null;
    }

    @Test
    public void testUser() {
        String username = "username";
        stateNotLoggedIn.user(username);
        verify(loginService).login(clientSession, username);
    }

    @Test
    public void testPass() {
        String password = "password";
        String message = stateNotLoggedIn.pass(password);
        assertEquals("503 Bad sequence of commands.", message);
    }

    @Test
    public void testQuit() {
        String message = stateNotLoggedIn.quit();
        assertEquals("221 Service closing control connection.", message);
    }

    @Test
    public void testPort() {
        String message = stateNotLoggedIn.port(9001);
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testType() {
        String message = stateNotLoggedIn.type("NON PRINT");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testMode() {
        String message = stateNotLoggedIn.mode("S");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStru() {
        String message = stateNotLoggedIn.stru("F");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testRetr() {
        String message = stateNotLoggedIn.retr("/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStor() {
        String message = stateNotLoggedIn.stor("/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testNoop() {
        String message = stateNotLoggedIn.noop();
        assertEquals("200 Command okay.", message);
    }

    @Test
    public void testAuth() {
        String message = stateNotLoggedIn.auth();
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testSyst() {
        String message = stateNotLoggedIn.syst();
        assertEquals("215 Jay's FTP Server V1.0: Windows 10", message);
    }

    @Test
    public void testFeat() {
        String message = stateNotLoggedIn.feat();
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testPwd() {
        String message = stateNotLoggedIn.pwd();
        assertEquals("257 /", message);
        verify(fileManager, times(1)).getCurrentDirectory();
    }

}
