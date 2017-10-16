package com.jpknox.server.utility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 */
public class FileManagerTest {

    private FileManager fileManager;

    @Before
    public void setup() {
        fileManager = FileManager.getInstance();
    }

    @After
    public void teardown() {
        fileManager = null;
    }

    @Test
    public void testSingletonFunctionality() {
        FileManager secondFileManager = FileManager.getInstance();
        assertEquals(fileManager, secondFileManager);
    }

    @Test
    public void testGetCurrentDirectory() {
        String currentDirectory = fileManager.getCurrentDirectory();
        assertEquals("/", currentDirectory);
    }



}
