package com.jpknox.server.storage;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;
import com.jpknox.server.storage.internaltransfer.*;
import com.jpknox.server.storage.internaltransfer.FileWriter;
import com.jpknox.server.storage.file.DirectoryTransition;
import com.jpknox.server.storage.file.DirectoryTransitioner;
import com.jpknox.server.storage.file.transition.factory.DirectoryTransitionFactory;

import javax.swing.text.DateFormatter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
        if (!exists(Url)) return null;
        DirectoryTransition[] transitions = DirectoryTransitionFactory.createDirectoryTransitions(Url);
        File file = DirectoryTransitioner.performTransitions(transitions, currentDir);
        if (file == null) {
            //TODO: The resource identified by the URL does not exist.
            //session.getViewCommunicator().write(FTPResponseFactory.createResponse(550));
            return null;
        } else if (file.isDirectory()) {
            //TODO: The resource is a directory, not a file.
        } else if (file.isFile()) {
            return file;
            //TODO: Return the file.
        } else {
            //TODO: Something unexpected has occurred.
        }

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
            session.getViewCommunicator().write(FTPResponseFactory.createResponse(550));
            return;
        }

        currentDir = newDir;
        session.getViewCommunicator().write(FTPResponseFactory.createResponse(250));
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
    public String getFileList() {

        //TODO: Get list of files within the folder specified by the URL.
        //TODO: Generate and return an order-sensitive, formatted list of the files within the folder

        //DirectoryTransition[] transitions = DirectoryTransitionFactory.createDirectoryTransitions(Url);
        //File parentFile = DirectoryTransitioner.performTransitions(transitions, currentDir);
        StringBuilder string = null;
        //if (parentFile.isDirectory()) {
            File parentFile = currentDir;
            string = new StringBuilder();
            for (File childFile : parentFile.listFiles()) {
                try {
                    //TODO: Verify that Paths.get() is retrieving a valid path relative to the project's location.
                    Path file = Paths.get(childFile.getPath());

                    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M");
                    String month = dateFormat.format(attr.creationTime().toMillis());
                    dateFormat.applyPattern("d");
                    String dayOfMonth = dateFormat.format(attr.creationTime().toMillis());
                    dateFormat.applyPattern("y");
                    String year = dateFormat.format(attr.creationTime().toMillis());

                    string.append("-rw-r--r--");        //Permissions
                    string.append("\t");
                    string.append("1");                 //?
                    string.append(" ");
                    string.append("0");                 //?
                    string.append("\t");
                    string.append("0");                 //?
                    string.append("\t");
                    string.append(childFile.length());  //Length
                    string.append(" ");
                    string.append(month); //Month
                    string.append(" ");
                    string.append(dayOfMonth); //Day
                    string.append(" ");
                    string.append(" ");
                    string.append(year); //Year
                    string.append(" ");
                    string.append(childFile.getName());
                    string.append(System.getProperty("line.separator"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        //}

        String format = "%S\t%d %d\t%d\t%d %S %d  %d %S";
        //return "-rw-r--r--    1 0        0        1073741824000 Feb 19  2016 1000GB.zip";
        if (!string.equals(null)) {
            return string.toString();
        } else {
            return "Error when creating file list.";
        }
    }

    @Override
    public boolean validUrl(String Url) {
        return !(Url.equals(null) || Url.length() == 0);
    }


}
