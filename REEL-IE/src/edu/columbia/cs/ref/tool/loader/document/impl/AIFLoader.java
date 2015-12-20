package edu.columbia.cs.ref.tool.loader.document.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mitre.jawb.atlas.AWBDocument;
import org.mitre.jawb.atlas.PhraseTaggingAnnotation;
import org.mitre.jawb.tasks.Task;
import org.mitre.jawb.tasks.generic.GenericTask;

import com.google.common.collect.Maps;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.loader.document.DocumentLoader;
import edu.columbia.cs.ref.tool.segmentator.impl.SimpleSegmentDocumentSegmentator;
import edu.columbia.cs.ref.tool.tagger.entity.EntityTagger;
import edu.columbia.cs.ref.tool.tagger.entity.coref.CorefEntityTagger;
import edu.columbia.cs.ref.tool.tagger.span.impl.EntitySpan;
import gov.nist.atlas.io.xml.AIFXMLImport;

public class AIFLoader extends DocumentLoader {

	private Map<RelationshipType,Task> tasks;
	private Map<String,String> taskTable = new HashMap<String, String>();
	private SimpleSegmentDocumentSegmentator segmentator;
	private EntityTagger<EntitySpan, Entity>[] entityTaggers;
	private Map<String, String> typeTable = new HashMap<String, String>();
	private Map<String, Relationship> relationshipsTable = new HashMap<String, Relationship>();
	private CorefEntityTagger[] corefEntityTaggers;
	
	public AIFLoader(Set<RelationshipType> relationshipTypes, Map<String,String> typeTable, Map<String,String> taskTable, CorefEntityTagger[] corefEntityTaggers, EntityTagger<EntitySpan, Entity>... entityTaggers) {
		super(relationshipTypes);
		
		this.typeTable = typeTable;
		this.taskTable = taskTable;
		
		tasks = new HashMap<RelationshipType,Task>(relationshipTypes.size());
		for (RelationshipType rType : relationshipTypes) {
			
			tasks.put(rType,GenericTask.getInstance(new File(taskTable.get(rType.getType()))));
			
		}
		
		segmentator = new SimpleSegmentDocumentSegmentator();
		
		this.entityTaggers = entityTaggers;
		
		this.corefEntityTaggers = corefEntityTaggers;
	}

	@Override
	public Set<Document> load(File file) {
		
		System.out.println("Loading..." + file);
		
		Set<Document> documents = new HashSet<Document>(1);
		
		String rId,role;
		
		Map<RelationshipType,AWBDocument> docs = new HashMap<RelationshipType,AWBDocument>(tasks.size());
		
		String text = null;
		
		for (Entry<RelationshipType, Task> t: tasks.entrySet()) {
			
			try {
				
				AWBDocument docum = AWBDocument.fromAIF(file.toURI(), t.getValue());
				
				docs.put(t.getKey(),docum);
				
				if (text == null)
					text = docum.getSignal().getCharsAt(0);
				
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Document d = new Document(file.getAbsolutePath(), file.getName(), segmentator.segmentate(text));
		
		getRelationships().clear();
		
		//Annotate Entities
		
		for (int j = 0; j < entityTaggers.length; j++) {
			entityTaggers[j].enrich(d);
		}
		
		for (int i = 0; i < corefEntityTaggers.length; i++) {
			corefEntityTaggers[i].enrich(d);
		}
		
		//Annotate Relations
		
		Collection<Entity> entities = d.getEntities();
		
		List<Entity> auxiliarEntities = new ArrayList<Entity>(entities);
		
		for (Entry<RelationshipType,AWBDocument> doc : docs.entrySet()) {
			
			//has to make sure entities are annotated and then annotates relation
			
			Iterator<PhraseTaggingAnnotation> it = doc.getValue().getAllAnnotations();
			
			while (it.hasNext()){
				
				PhraseTaggingAnnotation ann = it.next();
			
				role = ann.getAnnotationType().getName();
				
				if (!typeTable.containsKey(role))
					continue;
				
				rId = ann.getAttributeValue("RelationId").toString();
				
				Entity e = getEntity(ann, d, rId);
				
				Relationship r = getRelationship(rId,doc.getKey(),d);
				
				r.setRole(role, e);
				
//TODO				Find the subsumed entities...
				
			}
			
			
		}
		
		//Add finished relations
		
		for (Entry<String,Relationship> relationship : getRelationships().entrySet()) {
			
			d.addRelationship(relationship.getValue());
			
		}
		
		documents.add(d);
		
		return documents;
		
	}

	private Relationship getRelationship(String rId,RelationshipType type, Document d) {
		
		Relationship ret = getRelationships().get(rId);
		
		if (ret == null){
			ret = new Relationship(type);
			getRelationships().put(rId, ret);
		}
		
		return ret;
	}

	private Map<String, Relationship> getRelationships() {
		
		if (relationshipsTable == null){
			relationshipsTable = new HashMap<String, Relationship>();
		}
		
		return relationshipsTable;
	}

	private Entity getEntity(PhraseTaggingAnnotation ann, Document doc, String relationId) {
		
		//Entities might appear in more that one relationship
		
		Entity e = new Entity(ann.getAnnotationType().getName() + "-" + relationId, typeTable.get(ann.getAnnotationType().getName()), ann.getTextExtentStart(), ann.getTextExtent().length(), ann.getTextExtent(), doc);
		
		if (doc.getEntities().contains(e)){
			
			for (Entity ent : doc.getEntities()) {
				
				if (ent.equals(e))
					return ent;
			}
			
		}else{
			
			doc.addEntity(e);
			
			return e;
			
		}
		
		return null;
	}

}
