package edu.columbia.cs.ref.model.constraint.role.impl;

import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;

/**
 * The Class MatchRoleConstraint is an implementation of the RoleConstraint that
 * uses a given Entity matcher and an entity to check if the new entity can match
 * the original entity.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class MatchRoleConstraint implements RoleConstraint {

	/** The entity matcher. */
	private EntityMatcher entityMatcher;
	
	/** The original. */
	private Entity original;

	/**
	 * Instantiates a new match role constraint. It receives an entity matcher and
	 * the entity that will be compared to all the new entities
	 *
	 * @param entityMatcher the entity matcher
	 * @param original the original entity
	 */
	public MatchRoleConstraint (EntityMatcher entityMatcher, Entity original){
		this.entityMatcher = entityMatcher;
		this.original = original;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#checkConstraint(edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean checkConstraint(Entity role) {
		return entityMatcher.match(original,role);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.roles.RoleConstraint#getCompatibleEntities(java.util.Set)
	 */
	@Override
	public Set<Entity> getCompatibleEntities(Set<Entity> entities) {
		
		Set<Entity> ret = new HashSet<Entity>();
		
		for (Entity entity : entities) {
			
			if(entityMatcher.match(original,entity))
				ret.add(entity);
			
		}
		
		return ret;
	}

}
