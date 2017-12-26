package com.jpknox.server.storage;


import java.io.File;
import java.io.InputStream;

/**
 * Created by JoaoPaulo on 25-Sep-17.
 */
public class FileManager implements DataStore {

    private DataStore dataStore = new FTPLocalFileDataStore();

    @Override
    public File get(String URL) {
        return null;
    }

    @Override
    public File store(String URL, InputStream inputStream) {
        return null;
    }

    @Override
    public void delete(String URL) {

    }

    @Override
    public boolean exists(String URL) {
        return false;
    }

    @Override
    public String getCurrentDirectory() {
        return dataStore.getCurrentDirectory();
    }

    @Override
    public String getNameList(String URL) {
        return null;
    }
}
