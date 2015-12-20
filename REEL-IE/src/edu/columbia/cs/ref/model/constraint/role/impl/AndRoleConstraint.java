package edu.columbia.cs.ref.model.constraint.role.impl;

import java.util.Set;

import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Class AndRoleConstraint is an implementation of RoleConstraint
 * that is used to compose several other constraints. This constraint checks
 * if all the constraints passed as input to the constructor check
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class AndRoleConstraint implements RoleConstraint {
	
	/** The constraints. */
	RoleConstraint[] constraints;
	
	/**
	 * Instantiates a new and role constraint. It receives an undefined number
	 * of contraints that must be checked.
	 *
	 * @param constraints the constraints
	 */
	public AndRoleConstraint(RoleConstraint ... constraints){
		this.constraints=constraints;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#checkConstraint(edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean checkConstraint(Entity role) {
		for(RoleConstraint c : constraints){
			if(!c.checkConstraint(role)){
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#getCompatibleEntities(java.util.Set)
	 */
	@Override
	public Set<Entity> getCompatibleEntities(Set<Entity> entities) {
		Set<Entity> currentSet = entities;
		for(RoleConstraint c : constraints){
			currentSet = c.getCompatibleEntities(currentSet);
			if(currentSet.isEmpty()){
				return currentSet;
			}
		}
		return currentSet;
	}
}
