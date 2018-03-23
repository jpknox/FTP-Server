package com.jpknox.server;

import com.jpknox.server.control.ControlConnectionController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 24/09/2017.
 */
public class FTPServer {

    public static final int STANDARD_CONTROL_PORT = 21;
    public static final int MAXIMUM_INSTANCES = 10;
    private ControlConnectionController[] controlConnectionControllers = new ControlConnectionController[MAXIMUM_INSTANCES];
    private int controllerIndex = 0;
    private int clientCount = 0;
    private final ExecutorService pool = Executors.newFixedThreadPool(MAXIMUM_INSTANCES);

    public FTPServer() {
        try {
            log("Server is starting up.");
            ServerSocket serverSocket = new ServerSocket(STANDARD_CONTROL_PORT);
            serverSocket.setReuseAddress(true);
            while (true) {
                if (controllerIndex == MAXIMUM_INSTANCES-1) {
                    log("Maximum number of instances has been reached.");
                    break;
                }
                controllerIndex++;
                log("Waiting for client...");
                Socket clientConnection = serverSocket.accept();
                log(String.format("Controller number '%d' has a new connection on port '%d'.",
                        controllerIndex, clientConnection.getPort()));
                pool.execute(
                        new ControlConnectionController(clientConnection));

                clientCount = controllerIndex;
                log("Client " + clientCount + " connection established.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("An error has occurred; server shutting down.");
        }
        log("server shutting down.");
    }

    public FTPServer(ServerSocket serverSocket) {

    }

    public ControlConnectionController getClientSessionController(int id) {
        return controlConnectionControllers[id];
    }
}
