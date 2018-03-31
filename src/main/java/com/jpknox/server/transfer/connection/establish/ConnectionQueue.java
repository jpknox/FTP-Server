package com.jpknox.server.transfer.connection.establish;

import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

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
        log(String.format("Connection queue has a connection on port '%d'", connection.getPort()));
    }

    public synchronized Socket getConnection() {
        if (!isSet) {
            try { wait(); } catch (InterruptedException ie) {}
        }
        isSet = false;
        notify();
        log(String.format("Connection queue has handed over its connection on port '%d'", connection.getPort()));
        return connection;
    }
}
