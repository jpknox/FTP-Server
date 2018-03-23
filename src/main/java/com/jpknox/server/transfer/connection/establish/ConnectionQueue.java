package com.jpknox.server.transfer.connection.establish;

import java.net.Socket;

/**
 * Created by JoaoPaulo on 01-Jan-18.
 */
public class ConnectionQueue {

    private Socket connection;
    private boolean isSet = false;

    public synchronized void setConnection(Socket connection) {
        if (isSet) {
            try { wait(); } catch (InterruptedException ie) {}
        }
        this.connection = connection;
        isSet = true;
        notify();
        System.out.printf("Connection queue has a connection on port '%d'\n", connection.getPort());
    }

    public synchronized Socket getConnection() {
        if (!isSet) {
            try { wait(); } catch (InterruptedException ie) {}
        }
        isSet = false;
        notify();
        System.out.printf("Connection queue has handed over its connection on port '%d'\n", connection.getPort());
        return connection;
    }
}
