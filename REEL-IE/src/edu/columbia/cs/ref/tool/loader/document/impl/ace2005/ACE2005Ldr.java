package edu.columbia.cs.ref.tool.loader.document.impl.ace2005;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Segment;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.loader.document.DocumentLoader;
import edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources.ACEDocument;
import edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources.ACEEMention;
import edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources.ACEEntity;
import edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources.ACERMention;
import edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources.ACERelation;
import edu.columbia.cs.utils.SGMFileFilter;


public class ACE2005Ldr extends DocumentLoader {
	
	private SGMFileFilter filter = new SGMFileFilter();
	
	/**
	 * Constructor of the loader
	 * 
	 * @param relationshipTypes Represents the types of relationships to be extracted from the collection
	 * including the constraints that they must fulfill
	 */
	
	public ACE2005Ldr(Set<RelationshipType> relationshipTypes){
		super(relationshipTypes);
	}
	
	/**
	 * Method that loads a set of documents given a File that represents the directory of the collection
	 * 
	 * @param file Represents the directory of the collection
	 * @return a set of Documents representing the documents of a collection
	 */
	@Override
	public Set<Document> load(File file) {
		
		//Initialize the return set
		
		Set<Document> result = new HashSet<Document>();
		
		//Filters out files that are not SGM
		
		if(filter.accept(file.getAbsoluteFile(), file.getName())){
			
			//Initializes an ACEDocument object
			
			ACEDocument aceDoc = new ACEDocument();
			String absPath=file.getAbsolutePath();
			absPath=absPath.substring(0,absPath.length()-4);
			
			//Loads the corresponding XML and SGM
			
			aceDoc.load(absPath);
			
			//Obtains the segments to create the document
			
			List<Segment> plainText=aceDoc.getSegments();
			String fileName = file.getName();
			fileName=fileName.substring(0,fileName.length()-4);
			Document newDocument = new Document(file.getParent(),fileName,plainText);
			
			//Adds the entities into the document
			
			for(ACEEntity ent : aceDoc.getEntities()){

				//Proceeds to obtain all the information about the entity starting from its type
				
				String entityType = ent.getType();
				
				//Obtains all the mentions of an entity (will be entities)
				
				for(ACEEMention men : ent.getMentions()){
					
					//Obtains id. In case it is not available, the user must create one. On ACE they always are.
					
					String mentionId=men.getId();
					
					//Obtains start, lenght and text value of the entity mention
					
					int headStartIndex=men.getHeadOffset();
					int headLength=men.getHeadLength();
					String value=newDocument.getSubstring(headStartIndex, headLength);
					
					//Creates and adds the entity into the new document
					
					Entity newEntity = new Entity(mentionId, entityType, headStartIndex, headLength,
							value, newDocument);
					newDocument.addEntity(newEntity);
				}
			}
			
			//Once we have the entities, we need to load the relations
			
			for(ACERelation rel : aceDoc.getRelationships()){
				
				//We obtain its type
				
				String type = rel.getType();
				RelationshipType relType = getCompatibleType(relationshipTypes, type);
				
				//If we are looking for that type of relationship
				
				if(relType!=null){
					
					//Obtain all the mentions of the entity
					
					for(ACERMention men : rel.getMentions()){
						
						//Obtain participating Entities (always binary in this case)
						
						Entity arg1 = newDocument.getEntity(men.getArg1().getId());
						Entity arg2 = newDocument.getEntity(men.getArg2().getId());
						
						//If we have both arguments, we initialize the relationship
						
						if(arg1!=null && arg2!=null){
							
							//We create it, assign its roles and add it to the new document.
							
							Relationship newRelation = new Relationship(relType);
							newRelation.setRole("Arg-1", arg1);
							newRelation.setRole("Arg-2", arg2);
							newDocument.addRelationship(newRelation);
						}
					} 
				}
			}
			
			//Add the created and loaded document to the result set
			
			result.add(newDocument);
		}
		
		return result;
	}
	
	/**
	 * Looks for compatible RelationshipType objects based on the type.
	 * @param relationshipTypes the types we want to load.
	 * @param relType the type being matched
	 * @return the RelationshipType if matched, null otherwise.
	 */
	
	private RelationshipType getCompatibleType(Set<RelationshipType> relationshipTypes, String relType){
		
		for(RelationshipType type : relationshipTypes){
			if(type.isType(relType)){
				return type;
			}
		}
		return null;
	}

}