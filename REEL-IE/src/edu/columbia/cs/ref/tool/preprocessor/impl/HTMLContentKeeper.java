package edu.columbia.cs.ref.tool.preprocessor.impl;

import com.google.gdata.util.common.html.HtmlToText;

import edu.columbia.cs.ref.tool.preprocessor.Preprocessor;

/**
 * This class is an implementation of the Preprocessor interface.
 * 
 * This Preprocessor is able to extract the content of HTML files.
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class HTMLContentKeeper implements Preprocessor {

	/**
	 * This method is responsible for processing the content of a document and returns
	 * a transformed String that corresponds to a transformed version of the input content.
	 * 
	 * <br>
	 * <br>
	 * 
	 * This processor can be used to obtain the content of HTML files. To do that, we
	 * are calling the method HtmlToText.htmlToPlainText(content) from the
	 * Google Data Java Client Library
	 *
	 * @param content the content of document represented as a String
	 * @return the transformed version of the input content
	 */
	@Override
	public String process(String content) {
		

		return HtmlToText.htmlToPlainText(content);
		
		
	}

	
}
