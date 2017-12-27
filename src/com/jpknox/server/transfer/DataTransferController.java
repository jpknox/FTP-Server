package com.jpknox.server.transfer;


import com.jpknox.server.response.ControlConnectionCommunicator;
import com.jpknox.server.transfer.connection.FTPDataTransfer;
import com.jpknox.server.transfer.exception.IllegalPortException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by joaok on 23/12/2017.
 */
public class DataTransferController {

    public static final int LOWER_PORT_BOUNDARY = 50000;
    public static final int UPPER_PORT_BOUNDARY = 65535;

    private final ControlConnectionCommunicator controlConnectionCommunicator;
    private int dataPort = -1;
    private int dataConnectionCount = 0;
    private FTPDataTransfer dataTransfer;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    private boolean transferring = false;

    public DataTransferController(ControlConnectionCommunicator controlConnectionCommunicator) {
        this.controlConnectionCommunicator = controlConnectionCommunicator;
    }

    public int[] listen() {
        if (dataTransfer != null) {
            dataTransfer.disconnect();
            dataTransfer = null;
        }
        generatePassiveDataPort();
        dataTransfer = new FTPDataTransfer(getDataPort(), ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n".toLowerCase()).getBytes());
        executorService.submit(dataTransfer);
        log("Opening new port: " + getDataPort());
        return getEncodedDataPort();
    }

    public void send(String data) {
        dataTransfer.send(data.getBytes());
    }

    //TODO: Make this private and amend the tests
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
