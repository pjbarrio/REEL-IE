package edu.columbia.cs.ref.model.core.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntityBasedChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;

public class TaggedBagOfNgrams extends OperableStructure {

	private static final int DEFAULT_NGRAMS_SIZE = 3;
	private static final int DEFAULT_WINDOW_SIZE = 2;

	private Map<List<String>,Integer>[] ngramsB;
	private Map<List<String>,Integer>[] ngramsFB;
	private Map<List<String>,Integer>[] ngramsBA;

	private List<String>[] featLeft;
	private List<String>[] featRight;
	private String entity1Type;
	private String entity2Type;
	private int entity1Index;
	private int entity2Index;
	private int startFB;
	private int endFB;
	private int startB;
	private int endB;
	private int startBA;
	private int endBA;
	private int sizeNgrams;
	private int sizeWindow;

	public TaggedBagOfNgrams(CandidateSentence s){
		this(s,DEFAULT_NGRAMS_SIZE,DEFAULT_WINDOW_SIZE);
	}

	public TaggedBagOfNgrams(CandidateSentence s, int sizeNGrams, int sizeWindow){
		super(s);
		this.sizeNgrams=sizeNGrams;
		this.sizeWindow=sizeWindow;
		ngramsB=new Map[sizeNGrams];
		ngramsBA=new Map[sizeNGrams];
		ngramsFB=new Map[sizeNGrams];
		for(int i=0; i<sizeNGrams;i++){
			ngramsB[i]=new HashMap<List<String>,Integer>();
			ngramsBA[i]=new HashMap<List<String>,Integer>();
			ngramsFB[i]=new HashMap<List<String>,Integer>();
		}
	}

	public int[] getEntityIndexes(Entity[] entities, SequenceFS<Span> sentenceTokens){
		int numTokens = sentenceTokens.size();
		int[] entityIndex=new int[entities.length];
		int sentenceOffset=getCandidateSentence().getSentence().getOffset();
		for(int i=0;i<entities.length; i++){
			Entity entity = entities[i];
			int startEntity = entity.getOffset()-sentenceOffset;
			int endEntity = startEntity+entity.getLength();
			for(int j=0;j<numTokens;j++){
				Span w = sentenceTokens.getElement(j);
				int startTok = w.getStart();
				int endTok = w.getEnd();
				if(startEntity>=startTok && endEntity<=endTok){
					entityIndex[i]=j;
					break;
				}
			}
		}

		return entityIndex;
	}

	private String[] getTokenValues(SequenceFS<Span> tokenization){
		String[] result = new String[tokenization.size()];
		String sentenceValue = getCandidateSentence().getSentence().getValue();

		int sizeTokenization = tokenization.size();
		for(int i=0; i<sizeTokenization; i++){
			Span s = tokenization.getElement(i);
			result[i]=sentenceValue.substring(s.getStart(),s.getEnd());
		}

		return result;
	}

	@Override
	public void initialize() {
		//TODO: In the case of SSK and JSRE there should be only 2 entities
		SequenceFS<Span> tokenization = getFeatures(EntityBasedChunkingFG.class);
		String[] tokens = getTokenValues(tokenization);
		//I will assume that we have two entities only
		Entity[] entities = getCandidateSentence().getEntities();
		int[] entityIndexes =getEntityIndexes(entities, tokenization);

		entity1Index=entityIndexes[0];
		entity2Index=entityIndexes[1];

		entity1Type=entities[0].getEntityType();
		entity2Type=entities[1].getEntityType();
		if(entity1Index>entity2Index){
			int temp=entity1Index;
			entity1Index=entity2Index;
			entity2Index=temp;
			entity1Type=entities[1].getEntityType();
			entity2Type=entities[0].getEntityType();
		}

		startFB=0;
		endFB=entity1Index;
		startB=entity1Index+1;
		endB=entity2Index;
		startBA=entity2Index+1;
		endBA=tokenization.size();

		createNGrams(tokens,ngramsFB,startFB,entity2Index);
		createNGrams(tokens,ngramsB,entity1Index,entity2Index);
		createNGrams(tokens,ngramsBA,entity1Index,endBA-1);
		featLeft=createFeatureToken(tokens, entity1Index);
		featRight=createFeatureToken(tokens, entity2Index);
	}

