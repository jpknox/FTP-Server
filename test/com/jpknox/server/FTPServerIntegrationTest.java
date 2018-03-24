package com.jpknox.server;

import com.jpknox.server.state.StateLoggedIn;
import com.jpknox.server.state.StateNeedPassword;
import com.jpknox.server.state.StateNotLoggedIn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
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

    private PrintWriter outputWriter;
    private BufferedReader inputReader;
    private IntegrationTestWrapper integrationTestWrapper;
    private Thread integrationTestWrapperThread;
    boolean setUpIsDone;

    @Before
    public void setup() throws IOException {
        if (setUpIsDone) {
            return;
        }
        integrationTestWrapper = new IntegrationTestWrapper();
        integrationTestWrapperThread = new Thread(integrationTestWrapper);
        integrationTestWrapperThread.start();

        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 21);
        socket.connect(inetSocketAddress);

        //To send data to the server
        outputWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

        //To read data sent by the server
        inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        setUpIsDone = true;
    }

    @After
    public void teardown() throws IOException {
        //integrationTestWrapper.getFtpServer().setTestOveride(true);
        outputWriter.close();
        outputWriter = null;
        inputReader.close();
        inputReader = null;
    }

    @Test
    public void testInitialStateNotLoggedIn() throws IOException {
        sendLine("quit");
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        ////assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateLoggedInWithPassword() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("230 User1 logged in, proceed."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
    }

    @Test
    public void testStateLoggedInWithNoPassword() throws IOException {
        sendLine("USER anonymous");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("230 Anonymous logged in, proceed."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateNeedPassword() throws IOException {
        sendLine("USER user1");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateNeedPassword.class.getSimpleName(), state);
    }

    @Test
    public void testStateNotLoggedInWithBadPassword() throws IOException {
        sendLine("USER user1");
        sendLine("PASS badPassword");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("530 Not logged in."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testCannotReenterPassword() throws IOException {
        sendLine("USER user1");
        sendLine("PASS badPassword");
        sendLine("PASS badPassword");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("530 Not logged in."));
        assertTrue(inputReader.readLine().equals("503 Bad sequence of commands."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateLoggedInAfterOneFailedAttempt() throws IOException {
        sendLine("USER user1");
        sendLine("PASS badPassword");
        sendLine("PASS badPassword");
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("530 Not logged in."));
        assertTrue(inputReader.readLine().equals("503 Bad sequence of commands."));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("230 User1 logged in, proceed."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateNotLoggedInAfterEmptyUserParam() throws IOException {
        sendLine("USER");
        sendLine("quit");
        assertEquals(("220 Welcome to Jay's FTP Server!"), inputReader.readLine());
        assertEquals(("501 Syntax error in parameters or arguments."), inputReader.readLine());
        assertEquals(("221 Service closing control connection."), inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateNotLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testPasvCommand() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("PASV");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("230 User1 logged in, proceed."));
        assertEquals("227 Entering Passive Mode (127,0,0,1,", inputReader.readLine().substring(0, 37));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testMultiplePasvCommandsEachReturnNewPorts() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("PASV");
        sendLine("PASV");
        sendLine("PASV");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("230 User1 logged in, proceed."));

        String[] pasvResponse = {inputReader.readLine(),
                inputReader.readLine(),
                inputReader.readLine()};
        for (int i = 0; i < 3; i++) {
            for (int n = 1; n < 3; n++ ) {
                if (i == n) continue;
                assertFalse(pasvResponse[i].equals(pasvResponse[n]));
            }
        }
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testMessageContainingOnlySpacesDuringLogin() throws IOException {
        sendLine("");
        sendLine("USER user1");
        sendLine("                                                ");
        sendLine("PASS pass1");
        sendLine("                              ");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("501 Syntax error in parameters or arguments."));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("501 Syntax error in parameters or arguments."));
        assertTrue(inputReader.readLine().equals("230 User1 logged in, proceed."));
        assertTrue(inputReader.readLine().equals("501 Syntax error in parameters or arguments."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testInvalidCommand() throws IOException {
        sendLine("AB12");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("500 Syntax error, command unrecognized."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
    }

    @Test
    public void testLongInvalidCommand() throws IOException {
        sendLine("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" +
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" +
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("500 Syntax error, command unrecognized."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
    }

    @Test
    public void testShortInvalidCommand() throws IOException {
        sendLine("a");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("500 Syntax error, command unrecognized."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
    }

    @Test
    public void testRequestNameListBeforeSpecifyingTransmissionMode() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("NLST");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("425 Can't open data connection. Enter PASV first.", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testPrintWorkingDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
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
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryOneStepIntoSubSubfolder() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1\\Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryOneStepIntoSubSubfolderUsingForwardSlash() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
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
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
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
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
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
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
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
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
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
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryNonexistantDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD Fol");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("550 Requested action not taken. File unavailable (e.g., file not found, no access)."
                , inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryNonexistantDirectoryShort() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD a");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("550 Requested action not taken. File unavailable (e.g., file not found, no access)."
                , inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryUndefinedDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD ");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("501 Syntax error in parameters or arguments.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
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
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryUpAndDownRepeated() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 1/Subfolder 1/../Subfolder 1/../Subfolder 1/../Subfolder 1/..\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryCasingReflectsActualFolder() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"folder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryNoSpaces() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD NoSpaces");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\NoSpaces\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testChangeWorkingDirectoryRelativeToRoot() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"Folder 2\"");
        sendLine("CWD \"\\Folder 1\\Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
        //String state = ftpServer.getClientSessionController(0).getSession().getState().getClass().getSimpleName();
        //assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    //TODO: TDD for scenario of missing parameters e.g. "PASS " instead of "PASS password"

    private void sendLine(String txt) {
        outputWriter.print((txt + NEWLINE));
        outputWriter.flush();
    }
}
