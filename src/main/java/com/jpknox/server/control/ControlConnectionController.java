package com.jpknox.server.control;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;
import com.jpknox.server.command.FTPCommandDecoder;
import com.jpknox.server.response.ClientViewCommunicator;
import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ControlConnectionController implements Runnable {

    private final Socket clientConnection;
    private final ClientViewCommunicator viewCommunicator = new ClientViewCommunicator();
    private final FTPCommandDecoder FTPCommandDecoder = new FTPCommandDecoder();
    private final ClientSession session = new ClientSession(viewCommunicator);
    private FTPCommand ftpCommand;
    private FTPCommandAction ftpCommandAction;
    private String parameter;

    public ControlConnectionController(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void run() {
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
                    //log("Received input from client.");
                    try {
                        dataFromClient = viewCommunicator.readLine();
                        if (!dataFromClient.equals(null)) {
                            break;
                        } else {
                            log("Sleeping for 100 millis");
                            Thread.sleep(500);
                        }
                    } catch (NullPointerException | SocketException e) {
                        log(String.format("User '%s' has disconnected.", session.getUsername()));
                        break inputLoop;
                    }
                }
                log(session.getUsername() + ": " + dataFromClient);


                ftpCommand = FTPCommandDecoder.decode(dataFromClient);
                ftpCommandAction = ftpCommand.getAction();
                parameter = ftpCommand.getParams()[0];
                switch (ftpCommandAction) {
                    case USER:    session.getState().user(parameter); //Extract username
                                  break;
                    case PASS:    session.getState().pass(parameter); //Extract password
                                  break;
                    case PASV:    session.getState().pasv();
                                  break;
                    case QUIT:    session.getState().quit();
                                  log(session.getUsername() + " disconnected.");
                                  break inputLoop;
                    case NLST:    session.getState().nlst();
                                  break;
                    case STOR:    session.getState().stor(parameter);
                                  break;
                    case RETR:    session.getState().retr(parameter);
                                  break;
                    case DELE:    session.getState().dele(parameter);
                                  break;
                    case AUTH:    session.getState().auth();
                                  break;
                    case SYST:    session.getState().syst();
                                  break;
                    case FEAT:    session.getState().feat();
                                  break;
                    case RMD:     session.getState().rmd(parameter);
                                  break;
                    case MKD:     session.getState().mkd(parameter);
                                  break;
                    case PWD:     session.getState().pwd();
                                  break;
                    case CWD:     session.getState().cwd(parameter);
                                  break;
                    case NOOP:    session.getState().noop();
                                  break;
                    case TYPE:    session.getViewCommunicator().write(FTPResponseFactory.createResponse(202));
                                  break;
                    case LIST:    session.getState().list();
                                  break;
                    case ERROR_0: session.getViewCommunicator().write(FTPResponseFactory.createResponse(500));
                                  break;
                    case ERROR_1: session.getViewCommunicator().write(FTPResponseFactory.createResponse(501));
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
