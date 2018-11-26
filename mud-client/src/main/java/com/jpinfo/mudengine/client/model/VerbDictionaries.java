package com.jpinfo.mudengine.client.model;


import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.client.api.MudengineApi;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.action.Command;

@Component
public class VerbDictionaries {
	
	private static final Logger log = LoggerFactory.getLogger(VerbDictionaries.class);
	
	private static final String FILE_PREFIX = "system-verbs_";
	
	private Map<Locale, VerbDictionary> dictionaries;
	
	@Autowired
	private ResourceLoader loader;
	
	@Autowired
	private MudengineApi api;

	public VerbDictionaries() {
		
		dictionaries = new HashMap<>();
		
	}
	
	@PostConstruct
	public void initialize() {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		try {
			
			Resource[] jsonFiles = 
					ResourcePatternUtils.getResourcePatternResolver(loader).getResources(FILE_PREFIX + "*.json");
			
			
			for(Resource curDictionary: jsonFiles) {
				
				// Extract the locale name
				String justFileName = curDictionary.getFilename().split("\\.")[0];
				String strLocale = justFileName.substring(FILE_PREFIX.length(), justFileName.length());
				Locale locale = Locale.forLanguageTag(strLocale);
				
				log.info("Loading SYSTEM verb dictionary for locale {}...", locale);				

				// Load the contents
				VerbDictionary verbDictionary = 
						jsonMapper.readValue(
								curDictionary.getInputStream(), 
								new TypeReference<VerbDictionary>() {});

				log.info("OK");

				// Load the GAME verb dictionary as well
				loadGameCommands(verbDictionary, locale);

				// Put in the dictionary list
				dictionaries.put(locale, verbDictionary);
			}
			
			
		} catch (Exception e) {
			
			log.error("General error", e);
		} 
	}
	
	public VerbDictionary getDictionary(Locale locale) {
		
		return this.dictionaries.getOrDefault(
				locale,
				this.dictionaries.get(ClientHelper.DEFAULT_LOCALE)
				);
	}
	
	
	private VerbDictionary loadGameCommands(VerbDictionary verbDictionary, Locale locale) {

		log.info("Loading GAME verb dictionary for locale {}... ", locale);
		
		try {
		
			// Call the API to retrieve game list
			List<Command> gameCommandList = 
					api.getGameCommandList(locale.toLanguageTag());
			
			verbDictionary.getCommandList().addAll(gameCommandList);
			
			log.info("OK");
			
		} catch(Exception e) {
			
			log.error("Error loading verb dictionary: ", e);
		}
		
		return verbDictionary;
	}
}
