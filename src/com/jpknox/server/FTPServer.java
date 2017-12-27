package com.jpknox.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 24/09/2017.
 */
public class FTPServer {
    private ServerSocket serverSocket;
    private Socket clientConnection;
    private ControlConnectionController[] controlConnectionControllers = new ControlConnectionController[10];
    private int controllerIndex = 0;

    public FTPServer(ServerSocket serverSocket) {
        log("server starting up.");
        try {
            this.serverSocket = serverSocket;
            log("waiting for client.");
            this.clientConnection = this.serverSocket.accept();
            ControlConnectionController controlConnectionController = new ControlConnectionController(this.clientConnection);
            controlConnectionController.start();
            controlConnectionControllers[controllerIndex++] = controlConnectionController;
            log("client connection established.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("server shutting down.");
    }

    public ControlConnectionController getClientSessionController(int id) {
        return controlConnectionControllers[id];
    }
}
