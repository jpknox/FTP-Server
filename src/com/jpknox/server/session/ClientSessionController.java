package com.jpknox.server.session;

import com.jpknox.server.FileManager;

import java.io.*;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 14-Oct-17.
 */
public class ClientSessionController {

    private BufferedReader input;
    private PrintWriter output;
    private ClientSession clientSession;
    private Socket clientConnection;
    private String actionResponse = "";

    public ClientSessionController(Socket clientConnection) {
        this.clientSession = new ClientSession();
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
//            String dataToClient = null;
            String tempData;
            while (true) {
                log("Entered primary input loop");

                //Loop over never ending null chars sent by FTP clients
                while (true) {
                    log("Entered the keep-connection-alive loop.");
                    dataFromClient = (tempData = input.readLine()) == null ? "Null char" : tempData;
                    log("Received input from client.");
                    if (!dataFromClient.equals("Null char")) {
                        break;
                    } else {
                        log("Sleeping for 100 millis");
                        Thread.sleep(100);
                    }
                }
                log(clientSession.getClientName() + ": " + dataFromClient);
//				dataToClient = "Echo: " + dataFromClient;
//				output.write(dataToClient + "\r\n");
//				output.flush();

                if (dataFromClient.length() > 5 && dataFromClient.substring(0, 5).toUpperCase().equals("USER ")) {
					log("Username: " + dataFromClient.substring(5, dataFromClient.length()));
                    actionResponse = clientSession.getState().user(clientSession, dataFromClient.substring(5, dataFromClient.length())); //Extract username
                }
                if (dataFromClient.length() > 5 && dataFromClient.substring(0, 5).toUpperCase().equals("PASS ")) {
					log("Username: " + dataFromClient.substring(5, dataFromClient.length()));
                    actionResponse = clientSession.getState().pass(clientSession, dataFromClient.substring(5, dataFromClient.length())); //Extract username
                }
                if (dataFromClient.equals("quit")) {
                    actionResponse = clientSession.getState().quit(clientSession);
                    log(clientSession.getClientName() + " disconnected.");
                    sendToClient(actionResponse);
                    break;
                }
                if (dataFromClient.length() > 3) {
                    if (dataFromClient.toUpperCase().substring(0, 4).equals("AUTH")) {
                        actionResponse = clientSession.getState().auth(clientSession);
                    }
                    if (dataFromClient.toUpperCase().substring(0, 4).equals("SYST")) {
                        actionResponse = clientSession.getState().syst(clientSession);
                    }
                    if (dataFromClient.toUpperCase().substring(0, 4).equals("FEAT")) {
                        actionResponse = clientSession.getState().feat(clientSession);
                    }
                } else {
                    if (dataFromClient.toUpperCase().substring(0, 3).equals("PWD")) {
                        actionResponse = clientSession.getState().pwd(clientSession);
                    }
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
