package com.jpknox.server.utility;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 */
public class CommandDecoderIntegrationTest {

    private static final String NEWLINE = System.getProperty("line.separator");
    private CommandDecoder commandDecoder;
    private String telnetCommand;
    private FTPCommand decodedCommand;

    @Before
    public void setup() {
        commandDecoder = new CommandDecoder();
    }

    @After
    public void teardown() {
        commandDecoder = null;
        telnetCommand = null;
        decodedCommand = null;
    }

    @Test
    public void testUser() {
        telnetCommand = "USER anonymous" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.USER, decodedCommand.getAction());
        assertEquals("anonymous", decodedCommand.getParams()[0]);
    }

    @Test
    public void testPass() {
        telnetCommand = "PASS password" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.PASS, decodedCommand.getAction());
        assertEquals("password", decodedCommand.getParams()[0]);
    }

    @Test
    public void testQuit() {
        telnetCommand = "QUIT" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.QUIT, decodedCommand.getAction());
    }

    @Test
    public void testPort() {
        telnetCommand = "PORT 1234" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.PORT, decodedCommand.getAction());
        assertEquals("1234", decodedCommand.getParams()[0]);
    }

    @Test
    public void testTypeAsciiNonPrint() {
        telnetCommand = "TYPE A N" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("A", decodedCommand.getParams()[0]);
        assertEquals("N", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeAsciiTelnetFormatEffectors() {
        telnetCommand = "TYPE A T" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("A", decodedCommand.getParams()[0]);
        assertEquals("T", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeAsciiCarriageControl() {
        telnetCommand = "TYPE A C" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("A", decodedCommand.getParams()[0]);
        assertEquals("C", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeEbcdicNonPrint() {
        telnetCommand = "TYPE E N" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("E", decodedCommand.getParams()[0]);
        assertEquals("N", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeEbcdicTelnetFormatEffectors() {
        telnetCommand = "TYPE E T" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("E", decodedCommand.getParams()[0]);
        assertEquals("T", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeEbcdicCarriageControl() {
        telnetCommand = "TYPE E C" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("E", decodedCommand.getParams()[0]);
        assertEquals("C", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeImage() {
        telnetCommand = "TYPE I" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("I", decodedCommand.getParams()[0]);
    }

    @Test
    public void testTypeLocalByteSize() {
        telnetCommand = "TYPE L 8" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("L", decodedCommand.getParams()[0]);
        assertEquals("8", decodedCommand.getParams()[1]);
    }

    @Test
    public void testModeStream() {
        telnetCommand = "MODE S" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.MODE, decodedCommand.getAction());
        assertEquals("S", decodedCommand.getParams()[0]);
    }

    @Test
    public void testModeBlock() {
        telnetCommand = "MODE B" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.MODE, decodedCommand.getAction());
        assertEquals("B", decodedCommand.getParams()[0]);
    }

    @Test
    public void testModeCompressed() {
        telnetCommand = "MODE C" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.MODE, decodedCommand.getAction());
        assertEquals("C", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStruFile() {
        telnetCommand = "STRU F" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STRU, decodedCommand.getAction());
        assertEquals("F", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStruRecord() {
        telnetCommand = "STRU R" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STRU, decodedCommand.getAction());
        assertEquals("R", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStruPage() {
        telnetCommand = "STRU P" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STRU, decodedCommand.getAction());
        assertEquals("P", decodedCommand.getParams()[0]);
    }

    @Test
    public void testRetr() {
        telnetCommand = "RETR /folder/file.txt" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.RETR, decodedCommand.getAction());
        assertEquals("/folder/file.txt", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStor() {
        telnetCommand = "STOR /folder/file.txt" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STOR, decodedCommand.getAction());
        assertEquals("/folder/file.txt", decodedCommand.getParams()[0]);
    }

    @Test
    public void testNoop() {
        telnetCommand = "NOOP" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.NOOP, decodedCommand.getAction());
    }

    @Test
    public void testAuth() {
        telnetCommand = "AUTH" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.AUTH, decodedCommand.getAction());
    }

    @Test
    public void testSyst() {
        telnetCommand = "SYST" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.SYST, decodedCommand.getAction());
    }

    @Test
    public void testFeat() {
        telnetCommand = "FEAT" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.FEAT, decodedCommand.getAction());
    }

    @Test
    public void testPwd() {
        telnetCommand = "PWD" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.PWD, decodedCommand.getAction());
    }

    @Test
    public void testWhenCommandHasNoParametersItGetsDefaultParameters() {
        telnetCommand = "NOOP" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.NOOP, decodedCommand.getAction());
        assertEquals("", decodedCommand.getParams()[0]);
        assertEquals("", decodedCommand.getParams()[1]);
        assertEquals("", decodedCommand.getParams()[2]);
    }

    @Test
    public void testEmptyCommand() {
        telnetCommand = NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.ERROR, decodedCommand.getAction());
        assertEquals("", decodedCommand.getParams()[0]);
        assertEquals("", decodedCommand.getParams()[1]);
        assertEquals("", decodedCommand.getParams()[2]);
    }

    @Test
    public void testShortCommand() {
        telnetCommand = "U" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.ERROR, decodedCommand.getAction());
        assertEquals("", decodedCommand.getParams()[0]);
        assertEquals("", decodedCommand.getParams()[1]);
        assertEquals("", decodedCommand.getParams()[2]);
    }

    @Test
    public void testLongCommand() {
        telnetCommand = "USERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR" + NEWLINE;
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.ERROR, decodedCommand.getAction());
        assertEquals("", decodedCommand.getParams()[0]);
        assertEquals("", decodedCommand.getParams()[1]);
        assertEquals("", decodedCommand.getParams()[2]);
    }


}
