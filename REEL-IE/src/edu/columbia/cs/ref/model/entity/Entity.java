package edu.columbia.cs.ref.model.entity;

import java.io.Serializable;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.pattern.resources.Matchable;

/**
 * The Class Entity represents entities that are present in a text.
 * 
 * <br>
 * <br>
 * 
 * An entity is composed by five basic attributes:
 * 
 * <br>
 * <br>
 * 
 * 1) An unique identifier
 * 
 * <br>
 * <br>
 * 
 * 2) The entity type of the entity
 * 
 * <br>
 * <br>
 * 
 * 3) The starting index of the entity in the document
 * 
 * <br>
 * <br>
 * 
 * 4) The length of the entity
 * 
 * <br>
 * <br>
 * 
 * 5) The document where the entity is contained
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Entity implements Comparable<Entity>, Serializable, Matchable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3840011584730968663L;

	/** The Constant NULL_ENTITY. */
	public static final Entity NULL_ENTITY = new Entity("NULL","NULL",0,0,"NULL",null);
	
	/** The id. */
	private String id;
	
	/** The entity type. */
	private String entityType;
	
	/** The offset. */
	private int offset;
	
	/** The length. */
	private int length;
	
	/** The value. */
	private String value;
	
	/** The document. */
	private Document document;
	
	/**
	 * Instantiates a new Entity.
	 *
	 * @param id the id of the entity
	 * @param entityType the entity type
	 * @param offset the starting index of the entity in the document
	 * @param length the length of the entity
	 * @param value the value of the entity
	 * @param doc the document where the entity belongs
	 */
	public Entity(String id, String entityType, int offset, int length,
			String value, Document doc){
		setId(id);
		setEntityType(entityType);
		setOffset(offset);
		setLength(length);
		setValue(value);
		setDocument(doc);
	}
	
	/**
	 * Gets the id of the entity.
	 *
	 * @return the id of the entity
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id of the entity.
	 *
	 * @param id the new id of the entity
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the entity type.
	 *
	 * @return the entity type
	 */
	public String getEntityType() {
		return entityType;
	}
	
	/**
	 * Sets the entity type.
	 *
	 * @param entityType the new entity type
	 */
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	/**
	 * Gets the starting index of the entity in the text.
	 *
	 * @return the starting index of the entity in the text
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * Sets the starting index of the entity in the text
	 *
	 * @param offset starting index of the entity in the text
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	/**
	 * Gets the length of the entity.
	 *
	 * @return the length of the entity
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Sets the length of the entity.
	 *
	 * @param length length of the entity
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * Gets the value of the entity.
	 *
	 * @return the value of the entity
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value of the entity.
	 *
	 * @param value the new value of the entity
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Gets the document where the entity belongs.
	 *
	 * @return the document where the entity belongs
	 */
	public Document getDocument() {
		return document;
	}
	
	/**
	 * Sets the document where the entity belongs.
	 *
	 * @param document the new document where the entity belongs
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return entityType + "(" + value + ")";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Entity o) {
		int startIndex=offset;
		int endIndex=offset+length;
		int otherStartIndex=o.offset;
		int otherEndIndex=o.offset+o.length;
		if(startIndex<otherStartIndex){
			return -1;
		}else if(startIndex>otherStartIndex){
			return 1;
		}

		if(endIndex<otherEndIndex){
			return -1;
		}else if(endIndex>otherEndIndex){
			return 1;
		}

		return 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		
		return value.hashCode()*31 + entityType.hashCode();
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof Entity){
			
			Entity other = (Entity)o;
			
			if (offset != other.offset)
				return false;
			if (!value.equals(other.value)){
				return false;
			}
			if (!entityType.equals(other.entityType)){
				return false;
			}
		}else{
			return false;
		}
		return true;
	}
	
}
