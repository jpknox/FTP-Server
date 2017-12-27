package com.jpknox.server.state;

/**
 * Created by joaok on 24/09/2017.
 */
public interface SessionState {

    String user(String username);           //Login username
    String pass(String password);           //Login password
    String quit();                          //Disconnect
    String port(int portToUse);             //Data connection port
    String type(String format);             //Representation type (Default: ASCII non-print)
    String mode(String modeToUse);          //Data send mode (Default: Stream)
    String stru(String structureToUse);     //File structure representation (Default: File)
    String retr(String pathToFile);         //Download file
    String stor(String pathToFile);         //Upload file
    String noop();                          //Server status check
    String auth();                          //Authentication type
    String syst();                          //System settings
    String feat();                          //Features
    String pwd ();                          //Working directory
    String pasv();                          //Specifies the passive data send method
    String nlst();

}
