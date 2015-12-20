package edu.columbia.cs.ref.model.matcher.impl;

import java.util.Set;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;
import edu.columbia.cs.utils.Dictionary;

/**
 * The Class DictionaryEntityMatcher is an implementation of the EntityMatcher that
 * uses a dictionary of aliases to determine if two entities match.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class DictionaryEntityMatcher implements EntityMatcher {


	/** The dictionary. */
	private Dictionary dictionary;

	/**
	 * Instantiates a new dictionary entity matcher. It receives as input a dictionary
	 * of aliases that will be used if each pair of entities is an alias of each other.
	 *
	 * @param dictionary the dictionary
	 */
	public DictionaryEntityMatcher(Dictionary dictionary){
		this.dictionary = dictionary;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.relations.entity.matcher.EntityMatcher#match(edu.columbia.cs.cg.relations.Entity, edu.columbia.cs.cg.relations.Entity)
	 */
	@Override
	public boolean match(Entity original, Entity entity) {
		
		Set<String> matches = dictionary.getAliases(original.getValue());
		
		if (matches == null)
			return false;
		
		return matches.contains(entity.getValue());
			
		
	}

}
