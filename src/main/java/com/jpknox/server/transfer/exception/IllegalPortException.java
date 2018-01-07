package com.jpknox.server.transfer.exception;

/**
 * Created by joaok on 23/12/2017.
 */
public class IllegalPortException extends Exception {

    private int port;

    public IllegalPortException(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

}
