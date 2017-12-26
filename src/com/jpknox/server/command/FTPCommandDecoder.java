package com.jpknox.server.command;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 * Decodes a string command into its object representation
 * Commands are of the form 'ACTION PARAM1 PARAM2 PARAM3'
 *
 */
public class FTPCommandDecoder {

    private static final int ACTION = 0;

    // Extracts the command from a string
    public FTPCommand decode(String telnetCommand) {
        telnetCommand = telnetCommand.replace(System.getProperty("line.separator"), "");
        String[] tokens = telnetCommand.split(" ");

        //Extract the action
        FTPCommandAction commandAction = null;
        try {
            commandAction = FTPCommandAction.valueOf(tokens[ACTION].toUpperCase());
        } catch (IllegalArgumentException | NullPointerException | ArrayIndexOutOfBoundsException exception) {
            if (tokens.length == 0 || tokens[0].equals("")) {
                log("The client has entered a command consisting entirely of spaces");
                return new FTPCommand(FTPCommandAction.ERROR_1, defaultParams());
            } else {
                log("Error when creating an instance of the command ENUM with '" + tokens[ACTION] + "'");
                return new FTPCommand(FTPCommandAction.ERROR_0, defaultParams());
            }
        }

        //Extract the params
        boolean hasParams = tokens.length > 1;
        String[] commandParams;
        if (hasParams) {
            int numOfParams = tokens.length-1;
            commandParams = new String[numOfParams];
            for (int i = 1; i <= numOfParams; i++) {
                commandParams[i-1] = tokens[i];
            }
        } else {
            // Safer to populate with non-null data if no params are given to the decoder
            commandParams = defaultParams();
        }

        return new FTPCommand(commandAction, commandParams);
    }


    public String[] defaultParams() {
        String[] defaultParams = new String[] {"", "", ""};
        return defaultParams;
    }
}
