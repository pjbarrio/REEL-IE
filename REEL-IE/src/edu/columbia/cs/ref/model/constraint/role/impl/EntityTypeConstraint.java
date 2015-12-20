package edu.columbia.cs.ref.model.constraint.role.impl;

import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.entity.Entity;

/**
 * The Class EntityTypeConstraint is an implementation of the RoleConstraint that
 * checks if the entity type is compatible.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class EntityTypeConstraint implements RoleConstraint {

	/** The type. */
	private String type;
	
	/**
	 * Instantiates a new entity type constraint. The input is the compatible type
	 * of entity.
	 *
	 * @param type the compatible type of entity
	 */
	public EntityTypeConstraint(String type){
		this.type=type;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#checkConstraint(edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean checkConstraint(Entity role) {
		return role.getEntityType().equals(type);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#getCompatibleEntities(java.util.Set)
	 */
	@Override
	public Set<Entity> getCompatibleEntities(Set<Entity> entities) {
		Set<Entity> results = new HashSet<Entity>();
		
		for(Entity ent : entities){
			if(checkConstraint(ent)){
				results.add(ent);
			}
		}
		
		return results;
	}
	
}
