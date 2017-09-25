package com.jpknox.server;

import com.jpknox.server.state.StateLoggedIn;
import com.jpknox.server.state.StateNotLoggedIn;
import com.jpknox.server.state.SessionState;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by joaok on 24/09/2017.
 */
public class FTPServer {

	private SessionState currentState;
	private ServerSocket serverSocket;
	private Socket clientConnection;
	private BufferedReader input;
	private PrintWriter output;
	private String clientName = "client";

	public FTPServer() {
		this.currentState = new StateNotLoggedIn();
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

			output.write("\r\n");
			output.flush();

			String dataFromClient = null;
			String dataToClient = null;
			while (true) {
				dataFromClient = input.readLine();
				log(clientName + ": " + dataFromClient);
				dataToClient = "Echo: " + dataFromClient;
				output.write(dataToClient + "\r\n");
				output.flush();

				if (dataFromClient.length() > 5 && dataFromClient.substring(0, 5).equals("USER ")) {
//					log("Username: " + dataFromClient.substring(5, dataFromClient.length()));
					currentState.user(this, dataFromClient.substring(5, dataFromClient.length())); //Extract username
				}
				if (dataFromClient.equals("quit")) {
					log(clientName + " disconnected.");
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

	public int log(String text) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date) + ":\t" + text);
		return 0;
	}

	public int sendToClient(String text) {
		output.write(text);
		output.flush();
		return 0;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
}
