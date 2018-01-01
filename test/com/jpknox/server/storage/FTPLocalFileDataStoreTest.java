package com.jpknox.server.storage;

import com.jpknox.server.session.ClientSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 */
public class FTPLocalFileDataStoreTest {

    public static final String NEWLINE = System.getProperty("line.separator");

    private FTPLocalFileDataStore ftpLocalFileDataStore;

    @Before
    public void setup() {
        ClientSession session = Mockito.mock(ClientSession.class);
        ftpLocalFileDataStore = new FTPLocalFileDataStore(session);
    }

    @After
    public void teardown() {
        ftpLocalFileDataStore = null;
    }

    @Test
    public void testGetCurrentDirectory() {
        String currentDirectory = ftpLocalFileDataStore.getCurrentDirectory();
        assertEquals(System.getProperty("file.separator"), currentDirectory);
    }

//    @Test
//    public void testStoreSuccess() {
//        ByteArrayInputStream stream = new ByteArrayInputStream("I am data".getBytes());
//        File file = ftpLocalFileDataStore.store("NewFile.txt", stream);
//        assertEquals(0, stream.available());
//        assertNotNull(file);
//        assertEquals(9, file.length());
//        assertTrue(file.delete());
//    }

//    @Test
//    public void testGetNameList() {
//        String actual = ftpLocalFileDataStore.getNameList();
//        String expect = "1000GB.zip" + NEWLINE +
//                        "100GB.zip" + NEWLINE  +
//                        "100KB.zip" + NEWLINE;
//        System.out.printf(expect);
//        assertEquals(expect, actual);
//    }
}
