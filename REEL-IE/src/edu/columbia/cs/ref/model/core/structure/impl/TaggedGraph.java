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
import edu.columbia.cs.ref.algorithm.feature.generation.impl.StanfordNLPDependencyGraphFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.feature.impl.GraphFS;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.utils.DependencyGraph;
import edu.columbia.cs.utils.Pair;
import edu.columbia.cs.utils.SimpleGraph;
import edu.columbia.cs.utils.TokenInformation;

public class TaggedGraph extends OperableStructure {

	private DependencyGraph graph;
	private int entity1Index;
	private int entity2Index;
	private String entity1Type;
	private String entity2Type;
	
	public TaggedGraph(CandidateSentence s){
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
		SequenceFS<Span> tokenization = getFeatures(EntityBasedChunkingFG.class);
		String[] tokens = getTokenValues(tokenization);
		
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
		
		graph = new DependencyGraph(tokens.length,1.0);
		for(int i=0; i<tokens.length; i++){
			TokenInformation newNode = new TokenInformation();
			if(i==entity1Index){
				newNode.add(entity1Type);
				newNode.setIsEntity1(true, entity1Type);
			}else if(i==entity2Index){
				newNode.add(entity2Type);
				newNode.setIsEntity2(true, entity2Type);
			}else{
				newNode.add(tokens[i]);
			}
			graph.addNode(i, newNode);
		}
		
		GraphFS<Integer,String> depGraph= getFeatures(StanfordNLPDependencyGraphFG.class);
		SimpleGraph<Integer,String> g = depGraph.getGraph();
		for(Pair<Pair<Integer,Integer>,String> edge : g.getEdges()){
			Pair<Integer,Integer> edgeNodes = edge.first();
			String label = edge.second();
			graph.addEdge(edgeNodes.first(), edgeNodes.second(), label);
		}
		
		graph.normalizePaths();
	}

	
	public void add(SequenceFS<? extends Serializable> sequence){
		int seqSize=sequence.size();
		for(int i=0; i<seqSize; i++){
			TokenInformation nodeLabel =graph.getNodeLabel(i);
			String feature = sequence.getElement(i).toString();
			nodeLabel.add(feature);
			graph.addNode(i, nodeLabel);
		}
	}

	public DependencyGraph getGraph() {
		return graph;
	}
}
