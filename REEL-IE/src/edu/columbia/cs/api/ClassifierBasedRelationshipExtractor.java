package edu.columbia.cs.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.columbia.cs.ref.algorithm.CandidatesGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.document.splitter.SentenceSplitter;

/**
 * Implementation of the relationship extractor that uses a classifier to extract
 * information from a document
 * 
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class ClassifierBasedRelationshipExtractor<D extends Document> implements RelationshipExtractor<Document>{
	private Model m;
	private CandidatesGenerator generator;
	private StructureConfiguration structGenerator;

	/**
	 * Constructor of the classifier based relationship extractor. It receives as input a
	 * classification model and a sentence splitter.
	 * 
	 * @param m classification model
	 * @param s sentence splitter
	 */
	public ClassifierBasedRelationshipExtractor(Model m, SentenceSplitter s){
		this.m=m;
		this.generator = new CandidatesGenerator(s);
		this.structGenerator = m.getStructureConfiguration();
	}

	/**
	 * 
	 * Implementation of the extract Tuples method that uses the classifier to
	 * extract tuples from the input document
	 * 
	 * @param d the document that contains the information to be extracted
	 */
	public List<Relationship> extractTuples(Document d){
		
		Set<CandidateSentence> sents = generator.generateCandidates(d, m.getRelationshipTypes());
		
		Map<String,RelationshipType> types = new HashMap<String,RelationshipType>();
		for(RelationshipType type : m.getRelationshipTypes()){
			types.put(type.getType(), type);
		}
		
		List<Relationship> results = new ArrayList<Relationship>();
		
		for(CandidateSentence s : sents){
			OperableStructure struc = structGenerator.getOperableStructure(s);
			
			Set<String> labels = m.predictLabel(struc);
			for(String label : labels){
				RelationshipType type = types.get(label);
				Relationship r = struc.getCandidateSentence().getRelationship(type);
				r.setLabel(label);
				results.add(r);
			}
		}
		
		return results;
	}
}
