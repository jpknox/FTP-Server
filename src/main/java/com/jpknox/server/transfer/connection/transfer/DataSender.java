package com.jpknox.server.transfer.connection.transfer;

import com.jpknox.server.response.ClientViewCommunicator;
import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.transfer.connection.establish.ConnectionQueue;

import java.io.*;
import java.net.Socket;

/**
 * Created by JoaoPaulo on 01-Jan-18.
 */
public class DataSender implements Runnable {

    private ConnectionQueue connectionQueue;
    private String data;
    private ClientViewCommunicator clientViewCommunicator;

    public DataSender(ConnectionQueue connectionQueue, String data, ClientViewCommunicator clientViewCommunicator) {
        this.connectionQueue = connectionQueue;
        this.data = data;
        this.clientViewCommunicator = clientViewCommunicator;
    }


    @Override
    public void run() {
        try {
            Socket connection = connectionQueue.getConnection();
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
            byte[] bytes = data.getBytes();
            ByteArrayInputStream isr = new ByteArrayInputStream(bytes);
            for (int c; (c = isr.read()) != -1; bos.write(c));
            bos.close();
            isr.close();
            connection.close();
            clientViewCommunicator.write(FTPResponseFactory.createResponse(226));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
