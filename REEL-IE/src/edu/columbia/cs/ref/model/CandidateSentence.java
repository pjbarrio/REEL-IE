package edu.columbia.cs.ref.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * A representation of a candidate sentence. A candidate sentence is the basic structure
 * that contains the information that is used for most of the classifier based methods.
 * 
 * <br>
 * <br>
 * 
 * A candidate sentence is composed by a Sentence from a given document, a list of entities
 * that are present in the sentence. The order of these entities is very important since it
 * defines the role that each entity performs in the relationships.
 * 
 * <br>
 * <br>
 * 
 * A candidate sentence can also be annotated with several relationships (necessary for
 * training).
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class CandidateSentence extends FeaturableObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 90249725464599185L;
	private Sentence sentence;
	private List<Entity> entities;
	private String id=null;
	private Map<RelationshipType,Relationship> relationships;

	/**
	 * Candidate sentence constructor. It receives a Sentence from a given document where
	 * a given set of relationships may appear. Moreover, it receives a list of entities
	 * that are present in the sentence.
	 * 
	 * <br>
	 * <br>
	 * 
	 * We recommend that you use the class CandidatesGenerator instead of this constructor
	 * (should it be protected?)
	 * 
	 * @param sentence
	 * @param entities
	 */
	public CandidateSentence(Sentence sentence,List<Entity> entities){
		this.setSentence(sentence);
		this.entities=entities;
		relationships = new HashMap<RelationshipType,Relationship>();
	}

	/**
	 * 
	 * This method allows for the annotation of a candidate sentence. It receives a relationship
	 * rel between some of the entities present in this candidate sentence. This annotation
	 * indicates that there is a relationship between these entities
	 * 
	 * @param rel Relationship between some of the entities of this sentence
	 */
	public void addRelationship(Relationship rel){
		relationships.put(rel.getRelationshipType(), rel);
	}

	/**
	 * 
	 * This method sets the sentence that the candidate sentence corresponds to.
	 * 
	 * @param sentence Sentence that the candidate sentence corresponds to
	 */
	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}

	/**
	 * This methods gets the sentence that the candidate sentence corresponds to.
	 * 
	 * @return Sentence that the candidate sentence corresponds to
	 */
	public Sentence getSentence() {
		return sentence;
	}

	/**
	 * 
	 * This method returns an array with the ordered entities that corresponds to the
	 * tuple representation of the relationships present in this candidate sentence
	 * 
	 * @return an array of entities that is that corresponds to the tuple representation of the relationships present in this candidate sentence
	 */
	public Entity[] getEntities(){
		Entity[] ents = new Entity[entities.size()];
		return entities.toArray(ents);
	}

	/**
	 * This method returns all the labels that correspond to the relationships present
	 * in this candidate sentence
	 * 
	 * @return labels that correspond to the relationships present in this candidate sentence
	 */
	public Set<String> getLabels() {
		Set<String> labels = new HashSet<String>();
		for(Entry<RelationshipType,Relationship> entry : relationships.entrySet()){
			if(!entry.getValue().getLabel().equals(RelationshipType.NOT_A_RELATIONSHIP)){
				labels.add(entry.getValue().getLabel());
			}
		}
		return labels;
	}
	
	/**
	 * Given a relationship type, it returns a collection of roles that can be assigned
	 * for this candidate sentence
	 * 
	 * @param t relationship type that corresponds to a key to the relationship that we are trying to find the roles for
	 * @return a collection of strings corresponding to the labels of the roles in for this relationship type
	 */
	public Collection<String> getRoles(RelationshipType t){
		return relationships.get(t).getRoles();
	}

	/**
	 * Given a relationship type and a role, it returns the entity that is fulfilling that role in a relationship
	 * of this candidate sentence
	 * 
	 * @param type relationship type that corresponds to a key to the relationship
	 * @param role role that we want to find out
	 * @return entity fulfilling the role in this candidate sentence
	 */
	public Entity getRole(RelationshipType type,String role){
		return relationships.get(type).getRole(role);
	}
	
	/**
	 * Given a relationship type, it returns the relationship between the entities of the candidate sentence
	 * 
	 * @param type relationship type that corresponds to a key to the relationship
	 * @return annotation of how the entities of the candidate sentence are related for a given relationship type
	 */
	public Relationship getRelationship(RelationshipType t){
		return relationships.get(t);
	}

	@Override
	public int hashCode(){
		return entities.hashCode();
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof CandidateSentence){
			return entities.equals(((CandidateSentence) o).entities);
		}
		return false;
	}

	/**
	 * Returns a string that can be used as an unique identifier of the candidate sentence.
	 * 
	 * <br>
	 * <br>
	 * 
	 * This string is essentially a comma-separated string concatenation of the candidate sentence
	 * 
	 * @return unique identifier of the candidate sentence
	 */
	public String getId(){
		if(id==null){
			StringBuffer middleId = new StringBuffer();
			for(int i=0; i<entities.size(); i++){
				if(i==0){
					middleId.append(entities.get(i).getId());
				}else{
					middleId.append("," + entities.get(i).getId());
				}
			}

			id=middleId.toString();
		}
		return id;
	}
}
