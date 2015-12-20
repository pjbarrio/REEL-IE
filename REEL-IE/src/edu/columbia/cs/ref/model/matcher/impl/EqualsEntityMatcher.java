package edu.columbia.cs.ref.model.matcher.impl;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;

/**
 * The Class EqualsEntityMatcher is an implementation of the EntityMatcher that
 * uses the equals method ot the entities directly to determine if they match.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class EqualsEntityMatcher implements EntityMatcher {

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.entity.matcher.EntityMatcher#match(edu.columbia.cs.cg.relations.Entity, edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean match(Entity original, Entity entity) {
		
		return original.equals(entity);
		
	}

}
