package com.jpknox.server.transfer;


import com.jpknox.server.transfer.exception.IllegalPortException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by joaok on 23/12/2017.
 */
public class DataTransferController {

    public static final int LOWER_PORT_BOUNDARY = 50000;
    public static final int UPPER_PORT_BOUNDARY = 65535;

    private int dataPort = -1;

    private ServerSocket dataConnection;

    private boolean transferring = false;

    public int[] listen() {
        generatePassiveDataPort();
        try {
            dataConnection = new ServerSocket(dataPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getEncodedDataPort();
    }

    //TODO: Make this private and amend the tests
    public int generatePassiveDataPort() {
        dataPort = java.util.concurrent.ThreadLocalRandom.current().nextInt(LOWER_PORT_BOUNDARY, UPPER_PORT_BOUNDARY+1);
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
