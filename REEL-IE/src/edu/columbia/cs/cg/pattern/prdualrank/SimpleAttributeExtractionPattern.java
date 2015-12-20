package edu.columbia.cs.cg.pattern.prdualrank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.pattern.Pattern;

/**
 * The Class SimpleAttributeExtractionPattern represents a pattern that can be used
 * for Role Extraction.
 * 
 * <br>
 * <br>
 * 
 * A SimpleAttributeExtractionPattern is composed by a set of entity types that
 * can match the pattern, a list of words before the entity, a list of words after
 * the entity and finally the role to be assigned to the extracted entity
 *
 * @param <E> the element type
 * @param <D> the generic type
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class SimpleAttributeExtractionPattern<E extends Entity,D extends TokenizedDocument> extends Pattern<Entity,TokenizedDocument> {

	/** The entity types. */
	private Set<String> entityTypes;
	
	/** The words before. */
	private String[] wordsBefore;
	
	/** The words after. */
	private String[] wordsAfter;
	
	/** The role. */
	private String role;

	/**
	 * Instantiates a new simple attribute extraction pattern.
	 *
	 * @param role the role to be assigned to the extracted entity
	 * @param entityTypes a set of entity types that can match the pattern
	 * @param before the array of words before the entity
	 * @param after the array of words after the entity
	 */
	public SimpleAttributeExtractionPattern(String role, Set<String> entityTypes, String[] before,
			String[] after) {
		
		this.role = role;
		this.entityTypes = entityTypes;
		this.wordsBefore = before;
		this.wordsAfter = after;
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#findMatch(edu.columbia.cs.cg.document.Document)
	 */
	@Override
	public List<Entity> findMatch(TokenizedDocument d) {
		
		Collection<Entity> entities = d.getEntities();
	
		List<Entity> filtered = new ArrayList<Entity>();
		
		for (Entity entity : entities) {
			
			if (entityTypes.contains(entity.getEntityType()))
				filtered.add(entity);
			
		}
		
		List<Entity> matches = new ArrayList<Entity>();
		
		for (Entity entity : filtered) {
			
			if (matchesStructure(entity,d))
				matches.add(entity);
			
		}
		
		return matches;
	}

	private boolean matchesStructure(Entity entity, TokenizedDocument d) {
		
		Span entitySpan = d.getEntitySpan(entity);
		
		String[] tokenStrings = d.getTokenizedString();
		
		if (wordsBefore.length > entitySpan.getStart())
			return false;
		if (wordsAfter.length > tokenStrings.length - entitySpan.getEnd() - 1)
			return false;
		
		for (int i = wordsBefore.length-1; i >=0 ; i--) {
			
			if (!wordsBefore[i].equals(tokenStrings[entitySpan.getStart() - (wordsBefore.length - i)])){
				return false;
			}
			
		}
		
		for (int i = 0; i < wordsAfter.length; i++) {
			
			if (!wordsAfter[i].equals(tokenStrings[entitySpan.getEnd() + i + 1])){
				return false;
			}	
		
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#generateHashCode()
	 */
	@Override
	protected int generateHashCode() {
		
		int hashCode = 1;
		
		hashCode = 31*hashCode + role.hashCode();
		
		hashCode = 31*hashCode + entityTypes.hashCode();
	
		hashCode = 31*hashCode + Arrays.hashCode(wordsBefore);
		
		hashCode = 31*hashCode + Arrays.hashCode(wordsAfter);
		
		return hashCode;
	}


	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#generateToString()
	 */
	@Override
	protected String generateToString() {
		return Arrays.toString(wordsBefore) + entityTypes.toString() + " as " + role + Arrays.toString(wordsAfter);
		
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.pattern.Pattern#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		SimpleAttributeExtractionPattern<Entity, TokenizedDocument> other = (SimpleAttributeExtractionPattern<Entity, TokenizedDocument>)obj;
		
		if (!role.equals(other.role)){
			return false;
		}
		
		if (!entityTypes.containsAll(other.entityTypes)){
			return false;
		}
		
		if (wordsAfter.length != other.wordsAfter.length){
			return false;
		}
		
		if (wordsBefore.length != other.wordsBefore.length){
			return false;
		}

		if (!Arrays.equals(wordsAfter, other.wordsAfter)){
			return false;
		}
		
		if (!Arrays.equals(wordsBefore, other.wordsBefore)){
			return false;
		}
		
		return true;
	}
}
