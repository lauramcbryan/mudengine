package com.jpinfo.mudengine.common.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.security.core.context.SecurityContextHolder;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.utils.CommonConstants;

public class LocalizedMessages {
	
	public static final String KEY_NOT_FOUND = "key.not.found";
	
	public static final String PLACE_NOT_FOUND = "place.not.found";
	public static final String PLACE_CLASS_NOT_FOUND = "place.class.not.found";
	public static final String PLACE_EXIT_EXISTS = "place.exit.already.exists";
	
	public static final String ITEM_NOT_FOUND = "item.not.found";
	public static final String ITEM_CLASS_NOT_FOUND = "item.class.not.found";
	public static final String ITEM_NO_OWNER= "item.no.owner";
	
	
	public static final String BEING_NOT_FOUND = "being.not.found";
	public static final String BEING_CLASS_NOT_FOUND = "being.class.not.found";
	public static final String BEING_ACCESS_DENIED = "being.access.denied";
	public static final String BEING_NAME_IN_USE = "being.name.in.use";
	
	public static final String PLAYER_NOT_FOUND = "player.not.found";
	public static final String PLAYER_ACCESS_DENIED = "player.access.denied";
	public static final String PLAYER_NAME_IN_USE = "player.name.in.use";
	public static final String PLAYER_ACTIVATION_MISMATCH = "player.activation.mismatch";
	public static final String PLAYER_CHANGE_PASSWORD = "player.change.password";
	public static final String PLAYER_NO_LOGIN = "player.no.login";
	public static final String PLAYER_LOGIN_ERROR = "player.login.error";
	
	public static final String SESSION_NOT_FOUND = "session.not.found";
	public static final String SESSION_BEING_NOT_FOUND = "session.being.not.found";
	
	
	public static final String ACTION_REFUSED = "action.refused";
	

	private static final Map<String, ResourceBundle> bundles = new HashMap<>();
	
	/**
	 * Private constructor, just to avoid instantiation
	 */
	private LocalizedMessages() { }
	
	/**
	 * Get the localized message for the key provided.
	 * @param key
	 * @param params
	 * @return
	 */
	public static String getMessage(String key, Object... params) {
		
		String locale = getLocale();
		
		bundles.computeIfAbsent(locale, d ->
			ResourceBundle.getBundle("messages", Locale.forLanguageTag(d))
		);
		
		if (bundles.get(locale).getString(key)!=null)
			return String.format(bundles.get(locale).getString(key), params);
		else
			return String.format(bundles.get(locale).getString(LocalizedMessages.KEY_NOT_FOUND), key, locale);
	}	
	
	/**
	 * Returns the locale to be used.
	 * If the call is authenticated, that will be the player's locale.
	 * If the call is anonymous, or any other error arrises
	 * @return
	 */
	private static String getLocale() {
		
		try {
			MudUserDetails uDetails =  (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
			
			Optional<Player> playerData = uDetails.getPlayerData();
			
			if (playerData.isPresent())
				return playerData.get().getLocale();
			else
				return CommonConstants.DEFAULT_LOCALE;
			
		} catch(RuntimeException e) {
			return CommonConstants.DEFAULT_LOCALE;
		}
	}
}
