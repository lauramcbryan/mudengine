package com.jpinfo.mudengine.client.utils;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;


import org.springframework.messaging.support.MessageBuilder;

import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.player.Player;

import org.springframework.core.io.ClassPathResource;


public class ClientHelper {
	
	public static final Locale DEFAULT_LOCALE = Locale.US;
	
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
	
	
	private ClientHelper() { }
	
	
	public static int readIntInput() {
		
		int result = console.nextInt();
		
		if (console.hasNextLine()) {
			console.nextLine();
		}
		
		
		return result;
	}
	
	public static String readString() {
		
		return console.nextLine();
	}
	
	public static List<String> padMultinelineString(String original, int desiredWidth) {

		List<String> resultList = new ArrayList<>();

		// Starts with the full sample
		StringBuilder str = new StringBuilder(original);
		
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
		
		StringBuilder result = new StringBuilder(original);

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
			response = String.format("[%s] %s: %s", m.getMessageDate(), m.getSenderName(), m.getContent());
		} else {
			response = String.format("[%s]: %s", m.getMessageDate(), m.getContent());
		}
		
		return response;
	}
	
	private static void internalSendMessage(ClientConnection c, String message, boolean includeCRLF) throws Exception {
		
		String effectiveMessage = c.getLocalizedMessage(message) + (includeCRLF ? "\r\n" : "");
		
		// Build the message to send over tcp connection		
		org.springframework.messaging.Message<String> clientMessage = 
				MessageBuilder.withPayload(effectiveMessage)
					// .setHeader("headerName", "headerValue")
					.build();
		
		c.getConnection().send(clientMessage);
	}
	
	/**
	 * Sends a message to the client, appending the \r\n terminator
	 * 
	 * @param c
	 * @param message
	 * @throws Exception
	 */
	public static void sendMessage(ClientConnection c, String message) throws Exception {
		internalSendMessage(c, message, true);
	}
	
	/**
	 * Sends a notification message from the game engine to the client
	 * @param c
	 * @param m
	 * @throws Exception
	 */
	public static void sendMessage(ClientConnection c, Message m) throws Exception {
		internalSendMessage(c, ClientHelper.formatMessage(m), true);
	}

	/**
	 * Sends a data request message.  Doesn't append the \r\n terminator.
	 * @param c
	 * @param message
	 * @throws Exception
	 */
	public static void sendRequestMessage(ClientConnection c, String message) throws Exception {
		internalSendMessage(c, message, false);
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
	 * List available beings for that player.
	 * 
	 * @param client
	 * @throws Exception
	 */
	public static String listAvailableBeings(ClientConnection c, Player playerData, Optional<Long> selectedBeing) {
		
		StringBuilder m = new StringBuilder(); 
		
		m
			.append(c.getLocalizedMessage(LocalizedMessages.COMMAND_SELECT_AVAILABLE))
		.append(ClientHelper.CRLF);
		
		playerData.getBeingList().stream()
			.forEach(d -> 
				
				m
					.append(ClientHelper.CRLF)
					.append("\t")
					.append(d.getBeingCode())
					.append(" - ")
					.append(d.getBeingName())
					.append(" (")
					.append(d.getBeingClass())
					.append(") ")
					.append((selectedBeing.isPresent() && d.getBeingCode().equals(selectedBeing.get()) ? 
							c.getLocalizedMessage(LocalizedMessages.COMMAND_SELECT_ACTIVE):""))
				);
		
		if (playerData.getBeingList().isEmpty()) {
			m.append("\r\n--- "+ c.getLocalizedMessage(LocalizedMessages.NONE_MESSAGE) +" \r\n");
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
		
		StringBuilder m = new StringBuilder();
		
		m
			.append(client.getLocalizedMessage(LocalizedMessages.COMMAND_CREATE_AVAILABLE))
			.append(ClientHelper.CRLF);
		
		beingClassList.forEach(d -> 
			
			m.append(ClientHelper.CRLF)
				.append(d.getBeingClassCode())
				.append(" - ")
				.append(d.getName())
				.append(ClientHelper.CRLF)
				.append(d.getDescription())
				.append(ClientHelper.CRLF)
		);
		
		if (beingClassList.isEmpty()) {
			m.append("--- "+ client.getLocalizedMessage(LocalizedMessages.NONE_MESSAGE));
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
	public static String returnFormattedPlayerData(ClientConnection c, Player playerData, Optional<Long> activeBeingCode) {
		
		StringBuilder m = new StringBuilder();
		
		m
			.append(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_PLAYER))
		.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_USERNAME) + playerData.getUsername(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_STATUS) + playerData.getStrStatus(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);

	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_EMAIL)+ playerData.getEmail(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_LOCALE)+ playerData.getLocale(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);

		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_SELECT_AVAILABLE), 75))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);
	
		playerData.getBeingList().stream()
			.forEach(d -> 
				
				m
					.append(ClientHelper.BOX_LEFT)
						.append(
								ClientHelper.padString("\t" + 
										d.getBeingCode()+ " - "+ 
										d.getBeingName()+ 
										" (" + d.getBeingClass() + ")" +
										(activeBeingCode.isPresent() && d.getBeingCode().equals(activeBeingCode.get()) ?
												c.getLocalizedMessage(LocalizedMessages.COMMAND_SELECT_ACTIVE): "") 
									, 70))
					.append(ClientHelper.BOX_RIGHT)
				.append(ClientHelper.CRLF)
		);
	
		if (playerData.getBeingList().isEmpty()) {
			m
				.append(ClientHelper.BOX_LEFT)
					.append(ClientHelper.padString("\t" + c.getLocalizedMessage(LocalizedMessages.NONE_MESSAGE), 70))
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
	public static String returnFormattedBeingData(ClientConnection c, Being activeBeing) {

		StringBuilder m = new StringBuilder();

		m
			.append(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_BEING))
		.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_NAME)+ activeBeing.getName(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_CLASS)+ activeBeing.getBeingClassCode(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);
	
		
		m.append(ClientHelper.BOX_FRAME)
			.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_ATTRS), 75))
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
				.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHOAMI_SKILLS), 75))
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
			StringBuilder skillLine = new StringBuilder() 
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
	public static String returnFormattedPlaceData(ClientConnection c, Place currentPlace) {
		
		StringBuilder m = new StringBuilder();

		m
			.append(ClientHelper.CRLF)
			.append(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHEREAMI_START))
			.append(currentPlace.getPlaceClass().getName());
		
		
		m
			.append(ClientHelper.CRLF)
			.append(ClientHelper.BOX_FRAME);
		
		
		List<String> lstDescription = ClientHelper.padMultinelineString(
				currentPlace.getPlaceClass().getDescription(), 75);
		
		lstDescription.stream().forEach(d -> 
			
			m
				.append(ClientHelper.CRLF)
				.append(ClientHelper.BOX_LEFT)
				.append(d)
				.append(ClientHelper.BOX_RIGHT)
		);
		
		m
		.append(ClientHelper.CRLF)
		.append(ClientHelper.BOX_FRAME);
		
		// Exits
		m
		.append(ClientHelper.CRLF)
		.append(ClientHelper.BOX_LEFT)
			.append(ClientHelper.padString(c.getLocalizedMessage(LocalizedMessages.COMMAND_WHEREAMI_EXITS), 75))
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
