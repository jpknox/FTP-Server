package com.jpknox.server.utility;

import org.junit.Test;

import static com.jpknox.server.utility.Logger.log;
import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 */
public class LoggerTest {

    @Test
    public void testStaticLog() {
        int returnValue = log("some log data");
        assertEquals(0, returnValue);
    }

}
