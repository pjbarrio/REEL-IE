package edu.columbia.cs.ref.tool.loader.document;

import java.io.File;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.loader.Loader;

/**
 * Abstract class that represents the loader for a collection
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class DocumentLoader extends Loader<Document> {
	
	protected Set<RelationshipType> relationshipTypes;
	
	
	/**
	 * Constructor of the loader
	 * 
	 * @param relationshipTypes Represents the types of relationships to be extracted from the collection
	 * including the constraints that they must fulfill
	 */
	public DocumentLoader(Set<RelationshipType> relationshipTypes){
		this.relationshipTypes=relationshipTypes;
	}
	
	/**
	 * Method that loads a set of documents given a File that represents the directory of the collection
	 * 
	 * @param file Represents the directory of the collection
	 * @return a set of Documents representing the documents of a collection
	 */
	public abstract Set<Document> load(File file);
	
}
