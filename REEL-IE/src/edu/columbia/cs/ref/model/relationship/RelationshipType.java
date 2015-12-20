package edu.columbia.cs.ref.model.relationship;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.constraint.relationship.impl.DummyRelationshipConstraint;
import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.constraint.role.impl.NoConstraint;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;
import edu.columbia.cs.ref.model.matcher.impl.EqualsEntityMatcher;

/**
 * The Class RelationshipType represents a type of relationship that we are interested
 * in.
 * 
 * <br>
 * <br>
 * 
 * The RelationshipType contains more than the label indicating the semantics of the
 * relationship. Namely, a relationship type may also contain some constraints imposed
 * over roles, the relationship itself and the entities that can be matched. These
 * constraints are used to check if, given a RelationshipType, a given relationship is
 * valid.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class RelationshipType implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4327250318382126824L;

	/** The Constant NOT_A_RELATIONSHIP. */
	public static final String NOT_A_RELATIONSHIP = "";
	
	/** The type. */
	private String type;
	
	/** The indexes. */
	private Hashtable<String,Integer> indexes;
	
	/** The number entities. */
	private int numberEntities;
	
	/** The role constraints. */
	private RoleConstraint[] roleConstraints;
	
	/** The entity matchers. */
	EntityMatcher[] entityMatchers;
	
	/** The rel constraints. */
	private RelationshipConstraint relConstraints;
	
	/**
	 * Instantiates a new relationship type. The relationship type is created by
	 * providing a String which is an indication of the semantics of the relationship
	 * and zero or more roles involved in this relationship type
	 *
	 * @param type the type
	 * @param roles the roles
	 */
	public RelationshipType(String type, String ... roles){
		indexes=new Hashtable<String,Integer>();
		int rolesSize = roles.length;
		for(int i=0;i<rolesSize; i++){
			indexes.put(roles[i],i);
		}
		setType(type);
		numberEntities=roles.length;
		roleConstraints=new RoleConstraint[rolesSize];
		entityMatchers=new EntityMatcher[rolesSize];
		for(int i=0;i<rolesSize; i++){
			roleConstraints[i]=new NoConstraint();
			entityMatchers[i]=new EqualsEntityMatcher();
		}
		relConstraints=new DummyRelationshipConstraint();
	}
	
	/**
	 * Gets the index of the role in a vector representation of a relationship.
	 *
	 * @param role the role for which we want to check the index
	 * @return the index of the role in a vector representation of a relationship
	 */
	public int getIndex(String role){
		return indexes.get(role);
	}

	/**
	 * Sets the label of the relationships.
	 *
	 * @param type the new label of the relationships
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the label of the relationships.
	 *
	 * @return the label of the relationships
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Gets the number of entities involved in this relationship type.
	 *
	 * @return the number of entities involved in this relationship type
	 */
	public int getNumberEntities(){
		return numberEntities;
	}
	
	/**
	 * Gets the roles used in this relationship type.
	 *
	 * @return the roles used in this relationship type
	 */
	public Set<String> getRoles(){
		return indexes.keySet();
	}

	/**
	 * Checks if the type of relationship is the same as a given string.
	 *
	 * @param relType the relationship type label
	 * @return true, if the label of the relationship type is the input of this method
	 */
	public boolean isType(String relType) {
		return relType.equals(type);
	}
	
	/**
	 * Sets the constraint imposed over some role
	 *
	 * @param constraint the constraint
	 * @param role the role
	 */
	public void setConstraints(RoleConstraint constraint, String role){
		roleConstraints[indexes.get(role)]=constraint;
	}
	
	/**
	 * Gets the constraint imposed over some role
	 *
	 * @param role the role
	 * @return the constraint
	 */
	public RoleConstraint getConstraint(String role){
		return roleConstraints[indexes.get(role)];
	}
	
	/**
	 * Sets the entity matcher for a given role
	 *
	 * @param matcher the matcher
	 * @param role the role
	 */
	public void setMatchers(EntityMatcher matcher, String role){
		entityMatchers[indexes.get(role)]=matcher;
	}
	
	/**
	 * Gets the entity matcher for a given role
	 *
	 * @param role the role
	 * @return the matchers
	 */
	public EntityMatcher getMatchers(String role){
		return entityMatchers[indexes.get(role)];
	}
	
	/**
	 * Sets the relationship constraint for this relationship type.
	 *
	 * @param constraint the new constraints
	 */
	public void setConstraints(RelationshipConstraint constraint){
		relConstraints=constraint;
	}
		
	/**
	 * Gets the relationship constraint for this relationship type.
	 *
	 * @return the relationship constraint for this relationship type
	 */
	public RelationshipConstraint getRelationshipConstraint(){
		return relConstraints;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return type.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof RelationshipType){
			return type.equals(((RelationshipType) o).type);
		}
		return false;
	}
}
