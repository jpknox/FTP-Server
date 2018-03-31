package com.jpknox.server.transfer;


import com.jpknox.server.session.ClientSession;
import com.jpknox.server.storage.internaltransfer.FileQueue;
import com.jpknox.server.transfer.connection.InboundConnectionListener;
import com.jpknox.server.transfer.connection.establish.ConnectionQueue;
import com.jpknox.server.transfer.connection.establish.DataPortGenerator;
import com.jpknox.server.transfer.connection.transfer.DataReceiver;
import com.jpknox.server.transfer.connection.transfer.DataSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by João Paulo Knox on 23/12/2017.
 */
public class DataConnectionController {

    private final ClientSession session;
    private final ConnectionQueue connectionQueue = new ConnectionQueue();
    private boolean isListening = false;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public DataConnectionController(ClientSession session) {
        this.session = session;
    }

    public int[] createDataConnectionListener() {
        int port = DataPortGenerator.createPassiveDataPort();
//        log(String.format("Listening for a data connection on port '%d'.", port));
        InboundConnectionListener inboundConnectionListener = new InboundConnectionListener(port, connectionQueue);
        threadPool.execute(inboundConnectionListener);
        isListening = true;
        return DataPortGenerator.encodeDataPort(port);
    }

    public void receive(FileQueue fileQueue, String filename) {
        DataReceiver inboundDataReceiver = new DataReceiver(
                connectionQueue, fileQueue, filename, session.getViewCommunicator());
        Thread dataReceiver = new Thread(inboundDataReceiver);
        dataReceiver.start();
        isListening = false;
    }

    public void send(Object data) {
        DataSender outboundDataSender = new DataSender(connectionQueue, data, session.getViewCommunicator());
        Thread dataSender = new Thread(outboundDataSender);
        dataSender.start();
        isListening = false;
    }

    public boolean isListening() {
        return isListening;
    }
}
