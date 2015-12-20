package edu.columbia.cs.ref.model.core.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntityBasedChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.optimization.FeaturesDictionary;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;

public class TaggedSequence extends OperableStructure {
	private int startFB;
	private int endFB;
	private int startB;
	private int endB;
	private int startBA;
	private int endBA;
	private int entity1Index;
	private int entity2Index;
	private List<String>[] fb;
	private List<String>[] b;
	private List<String>[] ba;
	private List<String> entity1;
	private List<String> entity2;
	private String entity1Type;
	private String entity2Type;
	private static FeaturesDictionary fd = new FeaturesDictionary();
	private transient boolean normalized = false;
	
	public TaggedSequence(CandidateSentence s){
		super(s);
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
		
		entity1=new ArrayList<String>();
		entity1.add(entity1Type);
		entity2=new ArrayList<String>();
		entity2.add(entity2Type);
		
		fb=new List[endFB-startFB];
		for(int i=startFB; i<endFB; i++){
			fb[i-startFB]=new ArrayList<String>();
			fb[i-startFB].add(tokens[i]);
		}
		
		b=new List[Math.max(endB-startB,0)];
		for(int i=startB; i<endB; i++){
			b[i-startB]=new ArrayList<String>();
			b[i-startB].add(tokens[i]);
		}

		ba=new List[endBA-startBA];
		for(int i=startBA; i<endBA; i++){
			ba[i-startBA]=new ArrayList<String>();
			ba[i-startBA].add(tokens[i]);
		}
	}

	//TODO: BAD CODE AHEAD... REFACTOR
	public String[][] getInstanceFB() {
		String[][] result = new String[fb.length][entity1.size()];
		
		for(int i=0; i<fb.length; i++){
			String[] positionFeatures = new String[fb[i].size()];
			result[i]=fb[i].toArray(positionFeatures);
		}
		
		return result;
	}

	public String[][] getInstanceB() {
		String[][] result = new String[b.length][entity1.size()];
		
		for(int i=0; i<b.length; i++){
			String[] positionFeatures = new String[b[i].size()];
			result[i]=b[i].toArray(positionFeatures);
		}
		
		return result;
	}

	public String[][] getInstanceBA() {
		String[][] result = new String[ba.length][entity1.size()];
		
		for(int i=0; i<ba.length; i++){
			String[] positionFeatures = new String[ba[i].size()];
			result[i]=ba[i].toArray(positionFeatures);
		}
		
		return result;
	}

	public String[] getEntity1Feats() {
		String[] result = new String[entity1.size()];
		entity1.toArray(result);
		return result;
	}

	public String[] getEntity2Feats() {
		String[] result = new String[entity2.size()];
		entity2.toArray(result);
		return result;
	}

	public synchronized void normalizeFeatures() {
		if(!normalized){
			for(int i=0; i<fb.length; i++){
				List<String> list = fb[i];
				List<String> newList = new ArrayList<String>();
				int sizeList = list.size();
				for(int j=0; j<sizeList; j++){
					String str = list.get(j);
					newList.add(fd.getFeature(str));
				}
				fb[i]=newList;
			}
			
			for(int i=0; i<b.length; i++){
				List<String> list = b[i];
				List<String> newList = new ArrayList<String>();
				int sizeList = list.size();
				for(int j=0; j<sizeList; j++){
					String str = list.get(j);
					newList.add(fd.getFeature(str));
				}
				b[i]=newList;
			}
			
			for(int i=0; i<ba.length; i++){
				List<String> list = ba[i];
				List<String> newList = new ArrayList<String>();
				int sizeList = list.size();
				for(int j=0; j<sizeList; j++){
					String str = list.get(j);
					newList.add(fd.getFeature(str));
				}
				ba[i]=newList;
			}
			
			normalized=true;
		}
	}
	
	public void add(SequenceFS<? extends Serializable> sequence){
		for(int j=0; j<fb.length; j++){
			fb[j].add(sequence.getElement(startFB+j).toString());
		}
		entity1.add(sequence.getElement(entity1Index).toString());
		for(int j=0; j<b.length; j++){
			b[j].add(sequence.getElement(startB+j).toString());
		}
		entity2.add(sequence.getElement(entity2Index).toString());
		for(int j=0; j<ba.length; j++){
			ba[j].add(sequence.getElement(startBA+j).toString());
		}
	}
}
