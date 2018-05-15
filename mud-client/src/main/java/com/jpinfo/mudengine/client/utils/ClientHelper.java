package com.jpinfo.mudengine.client.utils;

import java.util.Scanner;


import org.springframework.messaging.support.MessageBuilder;

import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.common.message.Message;
import org.springframework.integration.ip.tcp.connection.TcpConnection;


public class ClientHelper {
	
	public static final String HEADER_TOKEN = "Auth";

	public static final String GREETINGS = "Welcome to the mudengine";

	public static final String GOODBYE = "Good bye";
	
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
}
