package com.jpknox.server.storage;

import java.io.File;
import java.io.InputStream;

/**
 * Created by joaok on 26/12/2017.
 */
public interface DataStore {

    File get(String URL);

    File store(String URL, InputStream inputStream);

    void delete(String URL);

    boolean exists(String URL);

    String getCurrentDirectory();

    String getNameList(String URL);

}
