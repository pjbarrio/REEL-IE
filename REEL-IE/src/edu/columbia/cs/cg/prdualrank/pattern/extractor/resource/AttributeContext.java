package edu.columbia.cs.cg.prdualrank.pattern.extractor.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.columbia.cs.cg.pattern.prdualrank.ExtractionPattern;
import edu.columbia.cs.cg.pattern.prdualrank.SimpleAttributeExtractionPattern;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.utils.NAryCartesianProduct;

/**
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * For further information, <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>.
 * 
 * <br><br>
 * 
 * <b>Description</b><br><br>
 * 
 * Represent the words around every attribute of a tuple. It is used to generate <b>Extraction Patterns</p>.
 * <br>
 * For instance, for a sentence: "After Google Inc. acquired YouTube for $1", depending on the configuration of the algorithm, some extraction patterns are:
 * <br>1. "After <b>BUYER</b> acquired <b>COMPANY</b> for"
 * <br>2. "<b>BUYER</b> acquired <b>COMPANY</b>"
 * <br>3. "<b>BUYER</b> acquired <b>COMPANY</b> for"
 *
 * Also, words from other sentences might be included in the context.
 * 
 * <br><br> For more information, read <b>Definition 2</b> in Section 3.1 of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class AttributeContext {

	private HashMap<String, Entity> entities;
	private List<String> roles;
	private HashMap<String, String[]> previousWords;
	private HashMap<String, String[]> postWords;
	private RelationshipType rType;
	private int hashCode = -1;
	private HashMap<String, Set<String>> entityTypes;

	/**
	 * Instantiates a new attribute context.
	 *
	 * @param relationshipType the relationship type that will be contained in this attribute context.
	 */
	public AttributeContext(RelationshipType relationshipType){

		this.rType = relationshipType;
		entities = new HashMap<String,Entity>();
		roles = new ArrayList<String>();
		previousWords = new HashMap<String,String[]>();
		postWords = new HashMap<String,String[]>();
		entityTypes = new HashMap<String, Set<String>>();
		
	}
	
	/**
	 * Adds the context for an specific role and entity.
	 *
	 * @param entity the entity being processed.
	 * @param role the role in the relationship of the entity being processed.
	 * @param before the words preceding the entity within the text where it is contained.
	 * @param after the words succeeding the entity within the text where it is contained.
	 */
	public void addContext(Entity entity, String role, String[] before,
			String[] after) {
		
		updateTypes(role,entity.getEntityType());
		roles.add(role);
		entities.put(role,entity);
		previousWords.put(role,before);
		postWords.put(role,after);
		
	}

	private void updateTypes(String role, String entityType) {
		
		Set<String> types = entityTypes.get(role);
		
		if (types == null){
			types = new HashSet<String>();
		}
		
		types.add(entityType);
		
		entityTypes.put(role,types);

		
	}

	/**
	 * Generate extraction patterns based on the context of all the attributes. Used all the possible combination of extraction patterns.
	 *
	 * @param size the maximum size of the extraction pattern used for one attribute. Extraction patterns are composed by as many attribute extraction patterns as instantiated attributes the tuple has.
	 * @return the list of generated extraction patterns.
	 */
	public List<Pattern<Relationship,TokenizedDocument>> generateExtractionPatterns(int size) {
		
		Map<String,Set<SimpleAttributeExtractionPattern<Entity, TokenizedDocument>>> patterns = new HashMap<String, Set<SimpleAttributeExtractionPattern<Entity, TokenizedDocument>>>();
		
		for (String role : roles) {
			
			patterns.put(role,generateCombinations(role,entityTypes.get(role),size,previousWords.get(role),postWords.get(role)));
			
		}
			
		List<Map<String, SimpleAttributeExtractionPattern<Entity, TokenizedDocument>>> combinations = NAryCartesianProduct.generateAllPossibilities(patterns);
		
		List<Pattern<Relationship,TokenizedDocument>> extractionPatterns = new ArrayList<Pattern<Relationship,TokenizedDocument>>();
		
		for (Map<String, SimpleAttributeExtractionPattern<Entity, TokenizedDocument>> map : combinations) {
			
			extractionPatterns.add(new ExtractionPattern<Relationship,TokenizedDocument>(roles,map,rType));
			
		}
		
		return extractionPatterns;
		
	}

	private Set<SimpleAttributeExtractionPattern<Entity, TokenizedDocument>> generateCombinations(String role, Set<String> entityTypes, int size,
			String[] wordsBefore, String[] wordsAfter) {
		
		Set<SimpleAttributeExtractionPattern<Entity, TokenizedDocument>> patterns = new HashSet<SimpleAttributeExtractionPattern<Entity,TokenizedDocument>>();

		for (int beforeSize = 0; beforeSize <= wordsBefore.length; beforeSize++) {
			for (int afterSize = 0; afterSize <= wordsAfter.length; afterSize++) {
				
				if (beforeSize == 0 && afterSize == 0)
					continue;
				
				if (beforeSize + afterSize > size)
					continue;
				
				String[] before = new String[0];
				
				if (beforeSize > 0){
					before = Arrays.copyOfRange(wordsBefore,wordsBefore.length-beforeSize,wordsBefore.length);
				}
				
				String[] after = new String[0];
				
				if (afterSize > 0){
					after = Arrays.copyOfRange(wordsAfter, 0, afterSize);
				}
				
				patterns.add(new SimpleAttributeExtractionPattern<Entity,TokenizedDocument>(role,entityTypes,before,after));
				
			}
		}

		return patterns;
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return previousWords.toString() + " - " + entities.toString() + " - "+ postWords.toString();
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		if (hashCode  == -1){
			
			hashCode = generateHashCode();
			
		}
		return hashCode;
		
	}

	private int generateHashCode() {
		
		int hash = 1;
		
		hash = 31*hash + entities.hashCode();
		
		hash = 31*hash + roles.hashCode();
		
		hash = 31*hash + previousWords.hashCode();
		
		hash = 31*hash + postWords.hashCode();
		
		return hash;
	}
}
