package com.jpknox.server.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 * Note - I wanted to mock out the action & param dependencies
 *          but Mockito wouldn't allow it because they're primitive types.
 */
public class FTPCommandTest {

    private FTPCommand command;
    private FTPCommandAction ftpCommandAction;
    private String[] ftpCommandParams;

    @Before
    public void setup() {
        ftpCommandParams = new String[0];
    }

    @After
    public void teardown() {
        command = null;
        ftpCommandAction = null;
        ftpCommandParams = null;
    }

    @Test
    public void testCommand() {
        ftpCommandAction = FTPCommandAction.USER;
        command = new FTPCommand(ftpCommandAction, ftpCommandParams);
        assertTrue(command != null);
        assertTrue(command.getAction() != null);
        assertTrue(command.getParams() != null);
    }

}
