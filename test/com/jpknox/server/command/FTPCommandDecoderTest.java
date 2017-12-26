package com.jpknox.server.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by JoaoPaulo on 18-Oct-17.
 */
public class FTPCommandDecoderTest {

    private FTPCommandDecoder FTPCommandDecoder;

    @Before
    public void setup() {
        FTPCommandDecoder = new FTPCommandDecoder();
    }

    @After
    public void teardown() {
        FTPCommandDecoder = null;
    }

    @Test
    public void testDefaultParams() {
        String[] defaultParams = new String[] {"", "", ""};
        assertArrayEquals(defaultParams, FTPCommandDecoder.defaultParams());
    }

}
