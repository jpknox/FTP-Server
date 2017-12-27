package com.jpknox.server.transfer;

import com.jpknox.server.session.ClientSession;
import com.jpknox.server.transfer.exception.IllegalPortException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by joaok on 23/12/2017.
 */
public class DataConnectionControllerTest {

    private DataConnectionController dataConnectionController;

    @Before
    public void setup() {
        ClientSession clientSession = Mockito.mock(ClientSession.class);
        dataConnectionController = new DataConnectionController(clientSession);
    }

    @After
    public void teardown() {
        dataConnectionController = null;
    }

    @Test
    public void testGeneratePassivePortOnce() {
        int max = dataConnectionController.UPPER_PORT_BOUNDARY;
        int min = dataConnectionController.LOWER_PORT_BOUNDARY;
        int port = dataConnectionController.generatePassiveDataPort();
        assertTrue(min <= port);
        assertTrue(port <= max);
    }

    @Test
    public void testGeneratePassivePortThrice() {
        int max = dataConnectionController.UPPER_PORT_BOUNDARY;
        int min = dataConnectionController.LOWER_PORT_BOUNDARY;
        int port0 = dataConnectionController.generatePassiveDataPort();
        int port1 = dataConnectionController.generatePassiveDataPort();
        int port2 = dataConnectionController.generatePassiveDataPort();

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
        dataConnectionController.setDataPort(dataConnectionController.LOWER_PORT_BOUNDARY-1);
    }

    @Test(expected = IllegalPortException.class)
    public void testSetPortTooHigh() throws IllegalPortException {
        dataConnectionController.setDataPort(dataConnectionController.UPPER_PORT_BOUNDARY+1);
    }

    @Test
    public void testSetPortValid() throws IllegalPortException {
        int port = dataConnectionController.LOWER_PORT_BOUNDARY+1;
        dataConnectionController.setDataPort(port);
        assertEquals(port, dataConnectionController.getDataPort());
    }

    @Test
    public void testgetEncodedDataPort51000() throws IllegalPortException {
        dataConnectionController.setDataPort(51000);
        int[] port = dataConnectionController.getEncodedDataPort();
        assertEquals(port[0], 199);
        assertEquals(port[1], 56);
    }

    @Test
    public void testgetEncodedDataPort50000() throws IllegalPortException {
        dataConnectionController.setDataPort(50000);
        int[] port = dataConnectionController.getEncodedDataPort();
        assertEquals(port[0], 195);
        assertEquals(port[1], 80);
    }

    @Test
    public void testgetEncodedDataPort65535() throws IllegalPortException {
        dataConnectionController.setDataPort(65535);
        int[] port = dataConnectionController.getEncodedDataPort();
        assertEquals(port[0], 255);
        assertEquals(port[1], 255);
    }

    @Test
    public void testgetEncodedDataPort57768() throws IllegalPortException {
        dataConnectionController.setDataPort(57768);
        int[] port = dataConnectionController.getEncodedDataPort();
        assertEquals(port[0], 225);
        assertEquals(port[1], 168);
    }

}
