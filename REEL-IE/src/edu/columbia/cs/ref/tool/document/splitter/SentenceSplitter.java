package edu.columbia.cs.ref.tool.document.splitter;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Sentence;

/**
 * The Interface SentenceSplitter represents objects that can be used to perform
 * sentence splitting tasks.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface SentenceSplitter {
	
	/**
	 * Splits the input document into sentences
	 *
	 * @param d input document
	 * @return the sentences of the input document
	 */
	public Sentence[] split(Document d);
}
