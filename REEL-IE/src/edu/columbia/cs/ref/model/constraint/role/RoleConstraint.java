package edu.columbia.cs.ref.model.constraint.role;

import java.io.Serializable;
import java.util.Set;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * The Interface RoleConstraint represents a constraint over a given role of a relationship.
 * 
 * <br>
 * <br>
 * 
 * Instances of classes that implement this interface can be used in two different
 * ways:
 * 
 * <br>
 * <br>
 * 
 * 1) by using the checkConstraint method, it can check if a given entity fulfills the
 * constraint
 * 
 * <br>
 * <br>
 * 
 * 2) by using the getCompatibleEntities method, it can filter entities taht do not
 * fulfill the constraint
 *
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface RoleConstraint extends Serializable{
	
	/**
	 * Check check if the input entity fulfills the constraint
	 *
	 * @param role the entity
	 * @return true, if successful
	 */
	public boolean checkConstraint(Entity role);
	
	/**
	 * Returns the subset of all the input entities that fulfill the constraint
	 *
	 * @param entities the entities to be checked
	 * @return the compatible entities
	 */
	public Set<Entity> getCompatibleEntities(Set<Entity> entities);
}
