package com.jpknox.server.state;

import com.jpknox.server.session.ClientSession;

/**
 * Created by joaok on 24/09/2017.
 */
public interface SessionState {

    String user(ClientSession session, String username);            //Login username
    String pass(ClientSession session, String password);            //Login password
    String quit(ClientSession session);                         	//Disconnect
    String port(ClientSession session, int portToUse);				//Data connection port
    String type(ClientSession session, String format);            	//Representation type (Default: ASCII non-print)
    String mode(ClientSession session, String modeToUse);         	//Data transfer mode (Default: Stream)
    String stru(ClientSession session, String structureToUse);    	//File structure representation (Default: File)
    String retr(ClientSession session, String pathToFile);        	//Download file
    String stor(ClientSession session, String pathToFile);        	//Upload file
    String noop(ClientSession session);                         	//Server status check
    String auth(ClientSession session);							    //Authentication type
    String syst(ClientSession session);							    //System settings
    String feat(ClientSession session);							    //Features
    String pwd (ClientSession session);							    //Working directory

}
