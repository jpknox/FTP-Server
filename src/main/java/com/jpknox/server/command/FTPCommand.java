package com.jpknox.server.command;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 * This object represents a FTP command including the action and its parameters.
 */
public class FTPCommand {

    private final FTPCommandAction action;
    private final String[] params;

    public FTPCommand(FTPCommandAction action, String[] params) {
        this.action = action;
        this.params = params;
    }

    public FTPCommandAction getAction() {
        return action;
    }

    public String[] getParams() {
        return params;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(action.toString());
        for (String string : params) {
            stringBuilder.append(" " + string);
        }
        return stringBuilder.toString();
    }
}
