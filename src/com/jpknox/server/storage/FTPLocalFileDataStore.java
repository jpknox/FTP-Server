package com.jpknox.server.storage;

import java.io.*;

/**
 * Created by joaok on 26/12/2017.
 */
public class FTPLocalFileDataStore implements DataStore {

    private File rootDir = new File("RealFtpStorage");
    private File currentDir;

    public FTPLocalFileDataStore() {
        if (!rootDir.isDirectory()) rootDir.mkdir();
        currentDir = new File(rootDir.toString());
    }

    @Override
    public File get(String URL) {
        return null;
    }

    @Override
    public File store(String URL, InputStream inputStream) {
        File file = new File(rootDir.getPath() + File.separatorChar + URL);
        System.out.println(file.toString());
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[8192];
            for (int len; (len = bis.read(buffer)) != -1; fos.write(buffer, 0, len));
            fos.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
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
        return rootDir.toString().replaceAll(currentDir.toString(), "/");
    }

    @Override
    public String getNameList(String URL) {
        return null;
    }


}
