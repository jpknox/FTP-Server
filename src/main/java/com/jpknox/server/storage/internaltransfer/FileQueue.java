package com.jpknox.server.storage.internaltransfer;

import java.io.File;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 01-Jan-18.
 */
public class FileQueue {

    private File file;
    private boolean isSet = false;

    public synchronized void setFile(File file) {
        if (isSet) {
            try { wait(); } catch (InterruptedException ie) {}
        }
        isSet = true;
        this.file = file;
        notify();
        log("File queue has a file");
    }

    public synchronized File getFile() {
        if (!isSet) {
            try { wait(); } catch (InterruptedException ie) {}
        }
        isSet = true;
        notify();
        log("File queue has handed over its file");
        return file;
    }
}
