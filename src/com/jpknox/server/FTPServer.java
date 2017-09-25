package com.jpknox.server;

import com.jpknox.server.state.StateLoggedIn;
import com.jpknox.server.state.StateNotLoggedIn;
import com.jpknox.server.state.SessionState;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by joaok on 24/09/2017.
 */
public class FTPServer {

	private SessionState currentState;
	private SessionState notLoggedIn;
	private SessionState loggedIn;
	private ServerSocket serverSocket;
	private Socket clientConnection;
	private BufferedReader input;
	private PrintWriter output;

	public FTPServer() {
		this.currentState = new StateNotLoggedIn(this);
		log("server starting up.");
		try {
			this.serverSocket = new ServerSocket(21);
			log("waiting for client.");
			this.clientConnection = serverSocket.accept();
			log("client connection established.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			this.input = new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream()));
			this.output = new PrintWriter(new OutputStreamWriter(this.clientConnection.getOutputStream()));

			String dataFromClient = null;
			String dataToClient = null;
			while (true) {
				dataFromClient = input.readLine();
				log("client said: \"" + dataFromClient + "\".");
				dataToClient = "Echo: " + dataFromClient;
				output.write(dataToClient + "\r\n");
				output.flush();
				log("sent to client: " + dataToClient);

				if (dataFromClient.length() > 5 && dataFromClient.substring(0, 5).equals("USER ")) {
					log("Username: " + dataFromClient.substring(5, dataFromClient.length()));
					currentState.user(dataFromClient.substring(5, dataFromClient.length())); //Extract username
				}
				if (dataFromClient.equals("quit")) {
					log("client disconnected.");
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("server shutting down.");
	}

	public int setState(SessionState nextState) {
		this.currentState = nextState;
		return 0;
	}

	public SessionState getNotLoggedIn() {
		return notLoggedIn != null ? notLoggedIn : (notLoggedIn = new StateNotLoggedIn(this));
	}

	public SessionState getLoggedIn() {
		return loggedIn != null ? loggedIn : (loggedIn = new StateLoggedIn(this));
	}

	public int log(String text) {
		System.out.println("Log: " + text);
		return 0;
	}

	public int sendToClient(String text) {
		output.write(text);
		output.flush();
		return 0;
	}
}
