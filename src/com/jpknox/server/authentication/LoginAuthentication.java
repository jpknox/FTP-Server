package com.jpknox.server.authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaok on 24/09/2017.
 */
public class LoginAuthentication {

	List<String> users;

	public LoginAuthentication() {
		users = new ArrayList<String>();
		users.add("user1");
	}

	public boolean validate(String username) {
		return users.contains(username);
	}
}
