package edu.columbia.cs.ref.model.constraint.relationship.impl;

import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Class AndRelationshipConstraint is an implementation of RelationshipConstraint
 * that is used to compose several other constraints. This constraint checks
 * if all the constraints passed as input to the constructor check
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class AndRelationshipConstraint implements RelationshipConstraint{

	/** The constraints. */
	private RelationshipConstraint[] constraints;

	/**
	 * Instantiates a new AndRelationshipConstraint. It receives an undefined number
	 * of contraints that must be checked.
	 *
	 * @param constraints the constraints
	 */
	public AndRelationshipConstraint(RelationshipConstraint ... constraints){
		this.constraints = constraints;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.relations.RelationshipConstraint#checkConstraint(edu.columbia.cs.cg.relations.Relationship)
	 */
	@Override
	public boolean checkConstraint(Relationship rel) {
		
		for (RelationshipConstraint constraint : constraints) {
			
			if (!constraint.checkConstraint(rel))
				return false;
			
		}
		
		return true;
		
	}

}
