package edu.columbia.cs.ref.model.constraint.relationship;

import java.io.Serializable;
import java.util.Set;

import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Interface RelationshipConstraint represents a constraint over a given relationship.
 * 
 * <br>
 * <br>
 * 
 * This interface only demands the implementation of one method which is the checkConstraint
 * method which is responsible for checking if a given relationship fulfills the constraint.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface RelationshipConstraint extends Serializable{
	
	/**
	 * Checks if the relationship constraint is fulfilled on the input relationship.
	 *
	 * @param rel the relationship where the constraint is to be checked
	 * @return true, if successful
	 */
	public boolean checkConstraint(Relationship rel);
}
