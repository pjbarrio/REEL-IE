package edu.columbia.cs.ref.model.relationship;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;
import edu.columbia.cs.ref.model.pattern.resources.Matchable;

/**
 * The Class Relationship represents relationships between several entities present
 * in a document.
 * 
 * <br>
 * <br>
 * 
 * A Relationship is defined by its type which is an instance of a RelationshipType.
 * 
 * <br>
 * <br>
 * 
 * Additionally, a relationship contains a set of entities that may be related or not.
 * Each of these entities fufills a role in the relationship.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Relationship implements Serializable,Matchable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -455430781191582505L;

	/** The hash code. */
	private int hashCode = -1;
	
	/** The id. */
	private String id=null;
	
	/** The type. */
	private RelationshipType type;
	
	/** The entities. */
	private Entity[] entities;
	
	/** The label. */
	private String label=null;
	
	/**
	 * Instantiates a new relationship given a relationship type
	 *
	 * @param type the type of relationship that this Relationship represents
	 */
	public Relationship(RelationshipType type){
		this.type=type;
		entities = new Entity[type.getNumberEntities()];
	}
	
	/**
	 * Sets the relationship type.
	 *
	 * @param type the new relationship type
	 */
	public void setRelationshipType(RelationshipType type){
		this.type=type;
		entities = new Entity[type.getNumberEntities()];
	}
	
	/**
	 * Indicates that a given entity is fulfilling a given role in the relationship.
	 *
	 * @param role the role that the entity is fulfilling
	 * @param e the entity that is fulfilling the role
	 */
	public void setRole(String role, Entity e){
		entities[type.getIndex(role)]=e;
	}
	
	/**
	 * Gets the entity that fulfills a given role.
	 *
	 * @param role the desired role
	 * @return the entity that fulfills the input role
	 */
	public Entity getRole(String role){
		return entities[type.getIndex(role)];
	}

	/**
	 * Gets the id of the relationship.
	 *
	 * @return the id of the relationship
	 */
	public String getId() {
		if(id==null){
			StringBuffer middleId = new StringBuffer();
			for(int i=0; i<entities.length; i++){
				if(i==0){
					middleId.append(entities[i].getId());
				}else{
					middleId.append("," + entities[i].getId());
				}
			}
			
			id=type.getType() + "(" + middleId + ")";
		}
		return id;
	}
	
	/**
	 * Gets an array containing the entities involved in the relationship
	 *
	 * @return an array containing the entities involved in the relationship
	 */
	public Entity[] getEntities(){
		return entities;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return type.getType() + Arrays.toString(entities);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){

		if (hashCode == -1){
			
			hashCode = 1;
			
			for (String role : type.getRoles()) {
				
				hashCode = 31*hashCode + getRole(role).hashCode();
				
			}
			
		}
		
		return hashCode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){

		if (o instanceof Relationship){
			
			Relationship other = (Relationship)o;
			
			if (!type.equals(other.type))
				return false;
			
			for (int i=0; i<entities.length;i++) {
				
				EntityMatcher em = type.entityMatchers[i];

				if (!em.match(entities[i], other.entities[i]))
					return false;
				
			}
		
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel(){
		return label;
	}
	
	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label){
		this.label=label;
	}
	
	/**
	 * Gets the relationship type.
	 *
	 * @return the relationship type
	 */
	public RelationshipType getRelationshipType(){
		return type;
	}

	/**
	 * Gets the roles that are present in the relationship.
	 *
	 * @return the roles that are present in the relationship
	 */
	public Collection<String> getRoles() {
		return type.getRoles();
	}
}
