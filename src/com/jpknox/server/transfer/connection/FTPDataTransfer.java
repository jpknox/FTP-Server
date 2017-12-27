package com.jpknox.server.transfer.connection;

import com.jpknox.server.response.FTPResponseFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 26/12/2017
 * Only supports stream transfers.
 */
public class FTPDataTransfer implements DataTransfer, Runnable {

    private byte[] data = new byte[]{};
    private final FTPResponseFactory ftpResponseFactory = new FTPResponseFactory();
    private ServerSocket connectionListenerSocket;
    private Socket dataConnectionSocket;
    private BufferedOutputStream out;
    private BufferedReader in;
    private boolean listening = false;

    //Read the file into memory - buffering it in chunks.
    //Write buffered file into the output buffer. Let it automatically flush when it's full.
    //Once the whole file has been written in the output buffer, perform a final explicit flush().

    public FTPDataTransfer(int port) {
        try {
            connectionListenerSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        listen();
        while (data.length == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        send(data);
        disconnect();
    }

    @Override
    public void listen() {
        try {
            listening = true;
            dataConnectionSocket = connectionListenerSocket.accept();
            listening = false;
            out = new BufferedOutputStream(dataConnectionSocket.getOutputStream());
            log("Data connection has been established.");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(byte[] data) {
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

    @Override
    public boolean isListening() {
        return listening;
    }

    @Override
    public boolean isConnected() {
        return !dataConnectionSocket.isClosed();
    }

    @Override
    public void assign(byte[] bytes) {
        this.data = bytes;
    }
}
