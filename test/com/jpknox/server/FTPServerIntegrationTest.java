package com.jpknox.server;

import com.jpknox.server.state.StateLoggedIn;
import com.jpknox.server.state.StateNeedPassword;
import com.jpknox.server.state.StateNotLoggedIn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by JoaoPaulo on 08-Oct-17.
 */
public class FTPServerIntegrationTest {

    public static final String NEWLINE = System.getProperty("line.separator");

    private FTPServer ftpServer;
    private ServerSocket mockServerSocket;
    private Socket mockSocket;
    private PipedOutputStream serverInputWriter;
    private PipedInputStream serverInputStream;
    private PipedOutputStream serverOutputStream;
    private PipedInputStream serverOutputInputStream;
    private BufferedReader serverOutputReader;

    @Before
    public void setup() throws IOException {
        mockServerSocket = Mockito.mock(ServerSocket.class);
        mockSocket = Mockito.mock(Socket.class);
        when(mockServerSocket.accept()).thenReturn(mockSocket);

        //To send data to the server
        serverInputWriter = new PipedOutputStream();
        serverInputStream = new PipedInputStream(serverInputWriter);
        when(mockSocket.getInputStream()).thenReturn(serverInputStream);

        //To read data sent by the server
        serverOutputStream = new PipedOutputStream();
        serverOutputInputStream = new PipedInputStream();
        serverOutputStream.connect(serverOutputInputStream);
        serverOutputReader = new BufferedReader(new InputStreamReader(serverOutputInputStream));
        when(mockSocket.getOutputStream()).thenReturn(serverOutputStream);
    }

    @After
    public void teardown() throws IOException {
        ftpServer = null;
        mockSocket.close();
        mockSocket = null;
        mockServerSocket.close();
        mockServerSocket = null;
        serverInputWriter.close();
        serverInputWriter = null;
        serverInputStream.close();
        serverInputStream = null;
        serverOutputStream.close();
        serverOutputStream = null;
        serverOutputInputStream.close();
        serverOutputInputStream = null;
        serverOutputReader.close();
        serverOutputReader = null;
    }

    @Test
    public void testInitialStateNotLoggedIn() throws IOException {
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateLoggedInWithPassword() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("230 User1 logged in, proceed."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateLoggedInWithNoPassword() throws IOException {
        sendLine("USER anonymous");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("230 Anonymous logged in, proceed."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateNeedPassword() throws IOException {
        sendLine("USER user1");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateNeedPassword.class.getSimpleName(), state);
    }

    @Test
    public void testStateNotLoggedInWithBadPassword() throws IOException {
        sendLine("USER user1");
        sendLine("PASS badPassword");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("530 Not logged in."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testCannotReenterPassword() throws IOException {
        sendLine("USER user1");
        sendLine("PASS badPassword");
        sendLine("PASS badPassword");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("530 Not logged in."));
        assertTrue(serverOutputReader.readLine().equals("503 Bad sequence of commands."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateLoggedInAfterOneFailedAttempt() throws IOException {
        sendLine("USER user1");
        sendLine("PASS badPassword");
        sendLine("PASS badPassword");
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("530 Not logged in."));
        assertTrue(serverOutputReader.readLine().equals("503 Bad sequence of commands."));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("230 User1 logged in, proceed."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateNotLoggedInAfterEmptyUserParam() throws IOException {
        sendLine("USER");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals(("220 Welcome to Jay's FTP Server!"), serverOutputReader.readLine());
        assertEquals(("501 Syntax error in parameters or arguments."), serverOutputReader.readLine());
        assertEquals(("221 Service closing control connection."), serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testPasvCommand() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("PASV");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("230 User1 logged in, proceed."));
        assertEquals("227 Entering Passive Mode (127,0,0,1,", serverOutputReader.readLine().substring(0, 37));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testMultiplePasvCommandsEachReturnNewPorts() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("PASV");
        sendLine("PASV");
        sendLine("PASV");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("230 User1 logged in, proceed."));

        String[] pasvResponse = {serverOutputReader.readLine(),
                                 serverOutputReader.readLine(),
                                 serverOutputReader.readLine()};
        for (int i = 0; i < 3; i++) {
            for (int n = 1; n < 3; n++ ) {
                if (i == n) continue;
                assertFalse(pasvResponse[i].equals(pasvResponse[n]));
            }
        }
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testMessageContainingOnlySpacesDuringLogin() throws IOException {
        sendLine("");
        sendLine("USER user1");
        sendLine("                                                ");
        sendLine("PASS pass1");
        sendLine("                              ");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("501 Syntax error in parameters or arguments."));
        assertTrue(serverOutputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(serverOutputReader.readLine().equals("501 Syntax error in parameters or arguments."));
        assertTrue(serverOutputReader.readLine().equals("230 User1 logged in, proceed."));
        assertTrue(serverOutputReader.readLine().equals("501 Syntax error in parameters or arguments."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testInvalidCommand() throws IOException {
        sendLine("AB12");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("500 Syntax error, command unrecognized."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
    }

    @Test
    public void testLongInvalidCommand() throws IOException {
        sendLine("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" +
                    "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" +
                        "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("500 Syntax error, command unrecognized."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
    }

    @Test
    public void testShortInvalidCommand() throws IOException {
        sendLine("a");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("500 Syntax error, command unrecognized."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
    }

    @Test
    public void testRequestNameListBeforeSpecifyingTransmissionMode() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("NLST");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("425 Can't open data connection. Enter PASV first.", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testPrintWorkingDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1\"");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryTwoStepsIntoSubSubfolder() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1\"");
        sendLine("PWD");
        sendLine("CWD \"Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryOneStepIntoSubSubfolder() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1\\Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryOneStepIntoSubSubfolderUsingForwardSlash() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryGoUpOneLevel() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD ..");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryGoUpTwoLevels() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD ..\\..");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryGoUpTwoManyLevels() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD ..\\..\\..");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryPeriodStaysInSameDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD .");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryGoToRootBackslash() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD \\");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryNonexistantDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD Fol");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("550 Requested action not taken. File unavailable (e.g., file not found, no access)."
                , serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryNonexistantDirectoryShort() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD a");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("550 Requested action not taken. File unavailable (e.g., file not found, no access)."
                , serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryUndefinedDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD Fol");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("550 Requested action not taken. File unavailable (e.g., file not found, no access)."
                , serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryGoToRootForwardslash() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD /");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryUpAndDownRepeated() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1/../Subfolder 1/../Subfolder 1/../Subfolder 1/..\"");
        sendLine("PWD");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertEquals("220 Welcome to Jay's FTP Server!", serverOutputReader.readLine());
        assertEquals("331 User name okay, need password.", serverOutputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", serverOutputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", serverOutputReader.readLine());
        assertEquals("257 \\Folder 1\\", serverOutputReader.readLine());
        assertEquals("221 Service closing control connection.", serverOutputReader.readLine());
        String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    //TODO: TDD for scenario of missing parameters e.g. "PASS " instead of "PASS password"

    private void sendLine(String txt) {
        try {
            serverInputWriter.write((txt + NEWLINE).getBytes());
            serverInputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
