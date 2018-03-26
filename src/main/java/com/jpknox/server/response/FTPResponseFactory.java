package com.jpknox.server.response;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by João Paulo Knox on 25/12/2017.
 * All responses from the factory will come in the form of a {@code String}
 * both the content and format of the {@code String} will be delegated to
 * the factory. The response is requested by providing a standard {@code code}
 * parameter relating to each type of response.
 */
public class FTPResponseFactory {

    public static final String DIRECTORY_LIST = "150 Sending the directory listing.";
    public static final String COMMAND_OKAY = "200 Command okay.";
    public static final String COMMAND_OKAY_WITH_ARG = "200 %s";
    public static final String SUPERFLUOUS_COMMAND = "202 Command not implemented, superfluous at this site.";
    public static final String SYSTEM_DETAILS = "215 %s: %s";
    public static final String CLOSING_CONTROL_CONNECTION = "221 Service closing control connection.";
    public static final String CLOSING_DATA_CONNECTION = "226 Closing data connection.";
    public static final String LOGIN_SUCCESS = "230 %s logged in, proceed.";
    public static final String FILE_ACTION_SUCCESS = "250 Requested file action okay, completed.";
    public static final String WORKING_DIRECTORY = "257 %s";
    public static final String NEED_PASSWORD = "331 User name okay, need password.";
    public static final String NO_DATA_CONNECTION = "425 Can't open data connection. Enter PASV first.";
    public static final String UNRECOGNIZED = "500 Syntax error, command unrecognized.";
    public static final String SYNTAX_ERROR = "501 Syntax error in parameters or arguments.";
    public static final String COMMAND_NOT_IMPLEMENTED = "502 Command not implemented.";
    public static final String BAD_SEQUENCE = "503 Bad sequence of commands.";
    public static final String MUST_LOG_IN = "530 Not logged in.";
    public static final String FILE_ACTION_FAILURE = "550 Requested action not taken. File unavailable" +
                                                     " (e.g., file not found, no access).";

    public static String createResponse(int code) {
        String format = null;
        String output;
        final StringBuilder sb = new StringBuilder();
        final Formatter formatter = new Formatter(sb, Locale.UK);

        if (code == 150) format = DIRECTORY_LIST;
        if (code == 200) format = COMMAND_OKAY;
        if (code == 202) format = SUPERFLUOUS_COMMAND;
        if (code == 221) format = CLOSING_CONTROL_CONNECTION;
        if (code == 226) format = CLOSING_DATA_CONNECTION;
        if (code == 250) format = FILE_ACTION_SUCCESS;
        if (code == 331) format = NEED_PASSWORD;
        if (code == 425) format = NO_DATA_CONNECTION;
        if (code == 500) format = UNRECOGNIZED;
        if (code == 501) format = SYNTAX_ERROR;
        if (code == 502) format = COMMAND_NOT_IMPLEMENTED;
        if (code == 503) format = BAD_SEQUENCE;
        if (code == 530) format = MUST_LOG_IN;
        if (code == 550) format = FILE_ACTION_FAILURE;
        formatter.format(format);
        output = sb.toString();
        sb.setLength(0);
        return output;
    }

    public static String createResponse(int code, String... param) {
        String format = null;
        String output;
        final StringBuilder sb = new StringBuilder();
        final Formatter formatter = new Formatter(sb, Locale.UK);

        if (code == 200) {
            if (param[0].equalsIgnoreCase("CWD")) {
                format = COMMAND_OKAY_WITH_ARG;
                formatter.format(format, "Working directory changed.");
            } else {
                return createResponse(code);
            }
        }
        else if (code == 215) {
            format = SYSTEM_DETAILS;
            formatter.format(format, param[0], param[1]);
        } else if (code == 230) {
            format = LOGIN_SUCCESS;
            formatter.format(format, firstCharToUpper(param[0]));
        }
        else if (code == 257) {
            format = WORKING_DIRECTORY;
            formatter.format(format, param[0]);
        }
        output = sb.toString();
        sb.setLength(0);
        return output;
    }

    private static String firstCharToUpper(String param) {
        return param.substring(0, 1).toUpperCase() + param.substring(1);
    }
}
