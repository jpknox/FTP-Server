package com.jpknox.server.transfer;

import com.jpknox.server.transfer.connection.establish.DataPortGenerator;
import com.jpknox.server.transfer.exception.IllegalPortException;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by joaok on 23/12/2017.
 */
public class DataPortGeneratorTest {

    @Test
    public void testGeneratePassivePortOnce() {
        int max = DataPortGenerator.UPPER_PORT_BOUNDARY;
        int min = DataPortGenerator.LOWER_PORT_BOUNDARY;
        int port = DataPortGenerator.createPassiveDataPort();
        assertTrue(min <= port);
        assertTrue(port <= max);
    }

    @Test
    public void testGeneratePassivePortThrice() {
        int max = DataPortGenerator.UPPER_PORT_BOUNDARY;
        int min = DataPortGenerator.LOWER_PORT_BOUNDARY;
        int port0 = DataPortGenerator.createPassiveDataPort();
        int port1 = DataPortGenerator.createPassiveDataPort();
        int port2 = DataPortGenerator.createPassiveDataPort();

        //Assert none of the ports are identical
        int[] ports = {port0, port1, port2};
        for (int i = 0; i < ports.length; i++) {
            assertTrue(min <= ports[i]);
            assertTrue(ports[i] <= max);
            for (int k = 0; k < ports.length; k++) {
                if (i == k) continue;
                assertFalse(ports[i] == ports[k]);
            }
        }
    }

    @Test
    public void testgetEncodedDataPort51000() throws IllegalPortException {
        int port = 51000;
        int[] enodedPort = DataPortGenerator.encodeDataPort(port);
        assertEquals(enodedPort[0], 199);
        assertEquals(enodedPort[1], 56);
    }

    @Test
    public void testgetEncodedDataPort50000() throws IllegalPortException {
        int port = 50000;
        int[] encodedPort = DataPortGenerator.encodeDataPort(port);
        assertEquals(encodedPort[0], 195);
        assertEquals(encodedPort[1], 80);
    }

    @Test
    public void testgetEncodedDataPort65535() throws IllegalPortException {
        int port = 65535;
        int[] encodedPort = DataPortGenerator.encodeDataPort(port);
        assertEquals(encodedPort[0], 255);
        assertEquals(encodedPort[1], 255);
    }

    @Test
    public void testgetEncodedDataPort57768() throws IllegalPortException {
        int port = 57768;
        int[] encodedPort = DataPortGenerator.encodeDataPort(port);
        assertEquals(encodedPort[0], 225);
        assertEquals(encodedPort[1], 168);
    }

}
