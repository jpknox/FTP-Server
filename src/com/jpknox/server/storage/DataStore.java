package com.jpknox.server.storage;

import java.io.File;
import java.io.InputStream;

/**
 * Created by joaok on 26/12/2017.
 */
public interface DataStore {

    File get(String Url);

    File store(String Url, InputStream inputStream);

    void delete(String Url);

    boolean exists(String Url);

    String getCurrentDirectory();

    void changeWorkingDirectory(String Url);

    void mkDir(String Url);

    String getNameList(String Url);

}
