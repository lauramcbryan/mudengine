package com.jpinfo.mudengine.client.handlers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.ClientLocalizedMessages;

@Component
@Qualifier("admin-commands")
public class AdminCommandHandler extends BaseCommandHandler {
	
	private static final Logger log = LoggerFactory.getLogger(AdminCommandHandler.class);

	public static final int SUDO_COMMAND = 997;
	public static final int SESSION_LIST_COMMAND = 998;
	public static final int SESSION_DROP_COMMAND = 999;

	@Value("${sudo.credential}")
	private String sudoCredential;
	
	@Autowired
	private MudClientGateway gateway;
	
	@Autowired
	private TcpNetServerConnectionFactory connFactory;
	
	
	private void handleSudoLoginCommand(ClientConnection client, CommandState command) throws ClientException {
		
		String credential = command.getParamValue("password");
		
		if (credential.equals(sudoCredential)) {
			
			client.sendMessage("Administrator powers granted.  Use them with care.");
			client.setAdmin(true);
		} else {
			client.sendMessage("Administrator powers NOT granted.");
		}
	}
	
	private void handleSessionListCommand(ClientConnection client) {

		for(String sessionId: gateway.getActiveConnections().keySet()) {
			
			ClientConnection session = gateway.getActiveConnections().get(sessionId);
			
			StringBuilder message = new StringBuilder();
			
			message.append("\r\n\r\nsessionId: ").append(sessionId);
			message.append("\r\nlogged: ").append(session.isLogged() ? "yes":"no");

			session.getPlayerData().ifPresent(d -> 
				message.append("\tusername: ").append(d.getUsername())
			);
			
			message.append("\tcommand: ");
			
			if (session.getCurCommand()!=null)
				message.append(session.getCurCommand().getVerb());
			else
				message.append("none");
			
			session.getActiveBeing().ifPresent(d -> {

				message
					.append("\tbeing: (id=").append(d.getCode())
					.append(", name=").append(d.getName());

					session.getCurPlace().ifPresent(f -> 
						message.append(", place: (id=").append(f.getCode())
							.append(", name=").append(f.getName())
							.append(")")
						);
				
				message.append(")");
				
			});
			
			client.sendMessage(message.toString());
		}
	}

	private void handleSessionDropCommand(CommandState command) throws ClientException {

		String sessionId = command.getParamValue("sessionId");
		
		ClientConnection session = gateway.getActiveConnections().get(sessionId);
		
		try {
			session.sendFile(ClientHelper.GOODBYE_FILE);
			
		} catch(IOException e) {
			
			// Disregard this exception, just log it
			log.error("Error trying to drop session");
		}
		
		connFactory.closeConnection(session.getConnection().getConnectionId());
		
	}
	
	
	/**
	 * Main method for handling all ADMIN commands.
	 * These commands are handled internally by the client.
	 * Administrative tasks and simple requests are covered here.
	 * 
	 * @param client - object with player info
	 * @param command - command being processed
	 * @throws Exception
	 */
	@Override
	public void handleCommand(ClientConnection client, CommandState command) throws ClientException {
		

		try {
		
			switch(command.getCommand().getCommandId()) {
			
				case AdminCommandHandler.SESSION_LIST_COMMAND:
					
					if (client.isAdmin())
						handleSessionListCommand(client);
					else
						throw new ClientException(ClientLocalizedMessages.COMMAND_ONLY_ADMIN);
					
					break;			
					
				case AdminCommandHandler.SESSION_DROP_COMMAND:
					
					if (client.isAdmin())
						handleSessionDropCommand(command);
					else
						throw new ClientException(ClientLocalizedMessages.COMMAND_ONLY_ADMIN);
					
					break;
					
				case AdminCommandHandler.SUDO_COMMAND:
					handleSudoLoginCommand(client, command);
					break;
				default: 
					client.sendMessage(ClientLocalizedMessages.COMMAND_NOT_SUPPORTED);
				
			}
		} finally {
			client.setCurCommandState(null);
		}
	}

}
