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
    USER("USER", 1),
    PASS("PASS", 1),
    QUIT("QUIT", 0),
    PORT("PORT", 1),
    TYPE("TYPE", 1),
    MODE("MODE", 1),
    STRU("STRU", 0),
    RETR("RETR", 1),
    DELE("DELE", 1),
    STOR("STOR", 1),
    NOOP("NOOP", 0),
    AUTH("AUTH", 0),
    SYST("SYST", 0),
    FEAT("FEAT", 0),
    PWD("PWD", 0),
    CWD("CWD", 1),
    PASV("PASV", 0),
    NLST("NLST", 0),
    LIST("LIST", 0),
    ERROR_1("SYNTAX ERROR", 0),
    ERROR_0("GENERIC_ERROR", 0);

    private String name;
    private int numberOfParams;

    FTPCommandAction(String name, int numberOfParams) {
        this.name = name;
        this.numberOfParams = numberOfParams;
    }


    public int getNumberOfParams() {
        return numberOfParams;
    }
}
