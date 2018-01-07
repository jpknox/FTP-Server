package com.jpknox.server.storage.internaltransfer;

import java.io.*;

/**
 * Created by JoaoPaulo on 01-Jan-18.
 */
public class FileWriter implements Runnable {

    private FileQueue fileQueue;
    private String filename;
    private File root;

    public FileWriter(FileQueue fileQueue, String filename, File root) {
        this.fileQueue = fileQueue;
        this.filename = filename;
        this.root = root;
    }

    @Override
    public void run() {
        try {
            File tempFile = fileQueue.getFile();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tempFile));
            File permanentFile = new File(root.getName() + System.getProperty("file.separator") + filename);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(permanentFile));
            for (int c; (c = bis.read()) != -1; bos.write(c));
            bis.close();
            bos.close();
            tempFile.delete();
            System.out.println("Finished writing file " + filename + ", size " + permanentFile.length());
            System.out.println("Final location " + permanentFile.getCanonicalPath());
            System.out.println("Is a file " + permanentFile.isFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
