package com.jpknox.server.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 18-Oct-17.
 */
public class FTPCommandDecoderTest {

    private FTPCommandDecoder ftpCommandDecoder;

    @Before
    public void setup() {
        ftpCommandDecoder = new FTPCommandDecoder();
    }

    @After
    public void teardown() {
        ftpCommandDecoder = null;
    }

    @Test
    public void testDefaultParams() {
        String[] defaultParams = new String[] {"", "", ""};
        assertArrayEquals(defaultParams, ftpCommandDecoder.defaultParams());
    }

    @Test
    public void testLongCwdWithinQuotes() {
        String rawCommand =
                "CWD \"Folder 1/Subfolder 1/../Subfolder 1/../Subfolder 1/../Subfolder 1/..\"";
        FTPCommand parsedCommand = ftpCommandDecoder.decode(rawCommand);
        assertEquals(FTPCommandAction.CWD, parsedCommand.getAction());
        assertEquals("\"Folder 1/Subfolder 1/../Subfolder 1/../Subfolder 1/../Subfolder 1/..\"",
                parsedCommand.getParams()[0]);
    }

    @Test
    public void testSimpleCwd() {
        String rawCommand = "CWD Folder 1";
        FTPCommand parsedCommand = ftpCommandDecoder.decode(rawCommand);
        assertEquals(FTPCommandAction.CWD, parsedCommand.getAction());
        assertEquals("Folder 1", parsedCommand.getParams()[0]);
    }

}
