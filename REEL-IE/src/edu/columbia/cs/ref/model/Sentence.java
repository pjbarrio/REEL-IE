package edu.columbia.cs.ref.model;

import java.io.Serializable;


/**
 * The Class Sentence represents sentences that are present in a document.
 * <br>
 * <br>
 * 
 * A sentence is composed by three basic attributes:
 * 
 * <br>
 * <br>
 * 
 * 1) The document where the sentence is contained
 * 
 * <br>
 * <br>
 * 
 * 2) The starting index of the sentence in the document
 * 
 * <br>
 * <br>
 * 
 * 3) The length of the sentence
 * 
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Sentence extends FeaturableObject implements Serializable {
	
	/** The d. */
	private Document d;
	
	/** The offset. */
	private int offset;
	//The length of the sentence comes from the text
	/** The length. */
	private int length;
	
	/**
	 * Instantiates a new sentence.
	 *
	 * @param d the d
	 * @param offset the offset
	 * @param length the length
	 */
	public Sentence(Document d, int offset, int length){
		this.d=d;
		this.offset=offset;
		this.length=length;
	}
	
	/**
	 * Sets the starting index of the sentence in the document.
	 *
	 * @param offset the new starting index of the sentence in the document
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	/**
	 * Gets the starting index of the sentence in the document.
	 *
	 * @return the starting index of the sentence in the document
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the length of the sentence.
	 *
	 * @param length the new length of the sentence
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Gets the length of the sentence.
	 *
	 * @return the length of the sentence
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Gets the value of the sentence.
	 *
	 * @return the value of the sentence
	 */
	public String getValue(){
		return d.getSubstring(getOffset(), getLength());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return d.getSubstring(getOffset(), getLength());
	}
	
	/**
	 * Merge.
	 *
	 * @param nextSentence the next sentence
	 * @return the sentence
	 */
	public Sentence merge(Sentence nextSentence){
		if(d!=nextSentence.d){
			throw new UnsupportedOperationException();
		}
		
		return new Sentence(d, offset, nextSentence.offset+nextSentence.length-offset);
	}
}
