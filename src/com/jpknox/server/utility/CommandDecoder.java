package com.jpknox.server.utility;

import com.jpknox.server.command.FTPCommand;
import com.jpknox.server.command.FTPCommandAction;

import java.util.Arrays;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 * Decodes a string command into its object representation
 * Commands are of the form 'ACTION PARAM1 PARAM2 PARAM3'
 *
 */
public class CommandDecoder {

    private static final int ACTION = 0;

    // Extracts the command from a string
    public FTPCommand decode(String telnetCommand) {
        String[] tokens = telnetCommand.split(" ");

        //Extract the action
        FTPCommandAction commandAction;
        try {
            commandAction = FTPCommandAction.valueOf(tokens[ACTION].toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            log("Error when creating an instance of the command ENUM.");
            return new FTPCommand(FTPCommandAction.ERROR, null);
        }

        //Extract the params
        boolean hasParams = tokens.length > 1;
        String[] commandParams;
        if (hasParams) {
            int numOfParams = tokens.length-1;
            commandParams = new String[numOfParams];
            for (int i = 1; i < tokens.length; i++) {
                commandParams[i-1] = tokens[i];
            }
        } else {
            // Safer to populate with non-null data if no params are given to the decoder
            commandParams = new String[3];
            Arrays.fill(commandParams, "-1");
        }

        return new FTPCommand(commandAction, commandParams);
    }
}
