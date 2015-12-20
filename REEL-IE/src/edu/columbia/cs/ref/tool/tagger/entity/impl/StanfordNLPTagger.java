package edu.columbia.cs.ref.tool.tagger.entity.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Segment;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tagger.entity.EntityTagger;
import edu.columbia.cs.ref.tool.tagger.span.impl.EntitySpan;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

public class StanfordNLPTagger<IN extends CoreMap> extends EntityTagger<EntitySpan, Entity>{

	AbstractSequenceClassifier<IN> classifier;
	
	public StanfordNLPTagger(Set<String> tag, AbstractSequenceClassifier<IN> classifier) throws ClassCastException, IOException, ClassNotFoundException {
		super(tag);
		this.classifier = classifier;
	}

	@Override
	protected List<EntitySpan> findSpans(Document d) {
		
		List<EntitySpan> entitySpans = new ArrayList<EntitySpan>();
		
		for (int i = 0; i < d.getPlainText().size(); i++) {
			
			List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(d.getPlainText().get(i).getValue());
			
			for (int j = 0; j < list.size(); j++) {
				
				entitySpans.add(createEntitySpan(list.get(j),d.getPlainText().get(i),d,entitySpans.size()));
				
			}
			
		}
		
		return entitySpans;
	}

	private EntitySpan createEntitySpan(
			Triple<String, Integer, Integer> triple, Segment segment, Document d, int newId) {
		return new EntitySpan(newId + "-" + triple.first, triple.first, segment.getValue(triple.second, triple.third-triple.second), triple.second, triple.third-triple.second);
	}



}
