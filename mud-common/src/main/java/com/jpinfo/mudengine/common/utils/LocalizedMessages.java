package com.jpinfo.mudengine.common.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.security.core.context.SecurityContextHolder;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.security.domain.MudUserDetails;

public class LocalizedMessages {
	
	public static final String API_ERROR_MESSAGE = "api.error.message";
	public static final String KEY_NOT_FOUND = "key.not.found";
	
	public static final String PLACE_NOT_FOUND = "place.not.found";
	public static final String PLACE_CLASS_NOT_FOUND = "place.class.not.found";
	public static final String PLACE_EXIT_EXISTS = "place.exit.already.exists";
	
	public static final String ITEM_NOT_FOUND = "item.not.found";
	public static final String ITEM_CLASS_NOT_FOUND = "item.class.not.found";
	public static final String ITEM_NO_OWNER= "item.no.owner";
	public static final String ITEM_BOTH_OWNER = "item.both.owner";
	
	
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
	public static final String PLAYER_ACTIVATION_SUBJECT = "player.activation.subject";
	
	
	public static final String PLAYER_ACTIVE_STATUS = "player.active.status";
	public static final String PLAYER_BANNED_STATUS = "player.banned.status";
	public static final String PLAYER_BLOCKED_STATUS = "player.blocked.status";
	public static final String PLAYER_INACTIVE_STATUS = "player.inactive.status";
	public static final String PLAYER_PENDING_STATUS = "player.pending.status";
	public static final String PLAYER_UNKNOWN_STATUS = "player.unknown.status";
	
	public static final String SESSION_NOT_FOUND = "session.not.found";
	public static final String SESSION_BEING_NOT_FOUND = "session.being.not.found";
	
	
	public static final String ACTION_REFUSED = "action.refused";
	public static final String ACTION_TARGET_NOT_FOUND = "action.target.not.found";
	

	private static final Map<Locale, ResourceBundle> bundles = new HashMap<>();
	
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
		
		Locale locale = getLocale();
		
		bundles.computeIfAbsent(locale, d ->
			ResourceBundle.getBundle("messages", d)
		);

		try {
			return String.format(bundles.get(locale).getString(key), params);
		} catch(MissingResourceException e) {
			return String.format(key, params);
		}
			
	}	
	
	/**
	 * Returns the locale to be used.
	 * If the call is authenticated, that will be the player's locale.
	 * If the call is anonymous, or any other error arrises
	 * @return
	 */
	private static Locale getLocale() {
		
		try {
			MudUserDetails uDetails =  (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
			
			Player playerData = uDetails.getPlayerData();
			
			if (playerData!=null)
				return Locale.forLanguageTag(playerData.getLocale());
			else
				return Locale.forLanguageTag(CommonConstants.DEFAULT_LOCALE);
			
		} catch(RuntimeException e) {
			return Locale.forLanguageTag(CommonConstants.DEFAULT_LOCALE);
		}
	}
}
