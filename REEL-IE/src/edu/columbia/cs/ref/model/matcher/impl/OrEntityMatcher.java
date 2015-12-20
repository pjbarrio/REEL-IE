package edu.columbia.cs.ref.model.matcher.impl;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;

// TODO: Auto-generated Javadoc
/**
 * The Class OrEntityMatcher is an implementation of EntityMatcher
 * that is used to compose several other matchers. This constraint checks
 * if the entities passed as input match using at least one of the matchers
 * passed as input to the constructor check
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OrEntityMatcher implements EntityMatcher {

	/** The entity matchers. */
	private EntityMatcher[] entityMatchers;

	/**
	 * Instantiates a new or entity matcher. It receives an undefined number
	 * of matchers that must be used in the matching.
	 *
	 * @param entityMatchers the entity matchers
	 */
	public OrEntityMatcher(EntityMatcher ...entityMatchers){
		this.entityMatchers = entityMatchers;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.entity.matcher.EntityMatcher#match(edu.columbia.cs.cg.relations.Entity, edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean match(Entity original, Entity entity) {
		
		for (int i = 0; i < entityMatchers.length; i++) {
			
			if (entityMatchers[i].match(original, entity))
				return true;
			
		}
		
		return false;
	}

}
