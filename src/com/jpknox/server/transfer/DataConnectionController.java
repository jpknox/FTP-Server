package com.jpknox.server.transfer;


import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.storage.internaltransfer.FileQueue;
import com.jpknox.server.transfer.connection.*;
import com.jpknox.server.transfer.connection.establish.ConnectionQueue;
import com.jpknox.server.transfer.connection.establish.DataPortGenerator;
import com.jpknox.server.transfer.connection.transfer.DataReceiver;
import com.jpknox.server.transfer.connection.transfer.DataSender;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 23/12/2017.
 */
public class DataConnectionController {

    private final ClientSession session;
    private final FTPResponseFactory responseFactory = new FTPResponseFactory();
    private final ConnectionQueue connectionQueue = new ConnectionQueue();
    private boolean isListening = false;

    public DataConnectionController(ClientSession session) {
        this.session = session;
    }

    public int[] createDataConnectionListener() {
        int port = DataPortGenerator.createPassiveDataPort();
        InboundConnectionListener inboundConnectionListener = new InboundConnectionListener(port, connectionQueue);
        Thread connectionListener = new Thread(inboundConnectionListener);
        connectionListener.start();
        isListening = true;
        log("Opening new port: " + port);
        return DataPortGenerator.encodeDataPort(port);
    }

    public void receive(FileQueue fileQueue, String filename) {
        DataReceiver inboundDataReceiver = new DataReceiver(
                connectionQueue, fileQueue, filename, session.getViewCommunicator());
        Thread dataReceiver = new Thread(inboundDataReceiver);
        dataReceiver.start();
        isListening = false;
    }

    public void send(String data) {
        DataSender outboundDataSender = new DataSender(connectionQueue, data, session.getViewCommunicator());
        Thread dataSender = new Thread(outboundDataSender);
        dataSender.start();
        isListening = false;
    }

    public boolean isListening() {
        return isListening;
    }
}
