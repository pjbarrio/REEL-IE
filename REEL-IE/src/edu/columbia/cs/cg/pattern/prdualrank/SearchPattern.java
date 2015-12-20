package edu.columbia.cs.cg.pattern.prdualrank;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.utils.Words;

/**
 * The Class SearchPattern represents a pattern that can be used for Document
 * Retrieval.
 * 
 * <br>
 * <br>
 * 
 * A SearchPattern is composed by several phrases that can be used to query for the
 * documents.
 *
 * @param <T> the type of document to be retrieved
 * @param <D> the 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class SearchPattern<T extends Document, D extends TokenizedDocument> extends Pattern<Document,TokenizedDocument> {

	/** The phrases. */
	private List<String[]> phrases;

	/**
	 * Instantiates a new search pattern by providing the set of phrases to be used
	 * as queries
	 *
	 * @param phrases the phrases to be used as queries
	 */
	public SearchPattern(List<String[]> phrases) {
		this.phrases = phrases;
		
	}

	/**
	 * Checks if is the search pattern is valid
	 * 
	 * <br>
	 * <br>
	 * 
	 * A search pattern is valid if:
	 * 
	 * 1) None of its phrases are stop words
	 * 
	 * <br>
	 * <br>
	 * 
	 * 2) It does not contain repeated phrases
	 * 
	 * <br>
	 * <br>
	 * 
	 * 3) The phrases do not overlap
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		
		for (String[] phrase : phrases) {
		
			if (!isSearchable(phrase))
				return false;
			
		}
		
		for (int i = 0;i < phrases.size()-1;i++) {
		
			String stringPhrase1 = getString(phrases.get(i));

			for (int j=i+1; j<phrases.size();j++) {
				
				if (Arrays.equals(phrases.get(i), phrases.get(j)))
					return false;
					
				String stringPhrase2 = getString(phrases.get(j));

				if (stringPhrase1.contains(stringPhrase2) || stringPhrase2.contains(stringPhrase1))
					return false;
					
			}
			
		}
		
		
		return true;
	}

	private static boolean isSearchable(String[] phrase) {
		
		for (int i = 0; i < phrase.length; i++) {
			
			if (!isSearchable(phrase[i]))
				return false;
			
		}
		
		for (int i = 0; i < phrase.length-1; i++) {
			for (int j = i+1; j < phrase.length; j++) {
				if (phrase[i].equals(phrase[j]))
					return false;
			}
		}
		
		return true;
		
	}

	private static boolean isSearchable(String word) {
		return Words.isSearchable(word);
	}

	private String getString(String[] phrase) {
		
		String ret = "";
		
		if (phrase.length != 0){
			
			for (String string : phrase) {
				
				ret = ret + " " + string;
				
			}
			
			ret = ret.substring(1);
			
		}
		
		return ret.toLowerCase();
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#findMatch(edu.columbia.cs.cg.document.Document)
	 */
	@Override
	public List<Document> findMatch(TokenizedDocument d) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#generateHashCode()
	 */
	@Override
	protected int generateHashCode() {
		
		int hashCode = 1;
		
		for (String[] phrase : phrases) {
			hashCode = 31*hashCode + Arrays.hashCode(phrase);
		}
		
		return hashCode;
		
	}
	
	/**
	 * Static method that checks is a given phrase can be used as a valid pattern.
	 *
	 * <br>
	 * <br>
	 * 
	 * See the description of the method isValid() to learn what is a valid pattern
	 *
	 * @param nGram the sequence that may be used as a phrase
	 * @return true, if nGram can form a valid pattern
	 */
	public static boolean isPatternizable(String[] nGram) {
		return isSearchable(nGram);
	}

	/**
	 * Gets the list of phrases from the search pattern
	 *
	 * @return the list of phrases from the search pattern
	 */
	public List<String[]> getNGrams(){
		return phrases;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#generateToString()
	 */
	@Override
	protected String generateToString() {
		String ret = "";
		
		for (String[] phrase : phrases) {
			ret = ret + " - " + Arrays.toString(phrase);
		}
		
		return ret;
	}
}
