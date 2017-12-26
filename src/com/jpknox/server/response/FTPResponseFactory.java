package com.jpknox.server.response;

import com.jpknox.server.utility.FTPServerConfig;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by joaok on 25/12/2017.
 * This class encapsulates the responsibility of creating and
 * formatting each response a client will receive when communicating
 * with the FTP protocol over the {@code control} connection.
 */
public class FTPResponseFactory implements ResponseFactory {

    public static final String COMMAND_OKAY = "200 Command okay.";
    public static final String SUPERFLUOUS_COMMAND = "202 Command not implemented, superfluous at this site.";
    public static final String SYSTEM_DETAILS = "215 %s: %s";
    public static final String CLOSING_CONTROL_CONNECTION = "221 Service closing control connection.";
    public static final String LOGIN_SUCCESS = "230 %s logged in, proceed.";
    public static final String WORKING_DIRECTORY = "257 %s";
    public static final String NEED_PASSWORD = "331 User name okay, need password.";
    public static final String UNRECOGNIZED = "500 Syntax error, command unrecognized.";
    public static final String SYNTAX_ERROR = "501 Syntax error in parameters or arguments.";
    public static final String COMMAND_NOT_IMPLEMENTED = "502 Command not implemented.";
    public static final String BAD_SEQUENCE = "503 Bad sequence of commands.";
    public static final String LOGIN_FAILURE = "530 Not logged in.";


    private String format;
    private String output;
    private final StringBuilder sb = new StringBuilder();
    private final Formatter formatter = new Formatter(sb, Locale.UK);

    @Override
    public String createResponse(int code) {
        if (code == 200) format = COMMAND_OKAY;
        if (code == 202) format = SUPERFLUOUS_COMMAND;
        if (code == 221) format = CLOSING_CONTROL_CONNECTION;
        if (code == 331) format = NEED_PASSWORD;
        if (code == 500) format = UNRECOGNIZED;
        if (code == 501) format = SYNTAX_ERROR;
        if (code == 502) format = COMMAND_NOT_IMPLEMENTED;
        if (code == 503) format = BAD_SEQUENCE;
        if (code == 530) format = LOGIN_FAILURE;
        formatter.format(format);
        output = sb.toString();
        sb.setLength(0);
        return output;
    }

    @Override
    public String createResponse(int code, String... param) {
        if (code == 215) {
            format = SYSTEM_DETAILS;
            formatter.format(format, param[0], param[1]);
        } else if (code == 230) {
            format = LOGIN_SUCCESS;
            formatter.format(format, firstCharToUpper(param[0]));
        }
        else if (code == 257) {
            format = WORKING_DIRECTORY;
            formatter.format(format, param);
        }
        output = sb.toString();
        sb.setLength(0);
        return output;
    }

    public String firstCharToUpper(String param) {
        return param.substring(0, 1).toUpperCase() + param.substring(1);
    }
}
