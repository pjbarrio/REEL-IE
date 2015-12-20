package edu.columbia.cs.ref.model.matcher;

import java.io.Serializable;

import edu.columbia.cs.ref.model.entity.Entity;

/**
 * The Interface EntityMatcher represents is used to force the implementation of methods
 * to determine if two entities match according to a given criterion.
 * 
 * <br>
 * <br>
 * 
 * The method match is used to determine if the two entities match
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface EntityMatcher extends Serializable {

	/**
	 * Determines if the two input entities match
	 *
	 * @param original the first entity
	 * @param entity the second entity
	 * @return true, if the entities match
	 */
	public boolean match(Entity original, Entity entity);

}
