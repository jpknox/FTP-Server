package com.jpknox.server.transfer.connection;

import com.jpknox.server.transfer.connection.establish.ConnectionQueue;

import java.io.IOException;
import java.net.ServerSocket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 01-Jan-18.
 */
public class InboundConnectionListener implements Runnable {

    private ConnectionQueue connectionQueue;
    private int port;

    public InboundConnectionListener(int port, ConnectionQueue connectionQueue) {
        this.connectionQueue = connectionQueue;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(port);
            log("Listening for a data connection...");
            connectionQueue.setConnection(listener.accept());
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
