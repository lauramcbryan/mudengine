package com.jpinfo.mudengine.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.player.Player;


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

	private ClientHelper() { }
	
	
	private static List<String> padMultinelineString(String original, int desiredWidth) {

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

	private static String padString(long original, int desiredLength) {
		return ClientHelper.padString(String.valueOf(original), desiredLength);
	}
	
	private static String padString(String original, int desiredLength) {
		
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
			.append(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_PLAYER))
		.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_USERNAME) + playerData.getUsername(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_STATUS) + playerData.getStrStatus(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);

	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_EMAIL)+ playerData.getEmail(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_LOCALE)+ playerData.getLocale(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);

		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_SELECT_AVAILABLE), 75))
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
												c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_SELECT_ACTIVE): "") 
									, 70))
					.append(ClientHelper.BOX_RIGHT)
				.append(ClientHelper.CRLF)
		);
	
		if (playerData.getBeingList().isEmpty()) {
			m
				.append(ClientHelper.BOX_LEFT)
					.append(ClientHelper.padString("\t" + c.getLocalizedMessage(ClientLocalizedMessages.NONE_MESSAGE), 70))
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
			.append(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_BEING))
		.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_FRAME)
		.append(ClientHelper.CRLF);
	
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_NAME)+ activeBeing.getName(), 36))
			.append(ClientHelper.BOX_CENTER)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_CLASS)+ activeBeing.getClassCode(), 36))
			.append(ClientHelper.BOX_RIGHT)
		.append(ClientHelper.CRLF);
	
		
		m.append(ClientHelper.BOX_FRAME)
			.append(ClientHelper.CRLF);
		
		m
			.append(ClientHelper.BOX_LEFT)
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_ATTRS), 75))
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
			
			// Calculate the amount of modifiers applied
			// Last trend new code!  (doing the same stuff)
			double curAttrModifiers =
				activeBeing.getAttrModifiers().stream()
					.filter(e -> e.getCode().equals(curAttrKey))
					.mapToDouble(BeingAttrModifier::getOffset)
					.sum();

			// Last decade code.  And works.			
			for(BeingAttrModifier curModifier: activeBeing.getAttrModifiers()) {
				
				if (curModifier.getCode().equals(curAttrKey)) {
					curAttrModifiers +=curModifier.getOffset();
				}
			}

			// Last century code.  OMG, that STILL WORKS!			
			for(int k=0;k<activeBeing.getAttrModifiers().size();k++) {
				
				BeingAttrModifier curModifier = activeBeing.getAttrModifiers().get(k);
				
				if (curModifier.getCode().equals(curAttrKey)) {
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
				.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHOAMI_SKILLS), 75))
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
			
			double curSkillModifiers;

			// Calculate the amount of modifiers applied			
			curSkillModifiers =
				activeBeing.getSkillModifiers().stream()
					.mapToDouble(BeingSkillModifier::getOffset)
					.sum();
	
			
			for(BeingSkillModifier curModifier: activeBeing.getSkillModifiers()) {
				
				if (curModifier.getCode().equals(curSkillKey)) {
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
			.append(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHEREAMI_START))
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
			.append(ClientHelper.padString(c.getLocalizedMessage(ClientLocalizedMessages.COMMAND_WHEREAMI_EXITS), 75))
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
