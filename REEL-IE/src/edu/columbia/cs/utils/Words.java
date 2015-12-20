package edu.columbia.cs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class Words provides several methods to determine which words are valid
 * terms for the Search engine
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Words {

	/** The Constant sym. */
	private static final String[] sym = { "&", "_", ".", ",", "?", "!", ":", "-",
		"\\", "/", ">", "<", "'", "\"", "|", ")", "(", ";", "{", "}" , "[" , "]","^","—","·"};
	
	/** The Constant defaultStopWords. */
	private static final String defaultStopWords = "data/stopWords.txt";
	
	/** The stop words. */
	private static Set<String> stopWords = null;
	
	/** The non searchable symbol. */
	private static Set<String> nonSearchableSymbol = null;
	
	/**
	 * Initializes the set of searchable symbols and the list of stop words. If
	 * any of the inputs file is not null then default symbols are loaded.
	 *
	 * @param stopWordsFile the file with the stop words
	 * @param nonSearchableSymbols the file with the non searchable symbols
	 */
	public static void initialize(File stopWordsFile, File nonSearchableSymbols){
		if (nonSearchableSymbol == null){

			if (nonSearchableSymbols==null){
				nonSearchableSymbol = new HashSet<String>();
				
				for (int i = 0; i < sym.length; i++) {
					nonSearchableSymbol.add(sym[i]);
				}
			}else{
				loadNonSearchableSymbols(nonSearchableSymbols);
			}
		}
		if (stopWords == null){
			if (stopWordsFile == null)
				loadStopWords(new File(defaultStopWords));
			else
				loadStopWords(stopWordsFile);
		}
	}
	
	private static void loadStopWords(File stopWordsFile){
		
		stopWords = new HashSet<String>();
		
		try {
			loadContent(stopWordsFile,stopWords);
		} catch (IOException e) {
			stopWords = null;
		}
		
	}
	
	private static void loadContent(File words, Set<String> set) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(words));
		
		String line;
		
		while ((line = br.readLine())!=null){
			set.add(line.toLowerCase());
		}
		
		br.close();
		
	}

	private static void loadNonSearchableSymbols(File nonSearchableSymbols){
		
		nonSearchableSymbol = new HashSet<String>();
		
		try {
			loadContent(nonSearchableSymbols, nonSearchableSymbol);
		} catch (IOException e) {
			nonSearchableSymbol = null;
		}
		
	}

	/**
	 * Checks if the input word can be a query term.
	 *
	 * @param word the word that we want to know if can be a query term
	 * @return true, if the input word can be a query term
	 */
	public static boolean isSearchable(String word) {
				
		String lowerCase = word.toLowerCase();
		
		if (nonSearchableSymbol.contains(lowerCase) || stopWords.contains(lowerCase))
			return false;
		
		return true;
	}

	/**
	 * Returns the set of stop words
	 *
	 * @return the set of stop words
	 */
	public static Set<String> getStopWords() {
			
		if (stopWords == null){
			initialize(new File(defaultStopWords), null);
		}
		return stopWords;
	}
	
}
