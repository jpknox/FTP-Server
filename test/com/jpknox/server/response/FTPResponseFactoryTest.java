package com.jpknox.server.response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by joaok on 25/12/2017.
 */
public class FTPResponseFactoryTest {

    private ResponseFactory ResponseFactory;

    @Before
    public void setup() {
        ResponseFactory = new FTPResponseFactory();
    }

    @After
    public void teardown() {
        ResponseFactory = null;
    }

    @Test
    public void testSuccessfulLogin() {
        String expect = "230 Joao logged in, proceed.";
        String username = "joao";
        String output = ResponseFactory.createResponse(230, username);
        assertEquals(expect, output);
    }

    @Test
    public void testFailedLogin() {
        String expect = "530 Not logged in.";
        String output = ResponseFactory.createResponse(530);
        assertEquals(expect, output);
    }

    @Test
    public void testTwoResponsesWontInterfere() {
        String expectFirst = "230 Joao logged in, proceed.";
        String username = "joao";
        String outputFirst = ResponseFactory.createResponse(230, username);
        assertEquals(expectFirst, outputFirst);

        String expectSecond = "530 Not logged in.";
        String outputSecond = ResponseFactory.createResponse(530);
        assertEquals(expectSecond, outputSecond);
    }

    @Test
    public void testNeedPassword() {
        String expect = "331 User name okay, need password.";
        String output = ResponseFactory.createResponse(331);
        assertEquals(expect, output);
    }

    @Test
    public void testClosingControlConnection() {
        String expect = "221 Service closing control connection.";
        String output = ResponseFactory.createResponse(221);
        assertEquals(expect, output);
    }

    @Test
    public void testBadSequenceOfCommands() {
        String expect = "503 Bad sequence of commands.";
        String output = ResponseFactory.createResponse(503);
        assertEquals(expect, output);
    }

    @Test
    public void testCommandOkay() {
        String expect = "200 Command okay.";
        String output = ResponseFactory.createResponse(200);
        assertEquals(expect, output);
    }

    @Test
    public void testCommandNotImplemented() {
        String expect = "502 Command not implemented.";
        String output = ResponseFactory.createResponse(502);
        assertEquals(expect, output);
    }

    @Test
    public void testSyst() {
        String expect = "215 Jay's FTP Server V1.0: Windows 10";
        String output = ResponseFactory.createResponse(215, "Jay's FTP Server V1.0", "Windows 10");
        assertEquals(expect, output);
    }

    @Test
    public void testPwd() {
        String expect = "257 /";
        String output = ResponseFactory.createResponse(257, "/");
        assertEquals(expect, output);
    }

}
