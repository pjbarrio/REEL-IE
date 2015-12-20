package edu.columbia.cs.ref.model.pattern;

import java.util.List;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.pattern.resources.Matchable;

/**
 * A Pattern represents any object that can be matched in a given document.
 * 
 * <br>
 * <br>
 * 
 * A Pattern is parameterized by two types. The first is the type of result produced
 * by the match and the second is the type of document in which the pattern can be
 * applied.
 *
 * @param <T> the type of result produced by the match
 * @param <D> the type of document in which there can be a match
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class Pattern<T extends Matchable,D extends Document> {

	/** The hash code. */
	private int hashCode = -1;
	
	/** The string. */
	private String string = null;
	
	/**
	 * Abstract method that finds all matches in the input document
	 *
	 * @param d the document where we are looking for the matches
	 * @return the matched objects in document d
	 */
	public abstract List<T> findMatch(D d);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		
		if (hashCode == -1){
			hashCode = generateHashCode();
		}
		
		return hashCode;
	}

	/**
	 * Abstract function that must return an hash value for the pattern
	 *
	 * @return hash value for the object
	 */
	protected abstract int generateHashCode();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		Pattern<T, D> other = (Pattern<T, D>) obj;
		return this.hashCode() == other.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		if (string == null){
			string = generateToString();
		}
		return string;
		
	}

	/**
	 * Abstract function that must return a string representation of the pattern
	 *
	 * @return string representation of the pattern
	 */
	protected abstract String generateToString();
}
