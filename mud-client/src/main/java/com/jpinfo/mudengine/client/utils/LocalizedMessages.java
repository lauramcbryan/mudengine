package com.jpinfo.mudengine.client.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalizedMessages {
	
	public static final Logger log = LoggerFactory.getLogger(LocalizedMessages.class);
	
	public static final String GENERAL_ERROR_MESSAGE="general.error.message";
	public static final String API_ERROR_MESSAGE="api.error.message";
	
	public static final String ANONYMOUS_MESSAGE="anonymous.message";
	public static final String NO_BEING_MESSAGE="nobeing.message";
	public static final String COMMAND_UNKNOWN="command.unknown";


	public static final String COMMAND_ONLY_LOGGED="command.only.logged";
	public static final String COMMAND_NO_BEING="command.no.being";
	public static final String COMMAND_NOT_SUPPORTED="command.not.supported";
	public static final String COMMAND_UNKNOWN_BEING="command.unknown.being";
	public static final String COMMAND_INVALID_PARAMETER="command.invalid.parameter";

	public static final String COMMAND_REGISTER_START="account.register.start";
	public static final String COMMAND_REGISTER_OK="account.register.ok";

	public static final String COMMAND_PASSWORD_OK="change.password.ok";

	public static final String COMMAND_ACTIVATE_START="activate.account.start";
	public static final String COMMAND_ACTIVATE_OK="activate.account.ok";

	public static final String COMMAND_HELP_START="command.help.start";
	public static final String COMMAND_HELP_USAGE="command.help.usage";

	public static final String COMMAND_LOGIN_OK="login.ok";

	public static final String COMMAND_LOGOUT_OK="logout.ok";
	public static final String COMMAND_LOCALE_OK="locale.ok";
	
	public static final String COMMAND_SELECT_AVAILABLE="command.select.available";
	public static final String COMMAND_SELECT_ACTIVE="command.select.active";
	
	public static final String NONE_MESSAGE="none.message";
	public static final String COMMAND_CREATE_AVAILABLE="command.create.available";
	
	public static final String COMMAND_WHOAMI_USERNAME="command.whoami.username";
	public static final String COMMAND_WHOAMI_EMAIL="command.whoami.email";
	public static final String COMMAND_WHOAMI_STATUS="command.whoami.status";
	public static final String COMMAND_WHOAMI_LOCALE="command.whoami.locale";
	public static final String COMMAND_WHOAMI_PLAYER="command.whoami.player";
	public static final String COMMAND_WHOAMI_ATTRS="command.whoami.attrs";
	public static final String COMMAND_WHOAMI_NAME="command.whoami.name";
	public static final String COMMAND_WHOAMI_CLASS="command.whoami.class";
	public static final String COMMAND_WHOAMI_SKILLS="command.whoami.skills";
	public static final String COMMAND_WHOAMI_BEING="command.whoami.being";
	
	
	public static final String COMMAND_WHEREAMI_EXITS="command.whereami.exits";
	public static final String COMMAND_WHEREAMI_START="command.whereami.start";
	
	private static Map<String, ResourceBundle> bundles = new HashMap<>();

	private String locale;

	public LocalizedMessages(String strLocale) {
		
		this.locale = strLocale;
		
		bundles.computeIfAbsent(strLocale, d -> {
			ResourceBundle bundle = ResourceBundle.getBundle("messages",Locale.forLanguageTag(d));
			
			if ((bundle!=null) && (bundle.getLocale().toString().equals(d)))
				log.info("Bundle for locale {} loaded", d);
			else
				log.warn("Bundle for locale {} NOT LOADED!", d);
			
			return bundle;
		});
	}
	
	public String getMessage(String key) {
		
		if (bundles.get(locale).containsKey(key))
			return bundles.get(locale).getString(key);
		else
			return key;
	}	
}
