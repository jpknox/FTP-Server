package com.jpknox.server.session;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;
import com.jpknox.server.utility.CommandDecoder;

import java.io.*;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ClientSessionController {

    private final ClientSession clientSession = new ClientSession();
    private final CommandDecoder commandDecoder = new CommandDecoder();
    private final Socket clientConnection;
    private FTPCommand ftpCommand;
    private FTPCommandAction ftpCommandAction;
    private BufferedReader input;
    private PrintWriter output;
    private String actionResponse = "";

    public ClientSessionController(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void start() {
        try {
            log("Setting up I/O.");
            this.input = new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream()));
            this.output = new PrintWriter(new OutputStreamWriter(this.clientConnection.getOutputStream()));
            log("I/O set up successfully.");

            output.write("220 Welcome to Jay's FTP Server!\r\n");
            output.flush();
            log("Sent welcome message.");

            String dataFromClient;
            String tempData;
        inputLoop:
            while (true) {
                log("Entered primary input loop");

                //Loop over never ending null chars sent by FTP clients
                while (true) {
                    log("Entered the keep-alive input loop.");
                    dataFromClient = input.readLine();
                    log("Received input from client.");
                    if (!dataFromClient.equals(null)) {
                        break;
                    } else {
                        log("Sleeping for 100 millis");
                        Thread.sleep(100);
                    }
                }
                log(clientSession.getClientName() + ": " + dataFromClient);


                ftpCommand = commandDecoder.decode(dataFromClient);
                ftpCommandAction = ftpCommand.getAction();
                switch (ftpCommandAction) {
                    case USER:  log("Username: " + ftpCommand.getParams()[0]);
                                actionResponse = clientSession.getState().user(clientSession, ftpCommand.getParams()[0]); //Extract username
                                break;
                    case PASS:  log("Password: " + dataFromClient.substring(5, dataFromClient.length()));
                                actionResponse = clientSession.getState().pass(clientSession, ftpCommand.getParams()[0]); //Extract password
                                break;
                    case QUIT:  log(clientSession.getClientName() + " disconnected.");
                                actionResponse = clientSession.getState().quit(clientSession);
                                sendToClient(actionResponse);
                                break inputLoop;
                    case AUTH:  actionResponse = clientSession.getState().auth(clientSession);
                                break;
                    case SYST:  actionResponse = clientSession.getState().syst(clientSession);
                                break;
                    case FEAT:  actionResponse = clientSession.getState().feat(clientSession);
                                break;
                    case PWD:   actionResponse = clientSession.getState().pwd(clientSession);
                                break;
                }

                if (actionResponse.equals(null) || actionResponse.length() == 0) actionResponse = "202 Command not implemented, superfluous at this site.";
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
