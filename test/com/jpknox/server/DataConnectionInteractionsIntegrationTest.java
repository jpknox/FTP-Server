package com.jpknox.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.io.FileUtils.contentEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 08-Oct-17.
 */
public class DataConnectionInteractionsIntegrationTest {

    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String SEPARATOR = System.getProperty("file.separator");
    public static final String ROOT_DIR_NAME = "RealFtpStorage";


    private PrintWriter outputWriter;
    private BufferedReader inputReader;
    private IntegrationTestWrapper integrationTestWrapper;
    private Thread integrationTestWrapperThread;
    private static boolean setUpIsDone;
    private Socket dataConnectionFromServer;
    private BufferedReader dataConnectionLineReader;
    private Socket controlConnectionToServer;

    @Before
    public void setup() throws IOException {
        if (!setUpIsDone) {
            integrationTestWrapper = new IntegrationTestWrapper();
            integrationTestWrapperThread = new Thread(integrationTestWrapper);
            integrationTestWrapperThread.start();
        }

        controlConnectionToServer = new Socket("127.0.0.1", 21);

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
        assertEquals("drw-r--r--\t1 0\t0\t0 4 1  2018 Folder1", linesOfData.get(0));
        assertEquals("drw-r--r--\t1 0\t0\t0 4 1  2018 Folder2", linesOfData.get(1));
        assertEquals("drw-r--r--\t1 0\t0\t0 4 2  2018 TempDestination", linesOfData.get(2));
        assertEquals("-rw-r--r--\t1 0\t0\t38 4 1  2018 textAtRootOfTestDirectory.txt", linesOfData.get(3));
        assertEquals("226 Closing data connection.", readLine());

        sendLine("quit");
        assertEquals("221 Service closing control connection.", readLine());

        dataConnectionLineReader.close();
        dataConnectionFromServer.close();
    }

    @Test
    public void testFileDownload_OneLayerDown_SameDirectory() throws IOException {
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
        File inboundTestFile = new File(ROOT_DIR_NAME + SEPARATOR +
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

    private void sendLine(String txt) {
        outputWriter.print((txt + NEWLINE));
        outputWriter.flush();
    }

    private String readLine() throws IOException {
        return inputReader.readLine();
    }
}
