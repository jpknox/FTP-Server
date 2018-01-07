package com.jpknox.server.transfer.connection.transfer;

import com.jpknox.server.response.ClientViewCommunicator;
import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.storage.internaltransfer.FileQueue;
import com.jpknox.server.transfer.connection.establish.ConnectionQueue;

import java.io.*;
import java.net.Socket;

/**
 * Created by JoaoPaulo on 01-Jan-18.
 */
public class DataReceiver implements Runnable{

    private ConnectionQueue connectionQueue;
    private FileQueue fileQueue;
    private String filename;
    private ClientViewCommunicator clientViewCommunicator;
    private FTPResponseFactory ftpResponseFactory = new FTPResponseFactory();

    public DataReceiver(ConnectionQueue connectionQueue, FileQueue fileQueue
            , String filename, ClientViewCommunicator clientViewCommunicator) {
        this.connectionQueue = connectionQueue;
        this.fileQueue = fileQueue;
        this.filename = filename;
        this.clientViewCommunicator = clientViewCommunicator;
    }

    @Override
    public void run() {
        System.out.println("Receiving " + filename);
        BufferedInputStream bis;
        BufferedOutputStream bos = null;
        try {
            Socket connection = connectionQueue.getConnection();
            bis = new BufferedInputStream(connection.getInputStream());
            File tempTransferFile = new File(System.getProperty("java.io.tmpdir")
                                                + System.getProperty("file.separator")
                                                + filename);
            bos = new BufferedOutputStream(new FileOutputStream(tempTransferFile));
            for (int i; (i = bis.read()) != -1; bos.write(i));
            bos.close();
            bis.close();
            connection.close();
            clientViewCommunicator.write(ftpResponseFactory.createResponse(226));
            System.out.println("Finished recieving " + filename + ", size " + tempTransferFile.length());
            fileQueue.setFile(tempTransferFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
