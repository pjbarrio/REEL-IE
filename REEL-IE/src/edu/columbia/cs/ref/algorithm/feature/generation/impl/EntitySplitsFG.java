package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.feature.FeatureSet;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;

/**
 * The Class EntitySplitsFG divides each candidate sentence according to
 * the positions of the entities.
 * 
 * <br>
 * <br>
 * 
 * The sentence is divided into 5 spans: (i) before the first entity; (ii) the first entity;
 * (iii) between the two entities; (iv) the second entity; (v) after the second entity. Some
 * of these spans may be empty.
 * 
 * <br>
 * <br>
 * 
 * This class receives as input another feature generator that must produce the original
 * tokenization.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class EntitySplitsFG extends
		CandidateSentenceFeatureGenerator<SequenceFS<Span>> {

	/** The tokenizer. */
	private FeatureGenerator<SequenceFS<Span>> tokenizer;

	/**
	 * Instantiates a new entity splits fg.
	 *
	 * @param tokenizer the tokenizer
	 */
	public EntitySplitsFG(FeatureGenerator<SequenceFS<Span>> tokenizer){
		this.tokenizer = tokenizer;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected SequenceFS<Span> extractFeatures(CandidateSentence candidateSentence) {
		
		List<Span> spansList = new ArrayList<Span>();
		
		SequenceFS<Span> spans = candidateSentence.getSentence().getFeatures(tokenizer);
		
		int sentenceOffset = candidateSentence.getSentence().getOffset();
		
		Entity[] entities = candidateSentence.getEntities();
		
		Arrays.sort(entities, new Comparator<Entity>() {

			@Override
			public int compare(Entity entity1, Entity entity2) {
				
				return new Integer(entity1.getOffset()).compareTo(entity2.getOffset());
				
			}
		});
		
		//Has to generate:
		//first
		//index lenght

		int indexStart1 = findIndex(entities[0].getOffset()-sentenceOffset,spans);
		int indexEnd1 = findIndex(entities[0].getOffset() + entities[0].getLength() - sentenceOffset,spans);
		int indexStart2 = findIndex(entities[1].getOffset() - sentenceOffset,spans);
		int indexEnd2 = findIndex(entities[1].getOffset() - sentenceOffset,spans);
		
		spansList.add(new Span(0, (int)Math.max(indexStart1-1,0)));

		spansList.add(new Span(indexStart1, indexEnd1));
		
		//middle 
		//index lenght
		
		if(indexEnd1<indexStart2){
			spansList.add(new Span(indexEnd1+1, indexStart2-1));
		}else{
			spansList.add(new Span(indexStart2, indexStart2-1));
		}
		
		
		
		//last
		//index lenght
		
		spansList.add(new Span(indexStart2,indexEnd2));
		
		spansList.add(new Span(indexEnd2+1,spans.size()-1));

		return new SequenceFS<Span>(spansList.toArray(new Span[0]));

	}

	private int findIndex(int index, SequenceFS<Span> spans) {
		
		for (int i = 0; i < spans.size(); i++) {
			if (index>=spans.getElement(i).getStart() && index <=spans.getElement(i).getEnd())
				return i;
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> retrieveRequiredFeatureGenerators() {
		
		ArrayList<FeatureGenerator> ret = new ArrayList<FeatureGenerator>();
		
		ret.add(tokenizer);
	
		return ret;
	}

}
