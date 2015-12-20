package edu.columbia.cs.ref.tool.tagger.span;

/**
 * The Class TaggedSpan represents a text span that has a given semantic
 * label.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class TaggedSpan {

	/** The tag. */
	private String tag;

	/**
	 * Instantiates a new tagged span by assigning a label.
	 *
	 * @param tag the label to be assigned to the new span
	 */
	public TaggedSpan(String tag){
		this.tag = tag;
	}
	
	/**
	 * Gets the label assigned to this tagged span.
	 *
	 * @return the label assigned to this tagged span.
	 */
	public String getTag(){
		return tag;
	}
	
}
