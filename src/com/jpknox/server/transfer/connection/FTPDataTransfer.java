package com.jpknox.server.transfer.connection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 26/12/2017
 * Only supports stream transfers.
 */
public class FTPDataTransfer implements DataTransfer, Runnable {

    private int port;
    private byte[] data;
    private ServerSocket connectionListenerSocket;
    private Socket dataConnectionSocket;
    private BufferedOutputStream out;
    private BufferedReader in;

    //Read the file into memory - buffering it in chunks.
    //Write buffered file into the output buffer. Let it automatically flush when it's full.
    //Once the whole file has been written in the output buffer, perform a final explicit flush().

    public FTPDataTransfer(int port, byte[] data) {
        this.port = port;
        this.data = data;
        try {
            connectionListenerSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        listen();
//        send(data);
//        disconnect();
    }

    @Override
    public void listen() {
        try {
            dataConnectionSocket = connectionListenerSocket.accept();
            out = new BufferedOutputStream(dataConnectionSocket.getOutputStream());
            log("Data connection has been established.");
            out.write("Hello, welcome to the data connection.\r\n".getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(byte[] data) {
        String newline = System.getProperty("line.separator");
        try {
            out.write(data);
            out.write(newline.getBytes());
            out.flush();
            log("Data has been transferred.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void transfer() {
        int i = 0;
        while (i < 100) {
            try {
                out.write((byte)i);
                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        send(data);
    }

    @Override
    public void disconnect() {
        try {
            if (dataConnectionSocket != null && dataConnectionSocket.isBound()) {
                out.write("Closing connection\r\n".getBytes());
                out.flush();
                out.close();
                connectionListenerSocket.close();
                dataConnectionSocket.close();
            }
            log("Closed data connection.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
