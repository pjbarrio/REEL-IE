package edu.columbia.cs.ref.model.constraint.relationship.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;


/**
 * The Class WordDistanceBetweenEntities is an implementation of RelationshipConstraint
 * that indicates that the order of the entities is not relevant since the relationship
 * is symmetric.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class EntitiesOrderNotRelevantConstraint implements RelationshipConstraint {
	
	/** The previous elements. */
	private Set<Set<String>> previousElements = new HashSet<Set<String>>();
	
	private Set<String> getIds(Entity[] ents){
		Set<String> strings = new HashSet<String>();
		for(Entity ent : ents){
			strings.add(ent.getId());
		}
		return strings;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.relations.RelationshipConstraint#checkConstraint(edu.columbia.cs.cg.relations.Relationship)
	 */
	@Override
	public boolean checkConstraint(Relationship rel) {
		Set<String> strings = getIds(rel.getEntities());
		if(!previousElements.contains(strings)){
			previousElements.add(strings);
			return true;
		}
		
		return false;
	}

}
