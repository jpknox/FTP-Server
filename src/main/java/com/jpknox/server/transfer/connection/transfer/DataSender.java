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
    private Object data;
    private ClientViewCommunicator clientViewCommunicator;

    public DataSender(ConnectionQueue connectionQueue, Object data, ClientViewCommunicator clientViewCommunicator) {
        this.connectionQueue = connectionQueue;
        this.data = data;
        this.clientViewCommunicator = clientViewCommunicator;
    }


    @Override
    public void run() {
        if (data.getClass() == File.class) {
            try ( BufferedInputStream bis =
                          new BufferedInputStream(new FileInputStream((File)data));
                  BufferedOutputStream bos =
                          new BufferedOutputStream(connectionQueue.getConnection().getOutputStream())) {
                for (int c; (c = bis.read()) != -1; bos.write(c));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (data.getClass() == String.class) {
            byte[] bytes = ((String) data).getBytes();
            try (Socket connection =
                         connectionQueue.getConnection();
                 BufferedOutputStream bos =
                         new BufferedOutputStream(connection.getOutputStream());
                 ByteArrayInputStream isr =
                         new ByteArrayInputStream(bytes)) {

                for (int c;
                     (c = isr.read()) != -1;
                     bos.write(c))
                    ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //TODO: Transfer this bit of logic generating the response, into the calling state.
        clientViewCommunicator.write(FTPResponseFactory.createResponse(226));
    }
}
