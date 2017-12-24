package com.jpknox.server.utility;

import com.jpknox.server.command.CommandDecoder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by JoaoPaulo on 18-Oct-17.
 */
public class CommandDecoderTest {

    private CommandDecoder commandDecoder;

    @Before
    public void setup() {
        commandDecoder = new CommandDecoder();
    }

    @After
    public void teardown() {
        commandDecoder = null;
    }

    @Test
    public void testDefaultParams() {
        String[] defaultParams = new String[] {"", "", ""};
        assertArrayEquals(defaultParams, commandDecoder.defaultParams());
    }

}
