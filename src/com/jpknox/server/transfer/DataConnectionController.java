package com.jpknox.server.transfer;


import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.transfer.connection.FTPDataTransfer;
import com.jpknox.server.transfer.exception.IllegalPortException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 23/12/2017.
 */
public class DataConnectionController {

    public static final int LOWER_PORT_BOUNDARY = 50000;
    public static final int UPPER_PORT_BOUNDARY = 65535;

    private final ClientSession session;
    private final FTPResponseFactory responseFactory = new FTPResponseFactory();
    private final FtpDataTransferFactory ftpDataTransferFactory = new FtpDataTransferFactory();
    private int dataPort = -1;
    private FTPDataTransfer dataTransfer;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    private boolean transferring = false;

    public DataConnectionController(ClientSession session) {
        this.session = session;
    }

    public int[] createDataConnection() {
        if (dataTransfer != null) {
            dataTransfer.disconnect();
            dataTransfer = null;
        }
        //TODO: Build response here and return as a string. To keep with convention.
        generatePassiveDataPort();
        dataTransfer = new FTPDataTransfer(getDataPort());
        executorService.submit(dataTransfer);
        log("Opening new port: " + getDataPort());
        return getEncodedDataPort();
    }

    public void send(String data) {
        if (dataTransfer != null) {
            if (dataTransfer.isConnected()) {
                session.getViewCommunicator().write(responseFactory.createResponse(150));
                dataTransfer.assign(data.getBytes());
                session.getViewCommunicator().write(responseFactory.createResponse(226));
            } else if (dataTransfer.isListening()){
                dataTransfer.assign(data.getBytes());
            }
        } else {
            session.getViewCommunicator().write(responseFactory.createResponse(425));
        }
    }

    public int generatePassiveDataPort() {
        try {
            setDataPort(java.util.concurrent.ThreadLocalRandom.current().nextInt(LOWER_PORT_BOUNDARY, UPPER_PORT_BOUNDARY+1));
        } catch (IllegalPortException e) {
            log("An IllegalPortException was thrown when generating a new passive data port.");
            e.printStackTrace();
            return -1;
        }
        return dataPort;
    }


    public int[] getEncodedDataPort() {
        // p1 = PPrime/256
        //  PPrime = (port - p2)
        if (dataPort == -1) return null;
        int port = dataPort;

        int p2 = port % 256;
        int p1 = (port - p2) / 256;
        //System.out.printf("p2: %d\n", p2);
        //System.out.printf("p1: %d\n", p1);
        int[] output = {p1, p2};
        return output;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int port) throws IllegalPortException {
        if (LOWER_PORT_BOUNDARY > port || port > UPPER_PORT_BOUNDARY) {
            throw new IllegalPortException(port);
        } else {
            this.dataPort = port;
        }
    }
}
