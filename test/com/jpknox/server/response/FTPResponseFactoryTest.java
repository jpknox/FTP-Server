package com.jpknox.server.response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by joaok on 25/12/2017.
 */
public class FTPResponseFactoryTest {

    private ResponseFactory responseFactory;

    @Before
    public void setup() {
        responseFactory = new FTPResponseFactory();
    }

    @After
    public void teardown() {
        responseFactory = null;
    }

    @Test
    public void testDirectoryListing() {
        String expect = "150 Sending the directory listing.";
        String actual = responseFactory.createResponse(150);
        assertEquals(expect, actual);
    }

    @Test
    public void testCommandOkay() {
        String expect = "200 Command okay.";
        String actual = responseFactory.createResponse(200);
        assertEquals(expect, actual);
    }

    @Test
    public void testCommandOkayWithArg() {
        String expect = "200 Working directory changed.";
        String actual = responseFactory.createResponse(200, "CWD");
        assertEquals(expect, actual);
    }

    @Test
    public void testCommandOkayWithSpuriousArg() {
        String expect = "200 Command okay.";
        String actual = responseFactory.createResponse(200, "ASDASDASDA!");
        assertEquals(expect, actual);
    }

    @Test
    public void testSuperfluousCommand() {
        String expect = "202 Command not implemented, superfluous at this site.";
        String actual = responseFactory.createResponse(202);
        assertEquals(expect, actual);
    }

    @Test
    public void testSyst() {
        String expect = "215 Jay's FTP Server V1.0: Windows 10";
        String actual = responseFactory.createResponse(215, "Jay's FTP Server V1.0", "Windows 10");
        assertEquals(expect, actual);
    }

    @Test
    public void testClosingControlConnection() {
        String expect = "221 Service closing control connection.";
        String actual = responseFactory.createResponse(221);
        assertEquals(expect, actual);
    }

    @Test
    public void testClosingDataConnection() {
        String expect = "226 Closing data connection.";
        String actual = responseFactory.createResponse(226);
        assertEquals(expect, actual);
    }

    @Test
    public void testSuccessfulLogin() {
        String expect = "230 Joao logged in, proceed.";
        String username = "joao";
        String actual = responseFactory.createResponse(230, username);
        assertEquals(expect, actual);
    }

    @Test
    public void testTwoResponsesWontInterfere() {
        String expectFirst = "230 Joao logged in, proceed.";
        String username = "joao";
        String actualFirst = responseFactory.createResponse(230, username);
        assertEquals(expectFirst, actualFirst);

        String expectSecond = "530 Not logged in.";
        String actualSecond = responseFactory.createResponse(530);
        assertEquals(expectSecond, actualSecond);
    }

    @Test
    public void testCwd() {
        String expect = "250 Requested file action okay, completed.";
        String actual = responseFactory.createResponse(250);
        assertEquals(expect, actual);
    }

    @Test
    public void testPwd() {
        String expect = "257 /";
        String actual = responseFactory.createResponse(257, "/");
        assertEquals(expect, actual);
    }

    @Test
    public void testNeedPassword() {
        String expect = "331 User name okay, need password.";
        String actual = responseFactory.createResponse(331);
        assertEquals(expect, actual);
    }

    @Test
    public void testCantOpenDataConnection() {
        String expect = "425 Can't open data connection. Enter PASV first.";
        String actual = responseFactory.createResponse(425);
        assertEquals(expect, actual);
    }

    @Test
    public void testCommandNotRecognised() {
        String expect = "500 Syntax error, command unrecognized.";
        String actual = responseFactory.createResponse(500);
        assertEquals(expect, actual);
    }

    @Test
    public void testSyntaxError() {
        String expect = "501 Syntax error in parameters or arguments.";
        String actual = responseFactory.createResponse(501);
        assertEquals(expect, actual);
    }

    @Test
    public void testCommandNotImplemented() {
        String expect = "502 Command not implemented.";
        String actual = responseFactory.createResponse(502);
        assertEquals(expect, actual);
    }

    @Test
    public void testBadSequenceOfCommands() {
        String expect = "503 Bad sequence of commands.";
        String actual = responseFactory.createResponse(503);
        assertEquals(expect, actual);
    }

    @Test
    public void testFailedLogin() {
        String expect = "530 Not logged in.";
        String actual = responseFactory.createResponse(530);
        assertEquals(expect, actual);
    }

    @Test
    public void testRequestedFileActionFailure() {
        String expect = "550 Requested action not taken. File unavailable (e.g., file not found, no access).";
        String actual = responseFactory.createResponse(550);
        assertEquals(expect, actual);
    }
}
