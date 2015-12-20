package edu.columbia.cs.api;

import java.util.List;

import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * Interface for Relationship Extractor
 * 
 * <br>
 * <br>
 * 
 * A class that implements this interface is responsible for extracting tuples from documents.
 * The method responsible for this task is the extractTuples method.
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface RelationshipExtractor<D> {

	/**
	 * Given an input document d this method is responsible for extracting all the relationships
	 * between entities of the document
	 * 
	 * @param d the document that contains the information to be extracted
	 */
	public List<Relationship> extractTuples(D d);
	
}
