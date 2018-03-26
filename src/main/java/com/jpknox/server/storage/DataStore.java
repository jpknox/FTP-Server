package com.jpknox.server.storage;

import com.jpknox.server.storage.internaltransfer.FileQueue;

import java.io.File;

/**
 * Created by João Paulo Knox on 26/12/2017.
 */
public interface DataStore {

    File get(String Url);

    //File store(String Url, InputStream inputStream);

    FileQueue store(String Url);

    boolean delete(String Url);

    boolean exists(String Url);

    String getCurrentDirectory();

    void changeWorkingDirectory(String Url);

    void mkDir(String Url);

    String getNameList(String Url);

    String getFileList();

    boolean validUrl(String Url);

}
