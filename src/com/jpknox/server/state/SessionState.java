package com.jpknox.server.state;

/**
 * Created by joaok on 24/09/2017.
 */
public interface SessionState {

	int user(String username);          //Login username
	int quit();                         //Disconnect
	int port(int portToUse);            //Data connection port
	int type(String format);            //Representation type (Default: ASCII non-print)
	int mode(String modeToUse);         //Data transfer mode (Default: Stream)
	int stru(String structureToUse);    //File structure representation (Default: File)
	int retr(String pathToFile);        //Download file
	int stor(String pathToFile);        //Upload file
	int noop();                         //Server status check

}
