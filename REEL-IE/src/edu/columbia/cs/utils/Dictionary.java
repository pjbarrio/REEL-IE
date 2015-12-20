package edu.columbia.cs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Dictionary {

	private Map<String, Set<String>> dictionary;
	private String separatorRegEx;
	private String name;

	public Dictionary(File dictionary, String separatorRegEx, String name){
		
		this.separatorRegEx = separatorRegEx;
		
		this.name = name;
		
		loadDictionary(dictionary);
		
	}
	
	public String getName(){
		return name;
	}
	
	private void loadDictionary(File dictionaryFile) {
		
		dictionary = new HashMap<String, Set<String>>();
		
		try {
		
			BufferedReader br = new BufferedReader(new FileReader(dictionaryFile));
			
			String line;
			
			while ((line=br.readLine()) != null){
				
				String[] spl = line.split(separatorRegEx);
				
				Set<String> aliases = new HashSet<String>();
				
				for (int i = 0; i < spl.length; i++) {
					aliases.add(spl[i]);
				}
				
				for (int i = 0; i < spl.length; i++) {
					
					dictionary.put(spl[i], aliases);
					
				}
				
			}
				
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Set<String> getEntries(){
		return dictionary.keySet();
	}
	
	public Set<String> getAliases(String key){
		return dictionary.get(key);
	}


}
