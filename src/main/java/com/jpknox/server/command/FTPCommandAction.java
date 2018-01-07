package com.jpknox.server.command;

/**
 * Created by JoaoPaulo on 16-Oct-17.
 * This command represents the finite list of actions
 * supported by an FTP server built to the RFC959 spec.
 * The Auth command is included for testing purposes.
 *
 * @see <a href="https://www.ietf.org/rfc/rfc959.txt">IETF's RFC959 for FTP</a>
 */
public enum FTPCommandAction {
    USER("USER"),
    PASS("PASS"),
    QUIT("QUIT"),
    PORT("PORT"),
    TYPE("TYPE"),
    MODE("MODE"),
    STRU("STRU"),
    RETR("RETR"),
    STOR("STOR"),
    NOOP("NOOP"),
    AUTH("AUTH"),
    SYST("SYST"),
    FEAT("FEAT"),
    PWD("PWD"),
    CWD("CWD"),
    PASV("PASV"),
    ERROR_1("SYNTAX ERROR"),
    NLST("NLST"),
    LIST("LIST"),
    ERROR_0("GENERIC_ERROR");

    private String name;

    FTPCommandAction(String name) {
        this.name = name;
    }

}
