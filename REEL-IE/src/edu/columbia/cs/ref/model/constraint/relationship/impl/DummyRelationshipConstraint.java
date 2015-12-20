package edu.columbia.cs.ref.model.constraint.relationship.impl;

import java.util.Set;

import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Class DummyRelationshipConstraint is an implementation of the RelationshipConstraint
 * that is actually a dummy check. All the relationships are accepted.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class DummyRelationshipConstraint implements RelationshipConstraint {
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.relations.RelationshipConstraint#checkConstraint(edu.columbia.cs.cg.relations.Relationship)
	 */
	public boolean checkConstraint(Relationship rel){
		return true;
	}
}
