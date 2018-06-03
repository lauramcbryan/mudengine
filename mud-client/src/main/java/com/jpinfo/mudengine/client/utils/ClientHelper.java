package com.jpinfo.mudengine.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


import org.springframework.messaging.support.MessageBuilder;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandParamState;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.player.Player;

import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.ip.tcp.connection.TcpConnection;


public class ClientHelper {
	
	public static final String CLIENT_TYPE = "text/plain";
	public static final String CRLF = "\r\n";
	

	public static final String GREETINGS_FILE = "greetings.txt";
	public static final String GOODBYE_FILE = "goodbye.txt";
	
	public static final String BOX_FRAME = "+-----------------------------------------------------------------------------+";
	public static final String BOX_LEFT = "| ";
	public static final String BOX_CENTER = " | ";
	public static final String BOX_RIGHT = " |";
	public static final String SPACE_FILLER = "                                                                                ";
		
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
	
	public static List<String> padMultinelineString(String original, int desiredWidth) {

		List<String> resultList = new ArrayList<String>();

		// Starts with the full sample
		StringBuffer str = new StringBuffer(original);
		
		// while the sample length is bigger than desiredWidth
		// cut slices to feed the result list
		while (str.length()>desiredWidth) {
			
			// Put the slice in result list
			resultList.add(str.substring(0, desiredWidth));
			
			// Remove from the sample
			str.delete(0, desiredWidth);
		}
		
		// here we may still have a pice of the sample
		if (str.length()>0) {
			resultList.add(
					ClientHelper.padString(str.toString(), desiredWidth));
		}

		return resultList;
	}

	public static String padString(int original, int desiredLength) {
		return ClientHelper.padString(String.valueOf(original), desiredLength);
	}
	
