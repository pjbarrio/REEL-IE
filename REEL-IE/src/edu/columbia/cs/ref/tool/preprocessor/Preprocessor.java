package edu.columbia.cs.ref.tool.preprocessor;

/**
 * The Interface Preprocessor.
 * 
 * The classes that implement Preprocessor transform the content of the text into
 * the format that corresponds to the input of the RE programs.
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface Preprocessor {

	/**
	 * This method is responsible for processing the content of a document and returns
	 * a transformed String that corresponds to a transformed version of the input content
	 *
	 * @param content the content of document represented as a String
	 * @return the transformed version of the input content
	 */
	public String process(String content);

}
