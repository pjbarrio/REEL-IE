package edu.columbia.cs.ref.tool.postagger;

/**
 * The Interface POSTagger represents objects that can be used to perform
 * POS tagging tasks.
 * 
 * <br>
 * <br>
 * 
 * The only method that it forces to implement is the method tag which receives
 * as input a tokenized version of a text and returns an array of Strings which
 * associates a tag to each input token.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface POSTagger {
	
	/**
	 * Produces the POS tags for the input data
	 *
	 * @param input a tokenized version of a text
	 * @return an array of Strings which associates a tag to each input token
	 */
	public String[] tag(String[] input);
}
