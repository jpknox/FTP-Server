package com.jpknox.server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.apache.commons.io.FileUtils.contentEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by JoaoPaulo on 08-Oct-17.
 */
public class FTPServerIntegrationTest {

    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String SEPARATOR = System.getProperty("file.separator");
    public static final String ROOT_DIR_NAME = "RealFtpStorage";

    private PrintWriter outputWriter;
    private BufferedReader inputReader;
    private IntegrationTestWrapper integrationTestWrapper;
    private Thread integrationTestWrapperThread;
    private static boolean setUpIsDone;
    private Socket controlConnectionToServer;
    private Socket dataConnectionFromServer;
    private BufferedReader dataConnectionLineReader;

    @Before
    public void setup() throws IOException {
        if (!setUpIsDone) {
            integrationTestWrapper = new IntegrationTestWrapper();
            integrationTestWrapperThread = new Thread(integrationTestWrapper);
            integrationTestWrapperThread.start();
        }

        controlConnectionToServer = new Socket((String)null, 21); //null address defaults to loopback interface

        //To send data to the server
        outputWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(controlConnectionToServer.getOutputStream())));

        //To read data sent by the server
        inputReader = new BufferedReader(new InputStreamReader(controlConnectionToServer.getInputStream()));

        setUpIsDone = true;
    }

    @After
    public void teardown() throws IOException {
        outputWriter.close();
        outputWriter = null;
        inputReader.close();
        inputReader = null;
        controlConnectionToServer.close();
    }

    @Test
    public void testInitialStateNotLoggedIn() throws IOException {
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
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
    }

    @Test
    public void testStateNeedPassword() throws IOException {
        sendLine("USER user1");
        sendLine("quit");
        assertTrue(inputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(inputReader.readLine().equals("331 User name okay, need password."));
        assertTrue(inputReader.readLine().equals("221 Service closing control connection."));
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
    }

    @Test
    public void testStateNotLoggedInAfterEmptyUserParam() throws IOException {
        sendLine("USER");
        sendLine("quit");
        assertEquals(("220 Welcome to Jay's FTP Server!"), inputReader.readLine());
        assertEquals(("501 Syntax error in parameters or arguments."), inputReader.readLine());
        assertEquals(("221 Service closing control connection."), inputReader.readLine());
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
    }

    @Test
    public void testChangeWorkingDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData\\Folder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryTwoStepsIntoSubSubfolder() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData\\Folder 1\"");
        sendLine("PWD");
        sendLine("CWD \"Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryOneStepIntoSubSubfolder() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1\\Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryOneStepIntoSubSubfolderUsingForwardSlash() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryGoUpOneLevel() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD ..");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryGoUpTwoLevels() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD ..\\..");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryGoUpTooManyLevels() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD ..\\..\\..");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryPeriodStaysInSameDirectory() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData\\Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD .");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryGoToRootBackslash() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD \\");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
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
    }

    @Test
    public void testChangeWorkingDirectoryGoToRootForwardslash() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1/Subfolder 1\"");
        sendLine("PWD");
        sendLine("CWD /");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryUpAndDownRepeated() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/Folder 1/Subfolder 1/../Subfolder 1/../Subfolder 1/../Subfolder 1/..\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryCasingReflectsActualFolder() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData/folder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
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
    }

    @Test
    public void testChangeWorkingDirectoryRelativeToRoot() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD \"TestData\\Folder 2\"");
        sendLine("CWD \"\\TestData\\Folder 1\\Subfolder 1\"");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testChangeWorkingDirectoryRelativeToRoot_NoQuotes_FolderHasSpaceInName() throws IOException {
        sendLine("USER user1");
        sendLine("PASS pass1");
        sendLine("CWD TestData\\Folder 2");
        sendLine("CWD \\TestData\\Folder 1\\Subfolder 1");
        sendLine("PWD");
        sendLine("quit");
        assertEquals("220 Welcome to Jay's FTP Server!", inputReader.readLine());
        assertEquals("331 User name okay, need password.", inputReader.readLine());
        assertEquals("230 User1 logged in, proceed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("250 Requested file action okay, completed.", inputReader.readLine());
        assertEquals("257 \\TestData\\Folder 1\\Subfolder 1\\", inputReader.readLine());
        assertEquals("221 Service closing control connection.", inputReader.readLine());
    }

    @Test
    public void testListCommand() throws IOException {
        assertEquals("220 Welcome to Jay's FTP Server!", readLine());
        sendLine("USER user1");
        assertEquals("331 User name okay, need password.", readLine());
        sendLine("PASS pass1");
        assertEquals("230 User1 logged in, proceed.", readLine());
        sendLine("CWD TestData");
        assertEquals("250 Requested file action okay, completed.", readLine());

        //Open the data connection
        sendLine("PASV");
        String pasvResponse = readLine();
        assertEquals("227 Entering Passive Mode (127,0,0,1,", pasvResponse.substring(0, 37));
        String[] encodedPort = new String[2];
        pasvResponse = pasvResponse.replace(")", "");   //Remove trailing parenthesis
        System.arraycopy(pasvResponse.split(","), 4, encodedPort, 0, 2);
        int port = Integer.parseInt(encodedPort[0]) * 256 + Integer.parseInt(encodedPort[1]);

        sendLine("LIST");
        dataConnectionFromServer = new Socket((String) null, port);
        dataConnectionLineReader = new BufferedReader(new InputStreamReader(dataConnectionFromServer.getInputStream()));
        ArrayList<String> linesOfData = new ArrayList();
        String data = null;
        while ((data = dataConnectionLineReader.readLine()) != null) {
            linesOfData.add(data);
        }
        assertEquals("drw-r--r--\t1 0\t0\t0 12 31  2017 Folder 1", linesOfData.get(0));
        assertEquals("drw-r--r--\t1 0\t0\t0 4 3  2018 Folder 2", linesOfData.get(1));
        assertEquals("drw-r--r--\t1 0\t0\t0 4 3  2018 Folder1", linesOfData.get(2));
        assertEquals("drw-r--r--\t1 0\t0\t0 4 3  2018 Folder2", linesOfData.get(3));
        assertEquals("drw-r--r--\t1 0\t0\t0 4 2  2018 TempDestination", linesOfData.get(4));
        assertEquals("-rw-r--r--\t1 0\t0\t38 4 3  2018 textAtRootOfTestDirectory.txt", linesOfData.get(5));
        assertEquals("226 Closing data connection.", readLine());

        sendLine("quit");
        assertEquals("221 Service closing control connection.", readLine());

        dataConnectionLineReader.close();
        dataConnectionFromServer.close();
    }

    @Test
    public void testFileDownload_SameDirectory() throws IOException {
        assertEquals("220 Welcome to Jay's FTP Server!", readLine());
        sendLine("USER user1");
        assertEquals("331 User name okay, need password.", readLine());
        sendLine("PASS pass1");
        assertEquals("230 User1 logged in, proceed.", readLine());
        sendLine("CWD TestData");
        assertEquals("250 Requested file action okay, completed.", readLine());

        //Open the data connection.
        sendLine("PASV");
        String pasvResponse = readLine();
        assertEquals("227 Entering Passive Mode (127,0,0,1,", pasvResponse.substring(0, 37));
        String[] encodedPort = new String[2];
        pasvResponse = pasvResponse.replace(")", "");   //Remove trailing parenthesis
        System.arraycopy(pasvResponse.split(","), 4, encodedPort, 0, 2); //Extract encoded port
        int port = Integer.parseInt(encodedPort[0]) * 256 + Integer.parseInt(encodedPort[1]); //Calculate port

        sendLine("RETR textAtRootOfTestDirectory.txt");
        dataConnectionFromServer = new Socket((String) null, port); //Null address is like loopback address
        BufferedInputStream byteReader = new BufferedInputStream(dataConnectionFromServer.getInputStream());
        File inboundTestFile =
                new File(ROOT_DIR_NAME + SEPARATOR +
                "TestData" + SEPARATOR +
                "TempDestination" + SEPARATOR +
                "textAtRootOfTestDirectory.txt");
        BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(inboundTestFile));
        for (int i; (i = byteReader.read()) != -1; fileWriter.write(i));
        byteReader.close();
        fileWriter.close();
        assertEquals("226 Closing data connection.", readLine());

        sendLine("quit");
        assertEquals("221 Service closing control connection.", readLine());

        File sourceTestFile = new File(ROOT_DIR_NAME + SEPARATOR +
                "TestData" + SEPARATOR + "textAtRootOfTestDirectory.txt");
        assertTrue(contentEquals(sourceTestFile, inboundTestFile));
        inboundTestFile.delete();
        dataConnectionFromServer.close();
    }

    @Test
    public void testCreateNewFolder() throws IOException {
        assertEquals("220 Welcome to Jay's FTP Server!", readLine());
        sendLine("USER user1");
        assertEquals("331 User name okay, need password.", readLine());
        sendLine("PASS pass1");
        assertEquals("230 User1 logged in, proceed.", readLine());
        sendLine("CWD TestData/TempDestination");
        assertEquals("250 Requested file action okay, completed.", readLine());

        sendLine("MKD aTestFolder");
        assertEquals("250 Requested file action okay, completed.", readLine());

        File newlyCreatedFolder =
                new File(ROOT_DIR_NAME + SEPARATOR +
                "TestData" + SEPARATOR +
                "TempDestination" + SEPARATOR +
                "aTestFolder");
        assertTrue(newlyCreatedFolder.isDirectory());
        newlyCreatedFolder.delete();

        sendLine("quit");
        assertEquals("221 Service closing control connection.", readLine());
    }

    @Test
    public void testDeleteFolder() throws IOException {
        File shouldBeDeletedByTest_Folder =
                new File(ROOT_DIR_NAME + SEPARATOR +
                        "TestData" + SEPARATOR +
                        "TempDestination" + SEPARATOR +
                        "shouldBeDeletedByTest_Folder");
        shouldBeDeletedByTest_Folder.mkdir();
        assertTrue(shouldBeDeletedByTest_Folder.isDirectory());
        
        assertEquals("220 Welcome to Jay's FTP Server!", readLine());
        sendLine("USER user1");
        assertEquals("331 User name okay, need password.", readLine());
        sendLine("PASS pass1");
        assertEquals("230 User1 logged in, proceed.", readLine());
        sendLine("CWD TestData/TempDestination");
        assertEquals("250 Requested file action okay, completed.", readLine());

        sendLine("RMD shouldBeDeletedByTest_Folder");
        assertEquals("250 Requested file action okay, completed.", readLine());
        assertFalse(shouldBeDeletedByTest_Folder.isDirectory());

        sendLine("quit");
        assertEquals("221 Service closing control connection.", readLine());
    }

    //TODO: TDD for scenario of missing parameters e.g. "PASS " instead of "PASS password"

    private void sendLine(String txt) {
        outputWriter.print((txt + NEWLINE));
        outputWriter.flush();
    }

    private String readLine() throws IOException {
        return inputReader.readLine();
    }
}
