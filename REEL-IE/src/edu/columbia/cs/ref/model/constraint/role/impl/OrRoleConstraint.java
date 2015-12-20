package edu.columbia.cs.ref.model.constraint.role.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Class OrRoleConstraint is an implementation of RoleConstraint
 * that is used to compose several other constraints. This constraint checks
 * if at least one of the constraints passed as input to the constructor checks
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OrRoleConstraint implements RoleConstraint {
	
	/** The constraints. */
	RoleConstraint[] constraints;
	
	/**
	 * Instantiates a new or role constraint. It receives an undefined number
	 * of contraints that must be checked.
	 *
	 * @param constraints the constraints
	 */
	public OrRoleConstraint(RoleConstraint ... constraints){
		
		this.constraints=constraints;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#checkConstraint(edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean checkConstraint(Entity role) {
		for(RoleConstraint c : constraints){
			if(c.checkConstraint(role)){
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#getCompatibleEntities(java.util.Set)
	 */
	@Override
	public Set<Entity> getCompatibleEntities(Set<Entity> entities) {
		Set<Entity> currentSet = new HashSet<Entity>();
		for(RoleConstraint c : constraints){
			currentSet.addAll(c.getCompatibleEntities(entities));
		}
		return currentSet;
	}
}
