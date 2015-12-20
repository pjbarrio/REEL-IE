package edu.columbia.cs.ref.model;

import java.io.Serializable;

/**
 * Representation of a Section of a document.
 * 
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Segment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6170689330249545921L;
	private String value;
	private int offset;
	
	/**
	 * Constructor of the Segment
	 * 
	 * @param value the text representation of the segment
	 * @param offset the offset that indicates the starting position of the segment in document
	 */
	public Segment(String value, int offset){
		this.setValue(value);
		this.setOffset(offset);
	}

	/**
	 * Sets the value of segment
	 * 
	 * @param value value to be set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Obtains the value of the segment
	 * 
	 * @return The value of the segment
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the starting position of the segment in the document
	 * 
	 * @param offset starting position to be set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Obtains the value of the starting position of the segment in the document
	 * 
	 * @return The offset of the starting position
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return string representation of the object.
	 */
	public String toString(){
		return value;
	}

	/**
	 * Verifies if the section starting in a given offset and with a given length is contained
	 * in this segment.
	 * 
	 * @param offset the offset that indicates the starting position of the section in document
	 * @param length the length of the section
	 * @return true if the section is contained in this segment and false otherwise
	 */
	public boolean contains(int offset, int length) {
		return offset>=getOffset() && length<=value.length()-(offset-getOffset());
	}
	
	/**
	 * Obtains the substring of the section starting at a given offset and with a given length
	 * 
	 * @param offset start of the substring to be retrieved
	 * @param length size of the substring to be retrieved
	 * @return substring retrieved
	 */
	public String getValue(int offset, int length){
		return value.substring(offset-getOffset(),offset-getOffset()+length);
	}
}
