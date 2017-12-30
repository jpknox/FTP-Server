package com.jpknox.server.storage;

import com.jpknox.server.response.FTPResponseFactory;
import com.jpknox.server.session.ClientSession;

import java.io.*;
import java.util.*;
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

    //TODO: Ensure the actual casing of the folder names is displayed correctly
    //TODO: Url begins with a \ || / then it's absolute.
    @Override
    public void changeWorkingDirectory(String Url) {
        if (Url.equals(null) || Url.length() == 0) {
            session.getViewCommunicator().write(ftpResponseFactory.createResponse(501));
            return;
        }
        File newDir = null;

        List<String> quotesToRemove = Arrays.asList("\"", "\'");    //Remove quotes " and '
        String noQuotesUrl = Pattern.compile("").splitAsStream(Url)
                .filter(s -> !quotesToRemove.contains(s))
                .collect(Collectors.joining());

        if (Stream.of("\\", "/", System.getProperty("file.separator")).anyMatch(noQuotesUrl::equals)) {
            //Go to root
            currentDir = rootDir;
            session.getViewCommunicator().write(ftpResponseFactory.createResponse(250));
            return;
        }

        //TODO: Keep the first \ or /
        //TODO: Split the list of directory commands into one
        //TODO: Keep Java's file.separator
        List<String> list = new ArrayList<String>(
                Arrays.asList(noQuotesUrl.split("\\\\|/|" + System.getProperty("line.separator"))));
        Collections.reverse(list);

        while (list.size() > 0) {
            String currentUrl = list.remove(list.size()-1);

            if (currentUrl.equals(".")) {
                //Stay in same directory
                newDir = currentDir;
            } else if (Stream.of("\\", "/", System.getProperty("file.separator")).anyMatch(currentUrl.substring(0, 1)::equals)) {
                //Navigate from absolute root
                newDir = new File(rootDir.toString() + currentUrl);
            } else if (currentUrl.length() > 1
                        && currentUrl.substring(0, 2).equals("..")) {
                //Go back up the directory tree
                newDir = currentDir.equals(rootDir) ? currentDir : new File(currentDir.getParent());
            } else {
                //Navigate to a folder relative to current dir
                try {
                    //Reflect real folder's casing
                    newDir = new File(currentDir.toString() + System.getProperty("file.separator")
                            + (new File(currentDir.toString() + System.getProperty("file.separator")
                            + currentUrl).getCanonicalFile().getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (newDir.isDirectory()) {
                currentDir = newDir;
            } else {
                newDir.delete();
                session.getViewCommunicator().write(ftpResponseFactory.createResponse(550));
                return;
            }
        }
        session.getViewCommunicator().write(ftpResponseFactory.createResponse(250));
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
