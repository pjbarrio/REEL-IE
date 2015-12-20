package edu.columbia.cs.cg.pattern.prdualrank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.utils.NAryCartesianProduct;

/**
 * The Class ExtractionPattern represents a pattern that can be used for Relationship
 * Extraction.
 * 
 * <br>
 * <br>
 * 
 * An ExtractionPattern is composed by several roles that can be fulfilled by entities
 * of the relationship that we are trying to extract. Each role in the ExtractionPattern
 * is also associated with a subpattern that is an object of the class
 * SimpleAttributeExtractionPattern.
 *
 * @param <T> the type of relationship that this pattern extracts
 * @param <D> the type of document that we can extract from
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class ExtractionPattern<T extends Relationship, D extends TokenizedDocument> extends Pattern<Relationship,TokenizedDocument> {

	/** The roles. */
	private List<String> roles;
	
	/** The simple attribute map. */
	private Map<String, SimpleAttributeExtractionPattern<Entity,TokenizedDocument>> simpleAttributeMap;
	
	/** The r type. */
	private RelationshipType rType;
	
	/** The r constraint. */
	private RelationshipConstraint rConstraint;

	/**
	 * Instantiates a new extraction pattern.
	 *
	 * @param roles the list of roles that this pattern must match
	 * @param simpleAttributeMap map that associates each role to the subpattern surounding the entity that must fulfill it
	 * @param rType the type of relationship that must be extracted
	 */
	public ExtractionPattern(List<String> roles, Map<String, SimpleAttributeExtractionPattern<Entity,TokenizedDocument>> simpleAttributeMap, RelationshipType rType) {
		
		this.roles = roles;
		this.simpleAttributeMap = simpleAttributeMap;
		this.rType = rType;
		this.rConstraint = rType.getRelationshipConstraint();

	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#findMatch(edu.columbia.cs.cg.document.Document)
	 */
	@Override
	public List<Relationship> findMatch(TokenizedDocument d) {
		
		Map<String,Set<Entity>> combinationsMap = new HashMap<String, Set<Entity>>();
		
		for (String role : roles) {
			
			combinationsMap.put(role, new HashSet<Entity>(simpleAttributeMap.get(role).findMatch(d)));
			
		}
		
		List<Map<String, Entity>> combinations = NAryCartesianProduct.generateAllPossibilities(combinationsMap);
		
		List<Relationship> tuples = new ArrayList<Relationship>();
		
		for (Map<String, Entity> map : combinations) {
			
			if (!inOrder(map,roles)){
				
				continue;
				
			}
			
			Relationship r = new Relationship(rType);
			
			for (Entry<String, Entity> entry : map.entrySet()) {
				
				r.setRole(entry.getKey(), entry.getValue());
				
			}
			
			if (rConstraint.checkConstraint(r)){
				
				tuples.add(r);
				
			}
			
		}
		
		return tuples;
		
	}

	private boolean inOrder(Map<String, Entity> map, List<String> roles) {
	
		Entity e = map.get(roles.get(0));
		
		for (int i = 1; i < roles.size(); i++) {
			
			Entity e2 = map.get(roles.get(i));
			
			if (e.getOffset() <= e2.getOffset()){
				
				e = e2;
				
			} else{
				
				return false;
				
			}
			
		}
	
		return true;
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#generateHashCode()
	 */
	protected int generateHashCode() {
		
		int hashCode = 1;
		
		hashCode = 31*hashCode + simpleAttributeMap.hashCode();

		hashCode = 31*hashCode + rType.hashCode();
		
		hashCode = 31*hashCode + rConstraint.hashCode();
		
		return hashCode;
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#generateToString()
	 */
	@Override
	protected String generateToString() {
		return simpleAttributeMap.toString();
	}
}
