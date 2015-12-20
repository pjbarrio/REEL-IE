package edu.columbia.cs.ref.model.constraint.relationship.impl;

import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Class OrRelationshipConstraint is an implementation of RelationshipConstraint
 * that is used to compose several other constraints. This constraint checks
 * if at least one of the constraints passed as input to the constructor checks
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OrRelationshipConstraint implements RelationshipConstraint{

	/** The constraints. */
	private RelationshipConstraint[] constraints;

	/**
	 * Instantiates a new or relationship constraint. It receives an undefined number
	 * of contraints that must be checked.
	 *
	 * @param constraints the constraints
	 */
	public OrRelationshipConstraint(RelationshipConstraint ... constraints){
		this.constraints = constraints;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.relations.RelationshipConstraint#checkConstraint(edu.columbia.cs.cg.relations.Relationship)
	 */
	@Override
	public boolean checkConstraint(Relationship rel) {
		
		for (RelationshipConstraint constraint : constraints) {
			
			if (constraint.checkConstraint(rel))
				return true;
			
		}
		
		return false;
		
	}

}
