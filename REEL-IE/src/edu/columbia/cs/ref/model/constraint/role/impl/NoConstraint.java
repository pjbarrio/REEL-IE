package edu.columbia.cs.ref.model.constraint.role.impl;

import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.entity.Entity;

/**
 * The Class NoConstraint is an implementation of the RoleConstraint
 * that is actually a dummy check. All the entities are accepted.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class NoConstraint implements RoleConstraint {

	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#checkConstraint(edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean checkConstraint(Entity role) {
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#getCompatibleEntities(java.util.Set)
	 */
	@Override
	public Set<Entity> getCompatibleEntities(Set<Entity> entities) {
		return entities;
	}
	
}
