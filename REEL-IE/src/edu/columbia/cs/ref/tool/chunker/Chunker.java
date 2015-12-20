package edu.columbia.cs.ref.tool.chunker;

/**
 * The Interface Chunker represents objects that can be used to perform
 * chunking tasks.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface Chunker {
	
	/**
	 * Given a tokenization and the coresponding POS tags, it returns
	 * the chunking tags for each token
	 *
	 * @param toks the tokenization of a sentence
	 * @param tags the POS tags of a sentence
	 * @return the chunking tags
	 */
	public String[] chunk(String[] toks, String[] tags);
}
