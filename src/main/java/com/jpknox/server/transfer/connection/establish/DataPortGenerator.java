package com.jpknox.server.transfer.connection.establish;

import com.jpknox.server.transfer.exception.IllegalPortException;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 31-Dec-17.
 */
public class DataPortGenerator {

    public static final int LOWER_PORT_BOUNDARY = 50000;
    public static final int UPPER_PORT_BOUNDARY = 65535;

    public static int createPassiveDataPort() {
        int port = java.util.concurrent.ThreadLocalRandom.current()
                .nextInt(LOWER_PORT_BOUNDARY, UPPER_PORT_BOUNDARY+1);
        return port;
    }


    public static int[] encodeDataPort(int port) {
        // p1 = PPrime/256
        //  PPrime = (port - p2)

        int p2 = port % 256;
        int p1 = (port - p2) / 256;
        //System.out.printf("p2: %d\n", p2);
        //System.out.printf("p1: %d\n", p1);
        int[] output = {p1, p2};
        return output;
    }
}
