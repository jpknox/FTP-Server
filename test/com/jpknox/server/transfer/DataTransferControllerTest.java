package com.jpknox.server.transfer;

import com.jpknox.server.transfer.exception.IllegalPortException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by joaok on 23/12/2017.
 */
public class DataTransferControllerTest {

    private DataTransferController dataTransferController;

    @Before
    public void setup() {
        dataTransferController = new DataTransferController();
    }

    @After
    public void teardown() {
        dataTransferController = null;
    }

    @Test
    public void testGeneratePassivePortOnce() {
        int max = dataTransferController.UPPER_PORT_BOUNDARY;
        int min = dataTransferController.LOWER_PORT_BOUNDARY;
        int port = dataTransferController.generatePassiveDataPort();
        assertTrue(min <= port);
        assertTrue(port <= max);
    }

    @Test
    public void testGeneratePassivePortThrice() {
        int max = dataTransferController.UPPER_PORT_BOUNDARY;
        int min = dataTransferController.LOWER_PORT_BOUNDARY;
        int port0 = dataTransferController.generatePassiveDataPort();
        int port1 = dataTransferController.generatePassiveDataPort();
        int port2 = dataTransferController.generatePassiveDataPort();

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

    @Test(expected = IllegalPortException.class)
    public void testSetPortTooLow() throws IllegalPortException {
        dataTransferController.setDataPort(dataTransferController.LOWER_PORT_BOUNDARY-1);
    }

    @Test(expected = IllegalPortException.class)
    public void testSetPortTooHigh() throws IllegalPortException {
        dataTransferController.setDataPort(dataTransferController.UPPER_PORT_BOUNDARY+1);
    }

    @Test
    public void testSetPortValid() throws IllegalPortException {
        int port = dataTransferController.LOWER_PORT_BOUNDARY+1;
        dataTransferController.setDataPort(port);
        assertEquals(port, dataTransferController.getDataPort());
    }

    @Test
    public void testConvertDataPort51000() throws IllegalPortException {
        dataTransferController.setDataPort(51000);
        int[] port = dataTransferController.convertDataPort();
        assertEquals(port[0], 199);
        assertEquals(port[1], 56);
    }

    @Test
    public void testConvertDataPort50000() throws IllegalPortException {
        dataTransferController.setDataPort(50000);
        int[] port = dataTransferController.convertDataPort();
        assertEquals(port[0], 195);
        assertEquals(port[1], 80);
    }

    @Test
    public void testConvertDataPort65535() throws IllegalPortException {
        dataTransferController.setDataPort(65535);
        int[] port = dataTransferController.convertDataPort();
        assertEquals(port[0], 255);
        assertEquals(port[1], 255);
    }

    @Test
    public void testConvertDataPort57768() throws IllegalPortException {
        dataTransferController.setDataPort(57768);
        int[] port = dataTransferController.convertDataPort();
        assertEquals(port[0], 225);
        assertEquals(port[1], 168);
    }

}
