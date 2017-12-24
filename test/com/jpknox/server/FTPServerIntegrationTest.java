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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    @Test
    public void testStateNotLoggedInAfterEmptyUserParam() throws IOException {
        sendLine("USER");
        sendLine("quit");
        ftpServer = new FTPServer(mockServerSocket);
        assertTrue(serverOutputReader.readLine().equals("220 Welcome to Jay's FTP Server!"));
        assertTrue(serverOutputReader.readLine().equals("501 Syntax error in parameters or arguments."));
        assertTrue(serverOutputReader.readLine().equals("221 Service closing control connection."));
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
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
        String state = ftpServer.getClientSessionController(0).getClientSession().getState().getClass().getSimpleName();
        assertEquals(StateLoggedIn.class.getSimpleName(), state);
    }

    //TODO: TDD for scenario of missing parameters e.g. "PASS " instead of "PASS password"
    //TODO: TDD for illegal/missing/too short command/message given to server
            // Command out of sequence

    private void sendLine(String txt) {
        try {
            serverInputWriter.write((txt + NEWLINE).getBytes());
            serverInputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
