package com.jpknox.server.transfer.connection;

/**
 * Created by joaok on 24/12/2017.
 */
public interface DataTransfer {

    void listen();

    void disconnect();

    boolean isListening();

    boolean isConnected();

    void assign(byte[] bytes);

//    Created, Listening (Blocked), Connected, Transferring, Disconnected, Deleted.

}
