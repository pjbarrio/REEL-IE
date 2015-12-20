package edu.columbia.cs.ref.model.constraint.relationship.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;

/**
 * The Class WordDistanceBetweenEntities is an implementation of RelationshipConstraint
 * that checks if the number of words between entities is less than a given value passed
 * to the constructor.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class WordDistanceBetweenEntities implements RelationshipConstraint {

	/** The tokenizer. */
	private Tokenizer tokenizer;
	
	/** The Maxdistance. */
	private int Maxdistance;
	
	/** The activate cache. */
	private boolean activateCache;

	/**
	 * Instantiates a new word distance between entities.
	 * 
	 * <br>
	 * <br>
	 * 
	 * The constructor receives two parameters. The first is the tokenizer that is
	 * used to split the text into words. The second is the maximum allowed distance
	 * between the entities.
	 *
	 * @param tokenizer the tokenizer
	 * @param Maxdistance the maxdistance
	 */
	public WordDistanceBetweenEntities(Tokenizer tokenizer, int Maxdistance){
		this.tokenizer = tokenizer;
		this.Maxdistance = Maxdistance;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.constraints.relations.RelationshipConstraint#checkConstraint(edu.columbia.cs.cg.relations.Relationship)
	 */
	@Override
	public boolean checkConstraint(Relationship rel) {
		
		TokenizedDocument document = null;
		
		List<Entity> entities = new ArrayList<Entity>();
		
		for (String role : rel.getRoles()){
			
			Entity e = rel.getRole(role);
			
			if (e != Entity.NULL_ENTITY){
				
				if (document == null || document.equals(e.getDocument())){
					entities.add(e);
					
					//TODO avoid the cast
					document = (TokenizedDocument)e.getDocument();
				}else{
					return false;
				}
							
			}
			
		}
		
		Collections.sort(entities);
		
		for (int i = 0; i < entities.size()-1; i++) {
			
			Entity e1 = entities.get(i);
			
			Entity e2 = entities.get(i+1);
			
			Span se1 = document.getEntitySpan(e1);
			
			Span se2 = document.getEntitySpan(e2);
			
			if (se1.getEnd() < se2.getStart()){
				
				if (se2.getStart() - se1.getEnd() - 1 > Maxdistance){
					
					return false;
				}
			}
					
		}
		
		return true;
	}

}
