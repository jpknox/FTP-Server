package com.jpknox.server.state;

/**
 * Created by João Paulo Knox on 24/09/2017.
 */
public interface SessionState {

    void user(String username);           //Login username
    void pass(String password);           //Login password
    void quit();                          //Disconnect
    void port(int portToUse);             //Data connection port
    void type(String format);             //Representation type (Default: ASCII non-print)
    void mode(String modeToUse);          //Data send mode (Default: Stream)
    void stru(String structureToUse);     //File structure representation (Default: File)
    void retr(String pathToFile);         //Download file
    void dele(String pathToFile);         //Delete a file
    void stor(String pathToFile);         //Upload file
    void noop();                          //Server status check
    void auth();                          //Authentication type
    void syst();                          //System settings
    void feat();                          //Features
    void pwd ();                          //Working directory
    void pasv();                          //Specifies the passive data send method
    void nlst();                          //A list of all files/folders within the current directory
    void list();                          //List information about files in the current directory
    void cwd(String Url);                 //Change current working directory
}
