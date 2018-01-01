package com.jpknox.server.control;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;
import com.jpknox.server.command.FTPCommandDecoder;
import com.jpknox.server.response.ClientViewCommunicator;
import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;

import java.io.*;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ControlConnectionController {

    private final Socket clientConnection;
    private final FTPResponseFactory responseFactory = new FTPResponseFactory();
    private final ClientViewCommunicator viewCommunicator = new ClientViewCommunicator();
    private final FTPCommandDecoder FTPCommandDecoder = new FTPCommandDecoder();
    private final ClientSession session = new ClientSession(viewCommunicator);
    private FTPCommand ftpCommand;
    private FTPCommandAction ftpCommandAction;

    public ControlConnectionController(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void start() {
        try {
            //log("Setting up I/O.");
            viewCommunicator.setOutput(new PrintWriter(new OutputStreamWriter(this.clientConnection.getOutputStream())));
            viewCommunicator.setInput(new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream())));
            //log("I/O set up successfully.");

            viewCommunicator.write("220 Welcome to Jay's FTP Server!");
            //log("Sent welcome message.");

            String dataFromClient;
            String tempData;

            log("Entering primary input loop");
        inputLoop:
            while (true) {

                //Loop over never ending null chars sent by FTP clients
                while (true) {
                    //log("Entered the keep-alive input loop.");
                    dataFromClient = viewCommunicator.readLine();
                    //log("Received input from client.");
                    if (!dataFromClient.equals(null)) {
                        break;
                    } else {
                        log("Sleeping for 100 millis");
                        Thread.sleep(100);
                    }
                }
                log(session.getClientName() + ": " + dataFromClient);


                ftpCommand = FTPCommandDecoder.decode(dataFromClient);
                ftpCommandAction = ftpCommand.getAction();
                switch (ftpCommandAction) {
                    case USER:    session.getState().user(ftpCommand.getParams()[0]); //Extract username
                                  break;
                    case PASS:    session.getState().pass(ftpCommand.getParams()[0]); //Extract password
                                  break;
                    case PASV:    session.getState().pasv();
                                  break;
                    case QUIT:    session.getState().quit();
                                  log(session.getClientName() + " disconnected.");
                                  break inputLoop;
                    case NLST:    session.getState().nlst();
                                  break;
                    case STOR:    session.getState().stor(ftpCommand.getParams()[0]);
                                  break;
                    case AUTH:    session.getState().auth();
                                  break;
                    case SYST:    session.getState().syst();
                                  break;
                    case FEAT:    session.getState().feat();
                                  break;
                    case PWD:     session.getState().pwd();
                                  break;
                    case CWD:     session.getState().cwd(ftpCommand.getParams()[0]);
                                  break;
                    case NOOP:    session.getState().noop();
                                  break;
                    case TYPE:    session.getViewCommunicator().write(responseFactory.createResponse(202));
                                  break;
                    case LIST:    session.getState().list();
                                  break;
                    case ERROR_0: session.getViewCommunicator().write(responseFactory.createResponse(500));
                                  break;
                    case ERROR_1: session.getViewCommunicator().write(responseFactory.createResponse(501));
                }

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Used by the integration tests
    public ClientSession getSession() {
        return this.session;
    }
}