	private List<String>[] createFeatureToken(String[] sent, int pos){
		List<String>[] feat = new List[sizeWindow*2 + 1];
		feat[sizeWindow]=new ArrayList<String>();
		if(pos==entity1Index){
			feat[sizeWindow].add(entity1Type);
		}else if(pos==entity2Index){
			feat[sizeWindow].add(entity2Type);
		}else{
			feat[sizeWindow].add(sent[pos]);
		}

		int left=Math.max(0, pos-sizeWindow);
		int right=Math.min(sent.length-1, pos+sizeWindow);

		for(int i=0; i<sizeWindow; i++){
			feat[i]=null;
		}
		for(int i=sizeWindow+1; i<sizeWindow*2 + 1; i++){
			feat[i]=null;
		}

		for(int i=pos-1; i>=left ;i--){
			feat[sizeWindow+i-pos]= new ArrayList<String>();
			if(i==entity1Index){
				feat[sizeWindow+i-pos].add(entity1Type);
			}else if(i==entity2Index){
				feat[sizeWindow+i-pos].add(entity2Type);
			}else{
				feat[sizeWindow+i-pos].add(sent[i]);
			}
		}
		for(int i=pos+1; i<=right ;i++){
			feat[sizeWindow+i-pos]= new ArrayList<String>();
			if(i==entity1Index){
				feat[sizeWindow+i-pos].add(entity1Type);
			}else if(i==entity2Index){
				feat[sizeWindow+i-pos].add(entity2Type);
			}else{
				feat[sizeWindow+i-pos].add(sent[i]);
			}
		}

		return feat;
	}

	public void createNGrams(String[] tokens, Map<List<String>,Integer>[] ngrams,
			int begin, int end){
		for(int i=0; i<sizeNgrams; i++){
			for(int j=begin; j<=end-i; j++){
				List<String> ngram = new ArrayList<String>();
				for(int k=j; k<=j+i; k++){
					if(k==entity1Index){
						ngram.add(entity1Type);
					}else if(k==entity2Index){
						ngram.add(entity2Type);
					}else{
						ngram.add(tokens[k]);
					}
				}

				Integer freq = ngrams[i].get(ngram);
				if(freq==null){
					freq=0;
				}
				ngrams[i].put(ngram, freq+1);
			}
		}
	}

	public Map<List<String>, Integer>[] getNgramsB() {
		return ngramsB;
	}

	public Map<List<String>, Integer>[] getNgramsFB() {
		return ngramsFB;
	}

	public Map<List<String>, Integer>[] getNgramsBA() {
		return ngramsBA;
	}

	public List<String>[] getFeatLeft() {
		return featLeft;
	}

	public List<String>[] getFeatRight() {
		return featRight;
	}

	public void add(SequenceFS<? extends Serializable> sequence){
		int pos=entity1Index;
		int left=Math.max(0, pos-sizeWindow);
		int right=Math.min(sequence.size()-1, pos+sizeWindow);
		featLeft[sizeWindow].add(sequence.getElement(pos).toString());
		for(int i=pos-1; i>=left ;i--){
			featLeft[sizeWindow+i-pos].add(sequence.getElement(i).toString());
		}
		for(int i=pos+1; i<=right ;i++){
			featLeft[sizeWindow+i-pos].add(sequence.getElement(i).toString());
		}
		
		pos=entity2Index;
		left=Math.max(0, pos-sizeWindow);
		right=Math.min(sequence.size()-1, pos+sizeWindow);
		featRight[sizeWindow].add(sequence.getElement(pos).toString());
		for(int i=pos-1; i>=left ;i--){
			featRight[sizeWindow+i-pos].add(sequence.getElement(i).toString());
		}
		for(int i=pos+1; i<=right ;i++){
			featRight[sizeWindow+i-pos].add(sequence.getElement(i).toString());
		}
	}
}
