package edu.columbia.cs.ref.tool.tagger.entity.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.entity.CorefEntity;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tagger.entity.coref.CorefEntityTagger;
import edu.columbia.cs.ref.tool.tagger.entity.impl.resources.DeterministicCorefAnnotator;
import edu.columbia.cs.ref.tool.tagger.span.impl.CorefEntitySpan;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordNLPCoreference extends CorefEntityTagger {

	private StanfordCoreNLP pipeline;
	private DeterministicCorefAnnotator crefAnn;

	public StanfordNLPCoreference(Set<String> tag) {
	
		super(tag);
		
		Properties props = new Properties();
		
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
		
		pipeline = new StanfordCoreNLP(props);
		
		crefAnn = new DeterministicCorefAnnotator(props);
		
	}

	
	
	@Override
	protected List<CorefEntitySpan> findSpans(Document d) {
		
		//has to get the document together. //has to be careful with offsets
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < d.getPlainText().size(); i++) {
			
			sb.insert(d.getPlainText().get(i).getOffset(),d.getPlainText().get(i).getValue()); //TODO see how to add the offset.
			
		}
		
		Annotation document = new Annotation(sb.toString());

		System.out.println("starting annotation...");
		
		pipeline.annotate(document);
		
		System.out.println("done annotating...");
		
		Collection<Entity> et = d.getEntities();
		
		List<Entity> fet = new ArrayList<Entity>();
		
		for (Entity entity : et) {
			
			if (accepts(entity.getEntityType())){
				fet.add(entity);
			}
			
		}
		
		Map<Integer,Entity> entitiesTable = new HashMap<Integer, Entity>();
		
		System.out.println("starting coref...");
		
		crefAnn.annotate(document,fet, entitiesTable);
		
		System.out.println("done coref...");
		
		Map<Integer,CorefChain> graph = document.get(CorefChainAnnotation.class);

		//obtain the sentences of the documents
		
		List<CoreMap> sents = document.get(CoreAnnotations.SentencesAnnotation.class);
		
		Set<CorefEntitySpan> ret = new HashSet<CorefEntitySpan>();
		
		for (Entry<Integer,CorefChain> coreMap : graph.entrySet()) {
			
			//get the list of mentions
			
			List<CorefMention> list = coreMap.getValue().getCorefMentions();
			
			//only when we have more than one mention
			
			if (list.size() > 1){
				
				//we get all the entities that are tagged
				
				List<Entity> heads = getEntityHead(list,entitiesTable);
				
				for (int i = 0; i < list.size(); i++) {
					
					//for each mention
					
					CorefMention cm = list.get(i);

					//get the corresponding entity
					
					Entity em = entitiesTable.get(cm.mentionID);
					
					String etype = null;
					
					if (em != null)
						etype = em.getEntityType();
					
					for (int j = 0; j < heads.size(); j++) { //for each already tagged entity
 						
						//get the corresponding entity
						
						if (et == null || !heads.get(j).getEntityType().equals(etype)){ //if the mention is not labeled as etype or at all
							
							int first = cm.startIndex-1;
							
							int second = cm.endIndex-2;
							
							CoreMap sente = sents.get(cm.sentNum-1);
							
							CoreLabel tokenStart = sente.get(CoreAnnotations.TokensAnnotation.class).get(first);
		
							CoreLabel tokenEnd = sente.get(CoreAnnotations.TokensAnnotation.class).get(second);
							
							CorefEntitySpan ces = new CorefEntitySpan("CR-"+cm.mentionID + "-" + heads.get(j).getId(), heads.get(j).getEntityType(), 
									sb.toString().substring(tokenStart.beginPosition(), tokenEnd.endPosition()), tokenStart.beginPosition(), 
									tokenEnd.endPosition()-tokenStart.beginPosition(), heads.get(j));
							
							//it can happen that there are duplicates entities. Many ways of obtaining the coreference.
							
							ret.add(ces);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		System.out.println("returning ...");
		
		return new ArrayList<CorefEntitySpan>(ret);
		
	}



	private List<Entity> getEntityHead(List<CorefMention> list, Map<Integer, Entity> entitiesTable) {
		
		List<Entity> ret = new ArrayList<Entity>();
		
		for (int i = 0; i < list.size(); i++) {
			
			Entity m = entitiesTable.get(list.get(i).mentionID);
			
			if (m != null){

				ret.add(m);
				
			}
			
		}
		
		return ret;
		
	}

}
