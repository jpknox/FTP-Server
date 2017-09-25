package com.jpknox.server.state;

import com.jpknox.server.FTPServer;

/**
 * Created by joaok on 24/09/2017.
 */
public interface SessionState {

    int user(FTPServer context, String username);          	//Login username
    int quit(FTPServer context);                         	//Disconnect
    int port(FTPServer context, int portToUse);				//Data connection port
    int type(FTPServer context, String format);            	//Representation type (Default: ASCII non-print)
    int mode(FTPServer context, String modeToUse);         	//Data transfer mode (Default: Stream)
    int stru(FTPServer context, String structureToUse);    	//File structure representation (Default: File)
    int retr(FTPServer context, String pathToFile);        	//Download file
    int stor(FTPServer context, String pathToFile);        	//Upload file
    int noop(FTPServer context);                         	//Server status check
    int auth(FTPServer context);							//Authentication type
	int syst(FTPServer context);							//System settings
	int feat(FTPServer context);							//Features
	int pwd(FTPServer context);								//Working directory

}
