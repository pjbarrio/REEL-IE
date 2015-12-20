package edu.columbia.cs.ref.model.constraint.relationship.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Class DistanceBetweenEntitiesConstraint is an implementation of RelationshipConstraint
 * that checks if the number of characters between entities is less than a given value passed
 * to the constructor.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class DistanceBetweenEntitiesConstraint implements RelationshipConstraint {

	/** The k. */
	private int k;
	
	/**
	 * Instantiates a new distance between entities constraint. The parameter is the
	 * maximum allowed distance between the entities.
	 *
	 * @param k the k
	 */
	public DistanceBetweenEntitiesConstraint(int k){
		this.k=k;
	}
	
	/**
	 * Instantiates a new distance between entities constraint. The parameter is the
	 * maximum allowed distance between the entities.
	 *
	 * @param k the k
	 */
	public DistanceBetweenEntitiesConstraint(Integer k){
		this.k = k;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.relations.RelationshipConstraint#checkConstraint(edu.columbia.cs.cg.relations.Relationship)
	 */
	@Override
	public boolean checkConstraint(Relationship rel) {
		Entity[] entities = rel.getEntities();
		
		List<Integer> starts = new ArrayList<Integer>();
		List<Integer> ends = new ArrayList<Integer>();
		
		for(Entity e : entities){
			starts.add(e.getOffset());
			ends.add(e.getOffset()+e.getLength());
		}
		
		Collections.sort(starts);
		Collections.sort(ends);
		
		int highestStart = starts.get(starts.size()-1);
		int lowestEnd = ends.get(0);
		
		if(highestStart-lowestEnd<=k){
			return true;
		}
		
		return false;
	}

}
