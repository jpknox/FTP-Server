package com.jpknox.server.response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by João Paulo Knox on 26/12/2017.
 */
public class ClientViewCommunicator {

    private PrintWriter output;
    private BufferedReader input;

    public void write(String text) {
        log(String.format("To client '%s'.", text));
        output.write(text + System.getProperty("line.separator"));
        output.flush();
    }

    public String readLine() throws SocketException {
        String line = "";
        try {
            line = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public void setOutput(PrintWriter output) {
        this.output = output;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }
}
