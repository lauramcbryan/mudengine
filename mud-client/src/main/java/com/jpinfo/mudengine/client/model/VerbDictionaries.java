package com.jpinfo.mudengine.client.model;


import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

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
	
	private Map<String, VerbDictionary> dictionaries;
	
	@Autowired
	private ResourceLoader loader;
	
	@Autowired
	private MudengineApi api;

	public VerbDictionaries() {
		
		dictionaries = new HashMap<String, VerbDictionary>();
		
	}
	
	@PostConstruct
	public void initialize() {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		try {
			
			Resource[] jsonFiles = 
					ResourcePatternUtils.getResourcePatternResolver(loader).getResources("system-verbs*.json");
			
			
			for(Resource curDictionary: jsonFiles) {
				
				// Extract the locale name
				String justFileName = curDictionary.getFilename().split("\\.")[0];
				String[] fileParts = justFileName.split("_"); 
				String locale = (fileParts.length>1 ? fileParts[1]:ClientHelper.DEFAULT_LOCALE);

				// Load the contents
				VerbDictionary verbDictionary = 
						jsonMapper.readValue(
								curDictionary.getFile(), 
								new TypeReference<VerbDictionary>() {});

				System.out.print("Loading SYSTEM verb dictionary for locale "+locale+"... ");
				
				dictionaries.put(locale, verbDictionary);
				
				System.out.println("OK");
				
				
				// Load the GAME verb dictionary as well
				
				System.out.print("Loading GAME verb dictionary for locale "+locale+"... ");
				
				try {
				
					// Call the API to retrieve game list
					List<Command> gameCommandList = 
							api.getGameCommandList(locale);
					
					verbDictionary.getCommandList().addAll(gameCommandList);
					
					System.out.println("OK");
					
				} catch(Exception e) {
					
					System.out.println("Error loading verb dictionary: " + e.getMessage());
				}
				
				
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
	}
	
	public VerbDictionary getDictionary(String locale) {
		
		if (this.dictionaries.containsKey(locale))
			return this.dictionaries.get(locale);
		else
			return this.dictionaries.get(ClientHelper.DEFAULT_LOCALE);
	}
}
