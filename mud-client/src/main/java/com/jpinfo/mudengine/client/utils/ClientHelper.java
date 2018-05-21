package com.jpinfo.mudengine.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;


import org.springframework.messaging.support.MessageBuilder;

import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.common.message.Message;

import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.ip.tcp.connection.TcpConnection;


public class ClientHelper {
	
	public static final String HEADER_TOKEN = "Auth";

	public static final String GREETINGS_FILE = "greetings.txt";
	public static final String GOODBYE_FILE = "goodbye.txt";
	
	public static final String REGISTER_COMMAND = "register";
	public static final String QUIT_COMMAND = "quit";
	public static final String HELP_COMMAND = "help";
	public static final String LOGIN_COMMAND = "login";
	public static final String LOGOUT_COMMAND = "logout";
	public static final String CHANGEPROF_COMMAND = "change profile";
	public static final String ACTIVATE_COMMAND = "activate account";
	public static final String PASSWORD_COMMAND = "change password";
	public static final String CREATEBEING_COMMAND = "create character";
	public static final String SELECTBEING_COMMAND = "select character";
	public static final String DELETEBEING_COMMAND = "delete character";
	
	private static Scanner console = new Scanner(System.in);
	
	public static int readIntInput() {
		
		int result = console.nextInt();
		
		if (console.hasNextLine()) {
			console.nextLine();
		}
		
		
		return result;
	}
	
	public static String readString() {
		
		String result = console.nextLine();
		
		return result;
	}
	
	private static String formatMessage(Message m) {
		
		String response = null;
		
		if (m.getSenderCode()!=null) {
			response = String.format("[%s] %s: %s", m.getMessageDate(), m.getSenderName(), m.getMessage());
		} else {
			response = String.format("[%s]: %s", m.getMessageDate(), m.getMessage());
		}
		
		return response;
	}
	
	public static void sendMessage(TcpConnection conn, String message) throws Exception {
		
		// Build the message to send over tcp connection		
		org.springframework.messaging.Message<String> clientMessage = 
				MessageBuilder.withPayload(message)
					// .setHeader("headerName", "headerValue")
					.build();
		
		
		
		conn.send(clientMessage);
	}
	
	
	public static void sendMessage(ClientConnection c, String message) throws Exception {
		sendMessage(c.getConnection(), message);
	}
	
	public static void sendMessage(ClientConnection c, Message m) throws Exception {
		sendMessage(c, ClientHelper.formatMessage(m));
	}
	
	public static void sendFile(ClientConnection c, String filename) throws Exception {
		
		File f = new ClassPathResource(filename).getFile();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		
		try {
			while (reader.ready()) {
				sendMessage(c, reader.readLine());
			}
		} finally {
			reader.close();
		}
	}
}
