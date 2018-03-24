package com.jpknox.server;

public class IntegrationTestWrapper implements Runnable {

    private FTPServer ftpServer;

    @Override
    public void run() {
        ftpServer = new FTPServer();
    }

    public FTPServer getFtpServer() {
        return ftpServer;
    }
}
