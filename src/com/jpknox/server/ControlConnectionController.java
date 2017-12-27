package com.jpknox.server;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;
import com.jpknox.server.command.FTPCommandDecoder;
import com.jpknox.server.response.ControlConnectionCommunicator;
import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.storage.FileManager;
import com.jpknox.server.transfer.DataTransferController;

import java.io.*;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ControlConnectionController {

    private final Socket clientConnection;
    private final FileManager fileManager = new FileManager();
    private final FTPResponseFactory responseFactory = new FTPResponseFactory();
    private final ControlConnectionCommunicator communicator = new ControlConnectionCommunicator();
    private final FTPCommandDecoder FTPCommandDecoder = new FTPCommandDecoder();
    private final DataTransferController dataTransferController = new DataTransferController(communicator);
    private final ClientSession clientSession = new ClientSession(dataTransferController);
    private FTPCommand ftpCommand;
    private FTPCommandAction ftpCommandAction;
    private String response = "";

    public ControlConnectionController(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void start() {
        try {
            //log("Setting up I/O.");
            communicator.setOutput(new PrintWriter(new OutputStreamWriter(this.clientConnection.getOutputStream())));
            communicator.setInput(new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream())));
            //log("I/O set up successfully.");

            communicator.write("220 Welcome to Jay's FTP Server!\r\n");
            //log("Sent welcome message.");

            String dataFromClient;
            String tempData;

            log("Entering primary input loop");
        inputLoop:
            while (true) {

                //Loop over never ending null chars sent by FTP clients
                while (true) {
                    //log("Entered the keep-alive input loop.");
                    dataFromClient = communicator.readLine();
                    //log("Received input from client.");
                    if (!dataFromClient.equals(null)) {
                        break;
                    } else {
                        log("Sleeping for 100 millis");
                        Thread.sleep(100);
                    }
                }
                log(clientSession.getClientName() + ": " + dataFromClient);


                ftpCommand = FTPCommandDecoder.decode(dataFromClient);
                ftpCommandAction = ftpCommand.getAction();
                switch (ftpCommandAction) {
                    case USER:    response = clientSession.getState().user(ftpCommand.getParams()[0]); //Extract username
                                  break;
                    case PASS:    response = clientSession.getState().pass(ftpCommand.getParams()[0]); //Extract password
                                  break;
                    case PASV:    response = clientSession.getState().pasv();
                                  break;
                    case QUIT:    log(clientSession.getClientName() + " disconnected.");
                                  response = clientSession.getState().quit();
                                  communicator.write(response);
                                  break inputLoop;
                    case NLST:    response = clientSession.getState().nlst();
                                  break;
                    case AUTH:    response = clientSession.getState().auth();
                                  break;
                    case SYST:    response = clientSession.getState().syst();
                                  break;
                    case FEAT:    response = clientSession.getState().feat();
                                  break;
                    case PWD:     response = clientSession.getState().pwd();
                                  break;
                    case NOOP:    response = clientSession.getState().noop();
                                  break;
                    case ERROR_0: response = responseFactory.createResponse(500);
                                  break;
                    case ERROR_1: response = responseFactory.createResponse(501);
                }
                communicator.write(response);
                response = "";

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ClientSession getClientSession() {
        return this.clientSession;
    }
}
