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

/**
 * Loader for the ACE 2005 collection
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class ACE2005Loader extends DocumentLoader {
	private SGMFileFilter filter = new SGMFileFilter();
	
	/**
	 * Constructor of the loader
	 * 
	 * @param relationshipTypes Represents the types of relationships to be extracted from the collection
	 * including the constraints that they must fulfill
	 */
	public ACE2005Loader(Set<RelationshipType> relationshipTypes){
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
		Set<Document> result = new HashSet<Document>();
		if(filter.accept(file.getAbsoluteFile(), file.getName())){
			ACEDocument aceDoc = new ACEDocument();
			String absPath=file.getAbsolutePath();
			absPath=absPath.substring(0,absPath.length()-4);
			aceDoc.load(absPath);
			List<Segment> plainText=aceDoc.getSegments();
			
			String fileName = file.getName();
			fileName=fileName.substring(0,fileName.length()-4);
			
			Document newDocument = new Document(file.getParent(),fileName,plainText);
			
			for(ACEEntity ent : aceDoc.getEntities()){
				//String entityId = ent.getId();
				String entityType = ent.getType();
				//String entitySubType = ent.getSubtype();
				for(ACEEMention men : ent.getMentions()){
					String mentionId=men.getId();
					//String mentionType=men.getType();
					//int extStartIndex= men.getExtOffset();
					//int extLength=men.getExtLength();
					int headStartIndex=men.getHeadOffset();
					int headLength=men.getHeadLength();
					String value=newDocument.getSubstring(headStartIndex, headLength);
					Entity newEntity = new Entity(mentionId, entityType, headStartIndex, headLength,
							value, newDocument);
					newDocument.addEntity(newEntity);
				}
			}
			
			for(ACERelation rel : aceDoc.getRelationships()){
				//String relationshipId = rel.getId();
				String type = rel.getType();
				//String subType = rel.getSubType();
				
				RelationshipType relType = getCompatibleType(relationshipTypes, type);
				if(relType!=null){
					for(ACERMention men : rel.getMentions()){
						Entity arg1 = newDocument.getEntity(men.getArg1().getId());
						Entity arg2 = newDocument.getEntity(men.getArg2().getId());
						if(arg1!=null && arg2!=null){
							Relationship newRelation = new Relationship(relType);
							newRelation.setRole("Arg-1", arg1);
							newRelation.setRole("Arg-2", arg2);
							newDocument.addRelationship(newRelation);
						}
					} 
				}
			}
			result.add(newDocument);
		}
		
		return result;
	}
	
	private RelationshipType getCompatibleType(Set<RelationshipType> relationshipTypes, String relType){
		for(RelationshipType type : relationshipTypes){
			if(type.isType(relType)){
				return type;
			}
		}
		return null;
	}

}
