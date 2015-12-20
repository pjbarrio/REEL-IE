package edu.columbia.cs.ref.tool.tokenizer;

import edu.columbia.cs.ref.model.Span;

/**
 * Tokenizer is an interface for objects that are responsible for splitting the text
 * into several tokens.
 * 
 * <br>
 * <br>
 * 
 * The only method that a Tokenizer needs to implement is the tokenize method which
 * receives a String representing the content of the text and returns an Array of
 * Span where each Span points to a word in the text.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface Tokenizer {

	/**
	 * Splits the content of a text into several tokens
	 *
	 * @param text the content of the text
	 * @return the Spans pointing out to wach word in the text
	 */
	public Span[] tokenize(String text);

}
