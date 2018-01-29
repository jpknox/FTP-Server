package com.jpknox.server.storage;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.session.FTPClientSession;
import com.jpknox.server.storage.internaltransfer.*;
import com.jpknox.server.storage.internaltransfer.FileWriter;
import com.jpknox.server.storage.file.DirectoryTransition;
import com.jpknox.server.storage.file.DirectoryTransitioner;
import com.jpknox.server.storage.file.transition.factory.DirectoryTransitionFactory;

import java.io.*;

/**
 * Created by joaok on 26/12/2017.
 */
public class FTPLocalFileDataStore implements DataStore {

    private final ClientSession session;
    private File rootDir = new File("RealFtpStorage");
    private File currentDir;
    private final FileQueue fileQueue = new FileQueue();

    public FTPLocalFileDataStore(ClientSession session) {
        this.session = session;
        if (!rootDir.isDirectory()) rootDir.mkdir();
        currentDir = new File(rootDir.toString());
    }

    @Override
    public File get(String Url) {
        return null;
    }

    //TODO: Integration test
//    @Override
//    public File store(String Url, InputStream inputStream) {
//        if (exists(Url)) return null;
//        File file = new File(rootDir.getPath() + File.separatorChar + Url);
//        System.out.println(file.toString());
//        try {
//            file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            BufferedInputStream bis = new BufferedInputStream(inputStream);
//            byte[] buffer = new byte[8192];
//            for (int len; (len = bis.read(buffer)) != -1; fos.write(buffer, 0, len));
//            fos.close();
//            bis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return file;
//    }

    @Override
    public FileQueue store(String Url) {
        if (exists(Url)) {
            System.out.println("Cannot store " + Url);
            return null;
        }
        System.out.println("Attempting to store " + Url);
        FileWriter permanentFileWriter = new FileWriter(fileQueue, Url, rootDir);
        Thread fileWriter = new Thread(permanentFileWriter);
        fileWriter.start();
        return fileQueue;
    }

    @Override
    public void delete(String Url) {

    }

    @Override
    public boolean exists(String Url) {
        return new File(Url).exists();
    }

    @Override
    public String getCurrentDirectory() {
        return currentDir.toString().replaceAll(rootDir.toString(), "") + "\\";
    }

    @Override
    public void changeWorkingDirectory(String Url) {

        DirectoryTransition[] transitions = DirectoryTransitionFactory.createDirectoryTransitions(Url);

        File rollbackDir = currentDir;
        File newDir = DirectoryTransitioner.performTransitions(transitions, currentDir);

        if (newDir == null) {
            currentDir = rollbackDir;
            session.sendResponse(FTPResponseFactory.createResponse(550));
            return;
        }

        currentDir = newDir;
        session.sendResponse(FTPResponseFactory.createResponse(250));
    }


    @Override
    public void mkDir(String Url) {

    }

    @Override
    public String getNameList(String Url) {
        String nameList = "";
        if (Url == null || Url.equals("\\") || Url.equals("/")) {
            for (File f : rootDir.listFiles()) {
                nameList = (nameList + f.getName() + System.getProperty("line.separator"));
            }
        }
        return nameList;
    }

    @Override
    public String getFileList(String Url) {
        return "-rw-ClientSession--ClientSession--    1 0        0        1073741824000 Feb 19  2016 1000GB.zip";
    }

    @Override
    public boolean validUrl(String Url) {
        return !(Url.equals(null) || Url.length() == 0);
    }


}
