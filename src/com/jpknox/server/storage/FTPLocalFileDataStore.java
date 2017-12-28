package com.jpknox.server.storage;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by joaok on 26/12/2017.
 */
public class FTPLocalFileDataStore implements DataStore {

    private final ClientSession session;
    private final FTPResponseFactory ftpResponseFactory = new FTPResponseFactory();
    private File rootDir = new File("RealFtpStorage");
    private File currentDir;

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
    @Override
    public File store(String Url, InputStream inputStream) {
        File file = new File(rootDir.getPath() + File.separatorChar + Url);
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
    public void delete(String Url) {

    }

    @Override
    public boolean exists(String Url) {
        return false;
    }

    @Override
    public String getCurrentDirectory() {
        return currentDir.toString().replaceAll(rootDir.toString(), "") + "\\";
    }

    //TODO: ".." must go back up by one, "/" must go back to root.
    //TODO: Pass this class a reference to the session so it can contact the client with appropriate messages.
    //TODO: Url begins with a \ || / then it's absolute.
    //TODO: Url begins with an alphanumeric char then it's relative to current dir.
    @Override
    public boolean changeWorkingDirectory(String Url) {
        if (Url.equals(null) || Url.length() == 0) return false;
        File newDir = null;

        List<String> quotesToRemove = Arrays.asList("\"", "\'");    //Remove quotes " and '
        String noQuotesUrl = Pattern.compile("").splitAsStream(Url)
                .filter(s -> !quotesToRemove.contains(s))
                .collect(Collectors.joining());
        if (Url.equals(".")) {
            //Stay in same directory
            newDir = currentDir;
        } else if (Stream.of("\\", "/", System.getProperty("file.separator")).anyMatch(Url.substring(0, 2)::equals)) {
            //Navigate from absolute root
            newDir = new File(rootDir.toString() + noQuotesUrl);
        } else if (Url.substring(0, 2).equals("..")) {
            //Go back up the directory tree
            newDir = new File(currentDir.getParent());
        } else {
            //Navigate relative to current dir
            newDir = new File(currentDir.toString() + System.getProperty("file.separator") + noQuotesUrl);
        }
        if (newDir.isDirectory()) {
            currentDir = newDir;
            session.getViewCommunicator().write(ftpResponseFactory.createResponse(250));
            return true;
        } else {
            newDir.delete();
            session.getViewCommunicator().write(ftpResponseFactory.createResponse(550));
            return false;
        }
    }

    //"RealFtpStorage/Folder 1/Subfolder 1"


    //TODO: Integration test
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


}
