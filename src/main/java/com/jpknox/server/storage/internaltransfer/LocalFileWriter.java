package com.jpknox.server.storage.internaltransfer;

import java.io.*;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 01-Jan-18.
 */
public class LocalFileWriter implements Runnable {

    private FileQueue fileQueue;
    private String filename;
    private File insertionFolder;

    public LocalFileWriter(FileQueue fileQueue, String filename, File insertionFolder) {
        this.fileQueue = fileQueue;
        this.filename = filename;
        this.insertionFolder = insertionFolder;
    }

    @Override
    public void run() {
        try {
            File tempFile = fileQueue.getFile();
            log("tempFile.isFile " + tempFile.isFile());
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tempFile));
            File permanentFile = new File(insertionFolder.getPath() + System.getProperty("file.separator") + filename);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(permanentFile));
            for (int c; (c = bis.read()) != -1; bos.write(c));
            bis.close();
            bos.close();
            tempFile.delete();
            log("Finished writing filename '" + filename + "', size " + permanentFile.length());
            log("Final location " + permanentFile.getCanonicalPath());
            log("permanentFile.isFile " + permanentFile.isFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
