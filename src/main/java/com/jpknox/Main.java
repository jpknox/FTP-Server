package com.jpknox;

import com.jpknox.server.FTPServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) {
        try {
            FTPServer ftpServer = new FTPServer(new ServerSocket(21));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
