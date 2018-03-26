package com.jpknox.server.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


        //Remove spaces unless they're within double quotes
        List<String> constituentComponents = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"|\']\\S*|\".+?\"|\'.+?\')\\s*").matcher(telnetCommand);
        while (m.find()) {
            constituentComponents.add(m.group(1));
        }


        //Extract the action
        FTPCommandAction commandAction = null;
        try {
            commandAction = FTPCommandAction.valueOf(constituentComponents.get(ACTION).toUpperCase());
        } catch (IllegalArgumentException | NullPointerException | IndexOutOfBoundsException exception) {
            if (constituentComponents.size() == 0 || constituentComponents.get(0).trim().equals("")) {
                log("The client has entered a command consisting entirely of spaces");
                return new FTPCommand(FTPCommandAction.ERROR_1, defaultParams());
            } else {
                log("Error when creating an instance of the command ENUM with '" + constituentComponents.get(ACTION) + "'");
                return new FTPCommand(FTPCommandAction.ERROR_0, defaultParams());
            }
        }

        //Extract the params
        boolean hasParams = constituentComponents.size() > 1;
        String[] commandParams;
        if (hasParams) {
            int numOfParams = constituentComponents.size()-1;
            commandParams = new String[numOfParams];
            for (int i = 1; i <= numOfParams; i++) {
                commandParams[i-1] = constituentComponents.get(i);
            }
        } else {
            // Safer to populate with non-null data if no params are given to the decoder
            commandParams = defaultParams();
        }

        //Validate parameters
        int populatedParams = 0;
        for (String param: commandParams) {
            if (!param.equals(null) && param.trim().length() > 0) {
                populatedParams++;
            }
        }
        if (populatedParams < commandAction.getNumberOfParams()) {
            return new FTPCommand(FTPCommandAction.ERROR_1, defaultParams());
        }


        return new FTPCommand(commandAction, commandParams);
    }


    public String[] defaultParams() {
        String[] defaultParams = new String[] {"", "", ""};
        return defaultParams;
    }
}
