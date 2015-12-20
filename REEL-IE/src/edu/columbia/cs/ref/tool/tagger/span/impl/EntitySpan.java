package edu.columbia.cs.ref.tool.tagger.span.impl;

import edu.columbia.cs.ref.tool.tagger.span.TaggedSpan;

/**
 * The Class EntitySpan represents a text span that corresponds to an entity in the text.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */

public class EntitySpan extends TaggedSpan{

	private String id;
	private String value;
	private int offset;
	private int length;

	/**
	 * Instantiates a new entity span.
	 *
	 * @param id The id of the entity
	 * @param tag the associated tag
	 * @param value The value of the span
	 * @param offset The starting index of the span in the text
	 * @param length The size of the span
	 */
	
	public EntitySpan(String id, String tag,String value, int offset, int length){
		super(tag);
		this.id = id;
		this.value = value;
		this.offset = offset;
		this.length = length;
	}
	
	/**
	 * Gets the value of the span
	 *
	 * @return the value of the span
	 */
	
	public String getValue() {
		return value;
	}

	/**
	 * Gets the length of the span
	 *
	 * @return the length of the span
	 */
	
	public int getLength() {
		return length;
	}

	/**
	 * Gets the starting index of the span in the document.
	 *
	 * @return the starting index of the span
	 */
	
	public int getOffset() {
		return offset;
	}

	/**
	 * Gets the unique identifier of the span
	 *
	 * @return the unique identifier of the span
	 */
	
	public String getId() {

		return id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return value + "-" + offset;
	}
	
	@Override
	public int hashCode() {
		return 31*(31 + offset) + length;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof EntitySpan){
			EntitySpan other = (EntitySpan)o;
			if ( other.offset != offset)
				return false;
			if ( other.length != length)
				return false;
			
			return true;
		}else{
			return false;
		}
		
	}
}