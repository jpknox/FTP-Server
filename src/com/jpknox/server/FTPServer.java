package com.jpknox.server;

import com.jpknox.server.state.StateNotLoggedIn;
import com.jpknox.server.state.SessionState;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by joaok on 24/09/2017.
 */
public class FTPServer {

    private SessionState currentState;
    private ServerSocket serverSocket;
    private Socket clientConnection;
    private BufferedReader input;
    private PrintWriter output;
    private String clientName = "client";
    private FileManager fileManager = FileManager.getInstance();

    public FTPServer(ServerSocket serverSocket) {
        this.currentState = new StateNotLoggedIn();
        log("server starting up.");
        try {
            this.serverSocket = serverSocket;
            log("waiting for client.");
            this.clientConnection = this.serverSocket.accept();
            log("client connection established.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Setting up I/O.");
            this.input = new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream()));
            this.output = new PrintWriter(new OutputStreamWriter(this.clientConnection.getOutputStream()));
            System.out.println("I/O set up successfully.");

            output.write("220 Welcome to Jay's FTP Server!\r\n");
            output.flush();
            System.out.println("Sent welcome message.");

            String dataFromClient = null;
            String dataToClient = null;
            String tempData;
            while (true) {
                System.out.println("Entered primary input loop");

                //Loop over never ending null chars sent by FTP clients
                while (true) {
                    System.out.println("Entered the keep-connection-alive loop.");
                    dataFromClient = (tempData = input.readLine()) == null ? "Null char" : tempData;
                    System.out.println("Received input from client.");
                    if (!dataFromClient.equals("Null char")) {
                        break;
                    } else {
                        System.out.println("Sleeping for 100 millis");
                        Thread.sleep(100);
                    }
                }
                log(clientName + ": " + dataFromClient);
//				dataToClient = "Echo: " + dataFromClient;
//				output.write(dataToClient + "\r\n");
//				output.flush();

                if (dataFromClient.length() > 5 && dataFromClient.substring(0, 5).toUpperCase().equals("USER ")) {
//					log("Username: " + dataFromClient.substring(5, dataFromClient.length()));
                    currentState.user(this, dataFromClient.substring(5, dataFromClient.length())); //Extract username
                }
                if (dataFromClient.length() > 5 && dataFromClient.substring(0, 5).toUpperCase().equals("PASS ")) {
//					log("Username: " + dataFromClient.substring(5, dataFromClient.length()));
                    currentState.pass(this, dataFromClient.substring(5, dataFromClient.length())); //Extract username
                }
                if (dataFromClient.equals("quit")) {
                    log(clientName + " disconnected.");
                    break;
                }
                if (dataFromClient.length() > 3) {
                    if (dataFromClient.toUpperCase().substring(0, 4).equals("AUTH")) {
                        currentState.auth(this);
                    }
                    if (dataFromClient.toUpperCase().substring(0, 4).equals("SYST")) {
                        currentState.syst(this);
                    }
                    if (dataFromClient.toUpperCase().substring(0, 4).equals("FEAT")) {
                        currentState.feat(this);
                    }
                } else {
                    if (dataFromClient.toUpperCase().substring(0, 3).equals("PWD")) {
                        currentState.pwd(this);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("server shutting down.");
    }

    public int setState(SessionState nextState) {
        log("switch from: " + this.currentState.getClass().getSimpleName() + " to " + nextState.getClass().getSimpleName() + ".");
        this.currentState = nextState;
        return 0;
    }

    public String getState() {
        return this.currentState.getClass().getSimpleName();
    }

    public int log(String text) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + ":\t" + text);
        return 0;
    }

    public int sendToClient(String text) {
        output.write(text + "\r\n");
        output.flush();
        return 0;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
