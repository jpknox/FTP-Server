package com.jpknox.server;

import java.io.File;

/**
 * Created by JoaoPaulo on 25-Sep-17.
 */
public class FileManager {

    private static FileManager instance;
    private File rootNode = new File("resources");
    private File displayNode = new File(rootNode.toString());

    public static FileManager getInstance() {
        if (instance == null) {
            return (instance = new FileManager());
        } else {
            return instance;
        }
    }

    public String getCurrentDirectory() {
        return rootNode.toString().replaceAll(displayNode.toString(), "/");
    }




}
