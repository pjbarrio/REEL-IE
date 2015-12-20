package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.SentenceFeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Sentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.feature.FeatureSet;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;

/**
 * The Class EntityBasedChunkingFG is a candidate sentence feature generator that chunks
 * tokens according to the boundaries of the entities in the candidate sentence.
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
public class EntityBasedChunkingFG extends CandidateSentenceFeatureGenerator<SequenceFS<Span>> {
	
	/** The tokenizer. */
	private FeatureGenerator<SequenceFS<Span>> tokenizer;

	/**
	 * Instantiates a new entity based chunking fg.
	 *
	 * @param tokenizer the feature generator that produces the original tokenization
	 */
	public EntityBasedChunkingFG(FeatureGenerator<SequenceFS<Span>> tokenizer){
		this.tokenizer = tokenizer;
	}
	
	private SequenceFS<Span> normalizeTokenization(CandidateSentence sent, SequenceFS<Span> sentenceTokens){
		for(Entity ent : sent.getEntities()){
			sentenceTokens=normalizeTokenization(sent.getSentence(),ent,sentenceTokens);
		}
		return sentenceTokens;
	}
	
	private SequenceFS<Span> normalizeTokenization(Sentence sentence, Entity ent, SequenceFS<Span> sentenceTokens) {
		List<Span> newSentenceTokens = new ArrayList<Span>();
		int numTokens = sentenceTokens.size();
		int currentStart=-1;
		int currentEnd=Integer.MAX_VALUE;
		int startEntity = ent.getOffset()-sentence.getOffset();
		int endEntity = startEntity+ent.getLength();
		
		for(int i=0;i<numTokens;i++){
			Span w = sentenceTokens.getElement(i);
			int startTok = w.getStart();
			int endTok = w.getEnd();
			if(currentStart<startTok && startEntity>=startTok){
				currentStart=startTok;
				ent.setOffset(startTok+sentence.getOffset());
			}
			if(currentEnd>endTok && endEntity<=endTok){
				currentEnd=endTok;
				ent.setLength(currentEnd-currentStart);
				break;
			}
		}
		
		if(currentEnd==Integer.MAX_VALUE){
			currentEnd=sentenceTokens.getElement(sentenceTokens.size()-1).getEnd();
			ent.setLength(currentEnd-currentStart);
		}
		
		if(currentStart==ent.getOffset()-sentence.getOffset() && 
				currentEnd==startEntity+ent.getLength()){
			Span newWord=new Span(currentStart,currentEnd);
			newSentenceTokens.add(newWord);
		}
		
		int sizeNewSentTokens = newSentenceTokens.size();
		outCycle: for(int i=0; i<sizeNewSentTokens; i++){
			Span w1 = newSentenceTokens.get(i);
			int startTokW1 = w1.getStart();
			int endTokW1 = w1.getEnd();
			for(int j=i+1; j<sizeNewSentTokens; j++){
				Span w2 = newSentenceTokens.get(j);
				int startTokW2 = w2.getStart();
				int endTokW2 = w2.getEnd();
				if(!(endTokW1<startTokW2 || endTokW2<startTokW1)){
					if(endTokW1-startTokW1<endTokW2-startTokW2){
						newSentenceTokens.remove(i);
					}else{
						newSentenceTokens.remove(j);
					}
					i=-1;
					sizeNewSentTokens = newSentenceTokens.size();
					continue outCycle;
				}

			}
		}

		List<Span> result = new ArrayList<Span>();
		int sizeSentTokens = sentenceTokens.size();
		sizeNewSentTokens = newSentenceTokens.size();
		int currentNewToken=0;
		Span newToken=null;
		if(currentNewToken<sizeNewSentTokens){
			newToken=newSentenceTokens.get(currentNewToken);
		}
		int i=0;
		for(; i<sizeSentTokens && newToken!=null; i++){
			Span w = sentenceTokens.getElement(i);
			if(w.getEnd()<=newToken.getStart()){
				result.add(w);
			}else if(w.getEnd()==newToken.getEnd()){
				result.add(newToken);
				currentNewToken++;
				if(currentNewToken<sizeNewSentTokens){
					newToken=newSentenceTokens.get(currentNewToken);
				}else{
					newToken=null;
				}
			}
		}
		for(;i<sizeSentTokens; i++){
			Span w = sentenceTokens.getElement(i);
			result.add(w);
		}
		
		Span[] arrayResult = new Span[result.size()];
		

		return new SequenceFS<Span>(result.toArray(arrayResult));
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected SequenceFS<Span> extractFeatures(CandidateSentence sentence) {
		SequenceFS<Span> tokenization = sentence.getSentence().getFeatures(tokenizer);
		
		SequenceFS<Span> spans=normalizeTokenization(sentence, tokenization);
				
		return spans;
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
