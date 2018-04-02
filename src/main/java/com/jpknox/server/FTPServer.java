package com.jpknox.server;

import com.jpknox.server.control.ControlConnectionController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by João Paulo Knox on 24/09/2017.
 */
public class FTPServer {

    public static final int STANDARD_CONTROL_PORT = 21;
    private ArrayList<ControlConnectionController> allControllers = new ArrayList();
    private int controllerIndex = 0;
    private int clientCount = 0;
    private boolean testOverride = false;
    private ControlConnectionController newController;

    public FTPServer() {
        try {
            log("Server is starting up.");
            ServerSocket serverSocket = new ServerSocket(STANDARD_CONTROL_PORT);
            serverSocket.setReuseAddress(true);
            while (true && !testOverride) {
                log("Waiting for client...");
                Socket clientConnection = serverSocket.accept();
                log(String.format("Controller number '%d' has a new connection on port '%d'.",
                        ++controllerIndex, clientConnection.getPort()));
                newController = new ControlConnectionController(clientConnection);
                allControllers.add(newController);
                Executors.callable(newController).call();
                log("Client " + clientCount + " connection established.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("An IOException has occurred. Server shutting down.");
        } catch (Exception e) {
            e.printStackTrace();
            log("An Exception related to the callable ControlConnectionController has occurred. Server shutting down.");
        }
        log("server shutting down.");
    }

    public ControlConnectionController getClientSessionController(int id) {
        return allControllers.get(id);
    }

    public void setTestOverride(boolean testOverride) {
        this.testOverride = testOverride;
    }
}
