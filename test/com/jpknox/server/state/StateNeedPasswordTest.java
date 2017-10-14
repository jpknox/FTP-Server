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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class StateNeedPasswordTest {

    private StateNeedPassword stateNeedPassword;
    private ClientSession clientSession;
    private String username;
    private LoginService loginService;
    private FileManager fileManager;

    @Before
    public void setup() {
        username = "username1";
        stateNeedPassword = new StateNeedPassword(username);
        loginService = Mockito.mock(LoginService.class);
        fileManager = Mockito.mock(FileManager.class);
        when(fileManager.getCurrentDirectory()).thenReturn("/");
        stateNeedPassword.setLoginService(loginService);
        stateNeedPassword.setFileManager(fileManager);
        clientSession = Mockito.mock(ClientSession.class);
    }

    @After
    public void teardown() {
        stateNeedPassword = null;
        username = null;
        loginService = null;
        fileManager = null;
        clientSession = null;
    }

    @Test
    public void testUser() {
        stateNeedPassword.user(clientSession, username);
        verify(loginService).login(clientSession, username);
    }

    @Test
    public void testPass() {
        String password = "password";
        stateNeedPassword.pass(clientSession, password);
        verify(loginService).login(clientSession, username, password);
    }

    @Test
    public void testQuit() {
        String message = stateNeedPassword.quit(clientSession);
        assertEquals("221 Service closing control connection.", message);
    }

    @Test
    public void testPort() {
        String message = stateNeedPassword.port(clientSession, 9001);
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testType() {
        String message = stateNeedPassword.type(clientSession, "NON PRINT");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testMode() {
        String message = stateNeedPassword.mode(clientSession, "S");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStru() {
        String message = stateNeedPassword.stru(clientSession, "F");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testRetr() {
        String message = stateNeedPassword.retr(clientSession, "/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testStor() {
        String message = stateNeedPassword.stor(clientSession, "/file.txt");
        assertEquals("530 Not logged in.", message);
    }

    @Test
    public void testNoop() {
        String message = stateNeedPassword.noop(clientSession);
        assertEquals("200 Command okay.", message);
    }

    @Test
    public void testAuth() {
        String message = stateNeedPassword.auth(clientSession);
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testSyst() {
        String message = stateNeedPassword.syst(clientSession);
        assertEquals("215 " + FTPServerConfig.OPERATING_SYSTEM + ": " + FTPServerConfig.SERVER_NAME, message);
    }

    @Test
    public void testFeat() {
        String message = stateNeedPassword.feat(clientSession);
        assertEquals("502 Command not implemented.", message);
    }

    @Test
    public void testPwd() {
        String message = stateNeedPassword.pwd(clientSession);
        assertEquals("257 /", message);
        verify(fileManager, times(2)).getCurrentDirectory();
    }

}
