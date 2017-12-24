package com.jpknox.server.command;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 */
public class FTPCommandActionTest {

    private FTPCommandAction commandAction;

    @After
    public void teardown() {
        commandAction = null;
    }

    @Test
    public void testUser() {
        commandAction = FTPCommandAction.valueOf("USER");
        assertEquals(FTPCommandAction.USER, commandAction);
    }

    @Test
    public void testPass() {
        commandAction = FTPCommandAction.valueOf("PASS");
        assertEquals(FTPCommandAction.PASS, commandAction);
    }

    @Test
    public void testQuit() {
        commandAction = FTPCommandAction.valueOf("QUIT");
        assertEquals(FTPCommandAction.QUIT, commandAction);
    }

    @Test
    public void testPort() {
        commandAction = FTPCommandAction.valueOf("PORT");
        assertEquals(FTPCommandAction.PORT, commandAction);
    }

    @Test
    public void testType() {
        commandAction = FTPCommandAction.valueOf("TYPE");
        assertEquals(FTPCommandAction.TYPE, commandAction);
    }

    @Test
    public void testMode() {
        commandAction = FTPCommandAction.valueOf("MODE");
        assertEquals(FTPCommandAction.MODE, commandAction);
    }

    @Test
    public void testStru() {
        commandAction = FTPCommandAction.valueOf("STRU");
        assertEquals(FTPCommandAction.STRU, commandAction);
    }

    @Test
    public void testRetr() {
        commandAction = FTPCommandAction.valueOf("RETR");
        assertEquals(FTPCommandAction.RETR, commandAction);
    }

    @Test
    public void testStor() {
        commandAction = FTPCommandAction.valueOf("STOR");
        assertEquals(FTPCommandAction.STOR, commandAction);
    }

    @Test
    public void testNoop() {
        commandAction = FTPCommandAction.valueOf("NOOP");
        assertEquals(FTPCommandAction.NOOP, commandAction);
    }

    @Test
    public void testAuth() {
        commandAction = FTPCommandAction.valueOf("AUTH");
        assertEquals(FTPCommandAction.AUTH, commandAction);
    }

    @Test
    public void testSyst() {
        commandAction = FTPCommandAction.valueOf("SYST");
        assertEquals(FTPCommandAction.SYST, commandAction);
    }

    @Test
    public void testFeat() {
        commandAction = FTPCommandAction.valueOf("FEAT");
        assertEquals(FTPCommandAction.FEAT, commandAction);
    }

    @Test
    public void testPwd() {
        commandAction = FTPCommandAction.valueOf("PWD");
        assertEquals(FTPCommandAction.PWD, commandAction);
    }

    @Test
    public void testError() {
        commandAction = FTPCommandAction.valueOf("ERROR_0");
        assertEquals(FTPCommandAction.ERROR_0, commandAction);
    }

}
