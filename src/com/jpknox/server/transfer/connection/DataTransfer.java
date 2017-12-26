package com.jpknox.server.transfer.connection;

/**
 * Created by joaok on 24/12/2017.
 */
public interface DataTransfer {

    void listen();

    void send(byte[] data);

    void disconnect();

//    Created, Listening (Blocked), Connected, Transferring, Disconnected, Deleted.

}