	public static String padString(String original, int desiredLength) {
		
		StringBuffer result = new StringBuffer(original);

		// if the original string is bigger than the desired
		if (result.length()>desiredLength) {
			
			// truncate until the desired length
			result.setLength(desiredLength-3);
			
			// append some dots to depict that the entry is truncated
			result.append("...");
		} else 
			// original string smaller than the desired
			if (result.length()<desiredLength) {
				
				// pad with some spaces
				result.append(SPACE_FILLER.substring(0, desiredLength - result.length()));
		}

		return result.toString();
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
	
	private static void internalSendMessage(TcpConnection conn, String message) throws Exception {
		
		// Build the message to send over tcp connection		
		org.springframework.messaging.Message<String> clientMessage = 
				MessageBuilder.withPayload(message)
					// .setHeader("headerName", "headerValue")
					.build();
		
		conn.send(clientMessage);
	}
	
	/**
	 * Sends a message to the client, appending the \r\n terminator
	 * 
	 * @param c
	 * @param message
	 * @throws Exception
	 */
	public static void sendMessage(ClientConnection c, String message) throws Exception {
		internalSendMessage(c.getConnection(), message + "\r\n");
	}
	
	/**
	 * Sends a notification message from the game engine to the client
	 * @param c
	 * @param m
	 * @throws Exception
	 */
	public static void sendMessage(ClientConnection c, Message m) throws Exception {
		sendMessage(c, ClientHelper.formatMessage(m));
	}

	/**
	 * Sends a data request message.  Doesn't append the \r\n terminator.
	 * @param c
	 * @param message
	 * @throws Exception
	 */
	public static void sendRequestMessage(ClientConnection c, String message) throws Exception {
		internalSendMessage(c.getConnection(), message);
	}

	/**
	 * Sends a text file to the client.
	 * @param c
	 * @param filename
	 * @throws Exception
	 */
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
	
	/**
	 * Get the value entered for a command parameter.
	 * 
	 * @param command
	 * @param key
	 * @param returnClass
	 * @return
	 * @throws ClientException
	 */
	public static <T> T getParamValue(CommandState command, String key, Class<T> returnClass) throws ClientException  {
		
		String paramValue = getParamValue(command, key);
		
		if (paramValue!=null) {
			try {
				return returnClass.getConstructor(String.class).newInstance(paramValue);
			} catch (Exception e) {
				
				throw new ClientException("System error retrieving parameter values.");
			}
		}
		
		
		return null;
		
	}	
	
	/**
	 * Get the value entered for a command parameter.
	 *  
	 * @param command
	 * @param key
	 * @return
	 */
	public static String getParamValue(CommandState command, String key) {

		Optional<CommandParamState> foundParam = command.getParameters().stream()
				.filter(d -> d.getParameter().getName().equals(key))
				.findFirst();
		
		return (foundParam.isPresent() ? foundParam.get().getEffectiveValue(): null);
	}
	
	/**
	 * List available beings for that player.
	 * 
	 * @param client
	 * @throws Exception
	 */
	public static String listAvailableBeings(Player playerData, Optional<Long> selectedBeing) {
		
		StringBuffer m = new StringBuffer(); 
		
		m
			.append("Available beings:")
		.append(ClientHelper.CRLF);
		
		playerData.getBeingList().stream()
			.forEach(d -> {
				
				m
					.append(ClientHelper.CRLF)
					.append("\t")
					.append(d.getBeingCode())
					.append(" - ")
					.append(d.getBeingName())
					.append(" (")
					.append(d.getBeingClass())
					.append(") ")
					.append((selectedBeing.isPresent() && d.getBeingCode().equals(selectedBeing.get()) ? "<ACTIVE>":""))
					;
		});
		
		if (playerData.getBeingList().isEmpty()) {
			m.append("\r\n--- None \r\n");
		}
		
		return m.toString();
	}
	
	/**
	 * List available being classes.
	 * 
	 * @param client
	 * @param beingClassList
	 * @throws Exception
	 */
	public static String listAvailableBeingClasses(ClientConnection client, List<BeingClass> beingClassList) {
		
		StringBuffer m = new StringBuffer();
		
		m
			.append("Available classes: ")
			.append(ClientHelper.CRLF);
		
		beingClassList.forEach(d -> {
			
			m.append(ClientHelper.CRLF)
				.append(d.getBeingClass())
				.append(" - ")
				.append(d.getName())
				.append(ClientHelper.CRLF)
				.append(d.getDescription())
				.append(ClientHelper.CRLF)
				;
		});
		
		if (beingClassList.isEmpty()) {
			m.append("--- None");
		}
		
		return m.toString();

	}
	
	/**
	 * Formats the player information in format below:
	 * 
00000000001111111111222222222233333333334444444444555555555566666666667777777777
01234567890123456789012345678901234567890123456789012345678901234567890123456789
+-----------------------------------------------------------------------------+
| Name: <userName>                     | Status: <strStatus>                  |
| Email: <email>                       | Locale: <locale>                     |
+-----------------------------------------------------------------------------+
| Available beings:                                                           |
| 	SomeName (someClass)                                                      |
| 	SomeName (someClass)                                                      |
+-----------------------------------------------------------------------------+
	 * 
	 * @param playerData
	 * @return
	 */
	public static String returnFormattedPlayerData(Player playerData, Optional<Long> activeBeingCode) {
		
		StringBuffer m = new StringBuffer();
		
		m
			.append("Player:")
		.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString("Username: "+ playerData.getUsername(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString("Status: "+ playerData.getStrStatus(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);

	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString("Email: "+ playerData.getEmail(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString("Locale: "+ playerData.getLocale(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);

		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString("Available beings:", 75))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);
	
		playerData.getBeingList().stream()
			.forEach(d -> {
				
				m
					.append(ClientHelper.BOX_LEFT)
						.append(
								ClientHelper.padString("\t" + 
										d.getBeingCode()+ " - "+ 
										d.getBeingName()+ 
										" (" + d.getBeingClass() + ")" +
										(activeBeingCode.isPresent() && d.getBeingCode().equals(activeBeingCode.get()) ? " <ACTIVE>": "") 
									, 70))
					.append(ClientHelper.BOX_RIGHT)
				.append(ClientHelper.CRLF);
		});
	
		if (playerData.getBeingList().isEmpty()) {
			m
				.append(ClientHelper.BOX_LEFT)
					.append(ClientHelper.padString("\tNone", 70))
				.append(ClientHelper.BOX_RIGHT)
			.append(ClientHelper.CRLF);
		}
		
		m.append(ClientHelper.BOX_FRAME)
			.append(ClientHelper.CRLF);
		
		
		return m.toString();
	}
	
	/**
	 * Formats being information in format below:
	 * 
00000000001111111111222222222233333333334444444444555555555566666666667777777777
01234567890123456789012345678901234567890123456789012345678901234567890123456789
+-----------------------------------------------------------------------------+
| Name: <beingName>                    | Class: <beingClass>                  |
+-----------------------------------------------------------------------------+
| Attrs:                                                                      |
|    Attr12 99 (+99)   AttrAA 99 (+99)   AttrAA 99 (+99)   AttrAA 99 (+99)    |
|    Attr12 99 (+99)   AttrAA 99 (+99)   AttrAA 99 (+99)   AttrAA 99 (+99)    |
| Skills:                                                                     |
|    someSkill (category) lvl <level> (+99)                                   |
|    someSkill (category) lvl <level> (+99)                                   |
+-----------------------------------------------------------------------------+  
	 * 
	 * @param activeBeing
	 * @return
	 */
	public static String returnFormattedBeingData(Being activeBeing) {

		StringBuffer m = new StringBuffer();

		m
			.append("Being:")
		.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString("Name: "+ activeBeing.getName(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString("Class: "+ activeBeing.getBeingClassCode(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);
	
		
		m.append(ClientHelper.BOX_FRAME)
			.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString("Attrs:", 75))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);
		
		
		
		// Start the first line for attributes
		m
			.append(ClientHelper.BOX_LEFT)
			.append("   ");
		

		// Passing the attribute list
		int attrCount = 0;
		for (String curAttrKey: activeBeing.getAttrs().keySet()) {
			
			// Retrieve the attribute effective value
			Integer curAttrValue = activeBeing.getAttrs().get(curAttrKey);
			
			float curAttrModifiers = 0.0f;

			// Calculate the amount of modifiers applied			
			for(BeingAttrModifier curModifier: activeBeing.getAttrModifiers()) {
				
				if (curModifier.getAttribute().equals(curAttrKey)) {
					curAttrModifiers +=curModifier.getOffset();
				}
				
			}

			// Assembly the line
			m
				.append(ClientHelper.padString(curAttrKey, 6))
				.append(" ")
				.append(ClientHelper.padString(curAttrValue, 2))
				.append(" (")
				.append(curAttrModifiers> 0f ? "+":"-")
				.append(ClientHelper.padString(Math.round(curAttrModifiers), 2))
				.append(")   ");
			
			
			// if the line is complete...
			if (++attrCount % 4 ==0) {
				
				// End the current line
				m
					.append(ClientHelper.BOX_RIGHT)
					.append(ClientHelper.CRLF);
				
				
				// Starts a new one
				m
					.append(ClientHelper.BOX_LEFT)
					.append("   ");
			}
		}
		
		// At the enf of this list, we must close the current line
		m
			.append(ClientHelper.padString(" ", (4 - (attrCount % 4)) * 18))
			.append(ClientHelper.BOX_RIGHT)
			.append(ClientHelper.CRLF);


		
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString("Skills:", 75))
			.append(ClientHelper.BOX_RIGHT)
			.append(ClientHelper.CRLF);
	
	
		// Start the first line for skills
		m
			.append(ClientHelper.BOX_LEFT)
			.append("   ");
	

		// Passing the skill list
		int skillCount = 0;
		for (String curSkillKey: activeBeing.getSkills().keySet()) {
			
			// Retrieve the skill effective value
			Integer curSkillValue = activeBeing.getSkills().get(curSkillKey);
			
			float curSkillModifiers = 0.0f;
	
			// Calculate the amount of modifiers applied			
			for(BeingSkillModifier curModifier: activeBeing.getSkillModifiers()) {
				
				if (curModifier.getSkillCode().equals(curSkillKey)) {
					curSkillModifiers +=curModifier.getOffset();
				}
				
			}
	
			// Assembly the line
			StringBuffer skillLine = new StringBuffer() 
				.append(curSkillKey)
				.append(" lvl ")
				.append(ClientHelper.padString(curSkillValue, 2))
				.append(" (")
				.append(curSkillModifiers> 0f ? "+":"-")
				.append(ClientHelper.padString(Math.round(curSkillModifiers), 2))
				.append(")");
			
			m.append(ClientHelper.padString(skillLine.toString(), 36));
			
			
			// if the line is complete...
			if (++skillCount % 2 ==0) {
				
				// End the current line
				m
					.append(ClientHelper.BOX_RIGHT)
					.append(ClientHelper.CRLF);
				
				
				// Starts a new one
				m
					.append(ClientHelper.BOX_LEFT)
					.append("   ");
			}
		}
	
		// At the enf of this list, we must close the current line
		m
			.append(ClientHelper.padString(" ", (2 - (skillCount % 2)) * 36))
			.append(ClientHelper.BOX_RIGHT)
			.append(ClientHelper.CRLF);
		
		
		// Ends the block
		m.append(ClientHelper.BOX_FRAME)
			.append(ClientHelper.CRLF);
		
		
		return m.toString();
		
	}
	
	
	/**
	 * Return place information in format below:
	 * 
	 * 
	 * @param currentPlace
	 * @return
	 */
	public static String returnFormattedPlaceData(Place currentPlace) {
		
		StringBuffer m = new StringBuffer();

		m
			.append(ClientHelper.CRLF)
			.append("Place: ")
			.append(currentPlace.getPlaceClass().getName());
		
		
		m
			.append(ClientHelper.CRLF)
			.append(ClientHelper.BOX_FRAME);
		
		
		List<String> lstDescription = ClientHelper.padMultinelineString(
				currentPlace.getPlaceClass().getDescription(), 75);
		
		lstDescription.stream().forEach(d -> {
			
			m
				.append(ClientHelper.CRLF)
				.append(ClientHelper.BOX_LEFT)
				.append(d)
				.append(ClientHelper.BOX_RIGHT)
			;
			
		});
		
		m
		.append(ClientHelper.CRLF)
		.append(ClientHelper.BOX_FRAME);
		
		// Exits
		m
		.append(ClientHelper.CRLF)
		.append(ClientHelper.BOX_LEFT)
			.append(ClientHelper.padString("Exits:", 75))
		.append(ClientHelper.BOX_RIGHT);
		
		for(String curDirection: currentPlace.getExits().keySet()) {
			
			PlaceExit curExit = currentPlace.getExits().get(curDirection);
			
			if (curExit.isVisible()) {
				
				m
				.append(ClientHelper.CRLF)
				.append(ClientHelper.BOX_LEFT)
					.append(ClientHelper.padString(
							"\t" + curDirection + " - " + curExit.getName(), 70))
				.append(ClientHelper.BOX_RIGHT);
			}
		}

		m
		.append(ClientHelper.CRLF)
		.append(ClientHelper.BOX_FRAME);
		
		return m.toString();
		
	}
}
