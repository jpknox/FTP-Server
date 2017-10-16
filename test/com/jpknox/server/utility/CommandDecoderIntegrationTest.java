package com.jpknox.server.utility;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 */
public class CommandDecoderIntegrationTest {

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
        telnetCommand = "USER anonymous";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.USER, decodedCommand.getAction());
        assertEquals("anonymous", decodedCommand.getParams()[0]);
    }

    @Test
    public void testPass() {
        telnetCommand = "PASS password";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.PASS, decodedCommand.getAction());
        assertEquals("password", decodedCommand.getParams()[0]);
    }

    @Test
    public void testQuit() {
        telnetCommand = "QUIT";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.QUIT, decodedCommand.getAction());
    }

    @Test
    public void testPort() {
        telnetCommand = "PORT 1234";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.PORT, decodedCommand.getAction());
        assertEquals("1234", decodedCommand.getParams()[0]);
    }

    @Test
    public void testTypeAsciiNonPrint() {
        telnetCommand = "TYPE A N";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("A", decodedCommand.getParams()[0]);
        assertEquals("N", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeAsciiTelnetFormatEffectors() {
        telnetCommand = "TYPE A T";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("A", decodedCommand.getParams()[0]);
        assertEquals("T", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeAsciiCarriageControl() {
        telnetCommand = "TYPE A C";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("A", decodedCommand.getParams()[0]);
        assertEquals("C", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeEbcdicNonPrint() {
        telnetCommand = "TYPE E N";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("E", decodedCommand.getParams()[0]);
        assertEquals("N", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeEbcdicTelnetFormatEffectors() {
        telnetCommand = "TYPE E T";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("E", decodedCommand.getParams()[0]);
        assertEquals("T", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeEbcdicCarriageControl() {
        telnetCommand = "TYPE E C";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("E", decodedCommand.getParams()[0]);
        assertEquals("C", decodedCommand.getParams()[1]);
    }

    @Test
    public void testTypeImage() {
        telnetCommand = "TYPE I";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("I", decodedCommand.getParams()[0]);
    }

    @Test
    public void testTypeLocalByteSize() {
        telnetCommand = "TYPE L 8";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.TYPE, decodedCommand.getAction());
        assertEquals("L", decodedCommand.getParams()[0]);
        assertEquals("8", decodedCommand.getParams()[1]);
    }

    @Test
    public void testModeStream() {
        telnetCommand = "MODE S";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.MODE, decodedCommand.getAction());
        assertEquals("S", decodedCommand.getParams()[0]);
    }

    @Test
    public void testModeBlock() {
        telnetCommand = "MODE B";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.MODE, decodedCommand.getAction());
        assertEquals("B", decodedCommand.getParams()[0]);
    }

    @Test
    public void testModeCompressed() {
        telnetCommand = "MODE C";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.MODE, decodedCommand.getAction());
        assertEquals("C", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStruFile() {
        telnetCommand = "STRU F";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STRU, decodedCommand.getAction());
        assertEquals("F", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStruRecord() {
        telnetCommand = "STRU R";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STRU, decodedCommand.getAction());
        assertEquals("R", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStruPage() {
        telnetCommand = "STRU P";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STRU, decodedCommand.getAction());
        assertEquals("P", decodedCommand.getParams()[0]);
    }

    @Test
    public void testRetr() {
        telnetCommand = "RETR /folder/file.txt";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.RETR, decodedCommand.getAction());
        assertEquals("/folder/file.txt", decodedCommand.getParams()[0]);
    }

    @Test
    public void testStor() {
        telnetCommand = "STOR /folder/file.txt";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.STOR, decodedCommand.getAction());
        assertEquals("/folder/file.txt", decodedCommand.getParams()[0]);
    }

    @Test
    public void testNoop() {
        telnetCommand = "NOOP /folder/file.txt";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.NOOP, decodedCommand.getAction());
    }

    @Test
    public void testAuth() {
        telnetCommand = "AUTH";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.AUTH, decodedCommand.getAction());
    }

    @Test
    public void testSyst() {
        telnetCommand = "SYST";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.SYST, decodedCommand.getAction());
    }

    @Test
    public void testFeat() {
        telnetCommand = "FEAT";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.FEAT, decodedCommand.getAction());
    }

    @Test
    public void testPwd() {
        telnetCommand = "PWD";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.PWD, decodedCommand.getAction());
    }

    @Test
    public void testWhenCommandHasNoParametersItGetsDefaultsParameters() {
        telnetCommand = "NOOP";
        decodedCommand = commandDecoder.decode(telnetCommand);
        assertEquals(FTPCommandAction.NOOP, decodedCommand.getAction());
        assertEquals("-1", decodedCommand.getParams()[0]);
        assertEquals("-1", decodedCommand.getParams()[1]);
        assertEquals("-1", decodedCommand.getParams()[2]);
    }


}
