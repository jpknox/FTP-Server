package com.jpknox.server.session;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;
import com.jpknox.server.command.FTPCommandDecoder;
import com.jpknox.server.response.FTPResponseFactory;

import java.io.*;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ClientSessionController {

    private final ClientSession clientSession = new ClientSession();
    private final FTPCommandDecoder FTPCommandDecoder = new FTPCommandDecoder();
    private final Socket clientConnection;
    private FTPCommand ftpCommand;
    private FTPCommandAction ftpCommandAction;
    private BufferedReader input;
    private PrintWriter output;
    private String actionResponse = "";
    private FTPResponseFactory responseFactory = new FTPResponseFactory();

    public ClientSessionController(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void start() {
        try {
            //log("Setting up I/O.");
            this.input = new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream()));
            this.output = new PrintWriter(new OutputStreamWriter(this.clientConnection.getOutputStream()));
            //log("I/O set up successfully.");

            output.write("220 Welcome to Jay's FTP Server!\r\n");
            output.flush();
            //log("Sent welcome message.");

            String dataFromClient;
            String tempData;

            log("Entering primary input loop");
        inputLoop:
            while (true) {

                //Loop over never ending null chars sent by FTP clients
                while (true) {
                    //log("Entered the keep-alive input loop.");
                    dataFromClient = input.readLine();
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
                    case USER:    actionResponse = clientSession.getState().user(ftpCommand.getParams()[0]); //Extract username
                                  break;
                    case PASS:    actionResponse = clientSession.getState().pass(ftpCommand.getParams()[0]); //Extract password
                                  break;
                    case PASV:    actionResponse = clientSession.getState().pasv();
                                  break;
                    case QUIT:    log(clientSession.getClientName() + " disconnected.");
                                  actionResponse = clientSession.getState().quit();
                                  sendToClient(actionResponse);
                                  break inputLoop;
                    case AUTH:    actionResponse = clientSession.getState().auth();
                                  break;
                    case SYST:    actionResponse = clientSession.getState().syst();
                                  break;
                    case FEAT:    actionResponse = clientSession.getState().feat();
                                  break;
                    case PWD:     actionResponse = clientSession.getState().pwd();
                                  break;
                    case ERROR_0: actionResponse = responseFactory.createResponse(500);
                                  break;
                    case ERROR_1: actionResponse = responseFactory.createResponse(501);
                }
                sendToClient(actionResponse);
                actionResponse = "";

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ClientSession getClientSession() {
        return this.clientSession;
    }

    private int sendToClient(String text) {
        output.write(text + "\r\n");
        output.flush();
        return 0;
    }
}
