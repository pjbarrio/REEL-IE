package edu.columbia.cs.ref.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.pattern.resources.Matchable;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.tool.io.Writable;

/**
 * Representation of a Document.
 * 
 * <br>
 * <br>
 * 
 * A Document is defined by its path, the name of the file and a list of Segments that represent
 * the content of the document. Additionally, a document may be annotated with information
 * like entities and relationships.
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Document implements Serializable, Writable, Matchable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -743800420105471562L;
	private String path;
	private String fileName;
	private List<Segment> text;
	private Map<String,Entity> entities;
	private Map<String,Relationship> relationships;
	
	/**
	 * protected constructor of the Document that keeps the internal state.
	 * Useful for the creation of subclasses.
	 * 
	 * @param d the document to be copied
	 */
	
	protected Document(Document d){
		
		this.path = d.path;
		this.fileName = d.fileName;
		this.text = d.text;
		this.entities = d.entities;
		this.relationships = d.relationships;
		
	}
	
	/**
	 * From text constructor for documents that are not stored in disk
	 * 
	 * @param text list of segments in the document
	 */
	public Document(List<Segment> text){
				
		this(null,null,text);

	}
	
	/**
	 * Constructor of the Document
	 * 
	 * @param path Path to the file containing the document
	 * @param fileName Name of the file containing the document
	 * @param text Segments that represent the content of the document
	 */
	public Document(String path, String fileName, List<Segment> text){
		setPath(path);
		setFilename(fileName);
		setPlainText(text);
		entities = new HashMap<String,Entity>();
		relationships = new HashMap<String,Relationship>();
	}
	
	/**
	 * Obtains the segments that represent the content of the document
	 * 
	 * @return the segments that represent the content of the document
	 */
	public List<Segment> getPlainText() {
		return text;
	}
	
	/**
	 * Sets the segments that represent the content of the document
	 * 
	 * @param text the segments to set
	 */
	public void setPlainText(List<Segment> text) {
		this.text = text;
	}
	
	/**
	 * Obtains the annotations of entities present in the document
	 * 
	 * @return The collection of entities that are present in the document
	 */
	public Collection<Entity> getEntities() {
		return entities.values();
	}
	
	
	/**
	 * Adds a new annotation of an entity present in the document
	 * 
	 * @param new annotation of an entity
	 */
	public void addEntity(Entity entity) {
		entities.put(entity.getId(),entity);
	}
	
	/**
	 * Obtains the entity annotation with a given id
	 * 
	 * @param id id of the annotation to be retrieved
	 * @return retrieved annotation
	 */
	public Entity getEntity(String id){
		return entities.get(id);
	}
	
	/**
	 * Obtains the annotations of relationships present in the document
	 * 
	 * @return The collection of relationships that are present in the document
	 */
	public Collection<Relationship> getRelationships() {
		return relationships.values();
	}
	
	/**
	 * Adds a new annotation of a relationship present in the document
	 * 
	 * @param new annotation of a relationship
	 */
	public void addRelationship(Relationship relationship) {
		relationships.put(relationship.getId(),relationship);
	}
	
	/**
	 * Obtains the relationship annotation with a given id
	 * 
	 * @param id id of the annotation to be retrieved
	 * @return retrieved annotation
	 */
	public Relationship getRelationship(String id){
		return relationships.get(id);
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return string representation of the object.
	 */
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("Segments:\n");
		int i=1;
		for(Segment seg : text){
			buf.append(i + ": " + seg + "\n");
			i++;
		}
		
		return buf.toString();
	}
	
	/**
	 * Obtains the substring of the document starting at a given offset and with a given length
	 * 
	 * @param offset start of the substring to be retrieved
	 * @param length size of the substring to be retrieved
	 * @return substring retrieved
	 */
	public String getSubstring(int offset, int length){
		for(Segment seg : text){
			if(seg.contains(offset,length)){
				return seg.getValue(offset,length);
			}
		}
		System.out.println(offset + " " + length);
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the path to the file containing the document
	 * 
	 * @param path the path to set
	 */
	public void setPath(String path){
		this.path=path;
	}
	
	/**
	 * Sets the name of the file containing the document
	 * 
	 * @param f the filename to set
	 */
	public void setFilename(String f) {
		this.fileName = f;
	}

	/**
	 * Obtains the name of the file containing the document
	 * 
	 * @return The name of the file containing the document
	 */
	public String getFilename() {
		return fileName;
	}
	
	/**
	 * Obtains the path to the file containing the document
	 * 
	 * @return The path to the file containing the document
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Obtains the name to be used when writing the file
	 * 
	 * @return The name to be used when writing the file
	 */
	@Override
	public String getWritableValue() {
		return fileName;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Document){
			Document other = (Document)obj;
			return path.equals(other.path) && fileName.equals(other.fileName);
		}
		return false;
	}
}
