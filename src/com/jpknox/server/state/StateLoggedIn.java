package com.jpknox.server.state;

import com.jpknox.server.FTPServer;
import com.jpknox.server.authentication.LoginAuthentication;

/**
 * Created by joaok on 24/09/2017.
 */
public class StateLoggedIn implements SessionState {

	public StateLoggedIn() {
	}

	@Override
	public int user(FTPServer context, String username) {
		context.log("Client attempting to login whilst already logged in.");
		context.sendToClient("You are already logged in.");
		return 0;
	}

	@Override
	public int quit(FTPServer context) {
		return 0;
	}

	@Override
	public int port(FTPServer context, int portToUse) {
		return 0;
	}

	@Override
	public int type(FTPServer context, String format) {
		return 0;
	}

	@Override
	public int mode(FTPServer context, String modeToUse) {
		return 0;
	}

	@Override
	public int stru(FTPServer context, String structureToUse) {
		return 0;
	}

	@Override
	public int retr(FTPServer context, String pathToFile) {
		return 0;
	}

	@Override
	public int stor(FTPServer context, String pathToFile) {
		return 0;
	}

	@Override
	public int noop(FTPServer context) {
		return 0;
	}

}
