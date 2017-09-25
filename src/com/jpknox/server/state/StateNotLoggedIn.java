package com.jpknox.server.state;

import com.jpknox.server.FTPServer;
import com.jpknox.server.authentication.LoginAuthentication;

/**
 * Created by joaok on 24/09/2017.
 */
public class StateNotLoggedIn implements SessionState {

	FTPServer context;
	private final LoginAuthentication loginAuthentication = new LoginAuthentication();

	public StateNotLoggedIn(FTPServer ftpServer) {
		this.context = ftpServer;
	}

	@Override
	public int user(String username) {
		context.log("Username \"" + username + "\" attempting to log in.");
		if (loginAuthentication.validate(username) == true) {
			context.setState(context.getLoggedIn());
			context.log("Switching from " + this.getClass().getName() + " to " + context.getLoggedIn().getClass().getName() + ".");
			context.log("Username \"" + username + "\" logged in.");
		} else {
			context.log("Username \"" + username + "\" failed to log in.");
		}
		return 0;
	}

	@Override
	public int quit() {
		return 0;
	}

	@Override
	public int port(int portToUse) {
		return 0;
	}

	@Override
	public int type(String format) {
		return 0;
	}

	@Override
	public int mode(String modeToUse) {
		return 0;
	}

	@Override
	public int stru(String structureToUse) {
		return 0;
	}

	@Override
	public int retr(String pathToFile) {
		return 0;
	}

	@Override
	public int stor(String pathToFile) {
		return 0;
	}

	@Override
	public int noop() {
		return 0;
	}
}
