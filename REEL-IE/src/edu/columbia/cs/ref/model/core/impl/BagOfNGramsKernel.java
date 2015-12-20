package edu.columbia.cs.ref.model.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import opennlp.tools.util.InvalidFormatException;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntityBasedChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.Kernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.impl.TaggedBagOfNgrams;

public class BagOfNGramsKernel extends Kernel {
	boolean m_bCache=true;
	boolean m_bNorm=true;
	static double num=0;

	protected Map<OperableStructure,Double> m_mapStoK;

	public BagOfNGramsKernel() {
		m_mapStoK = Collections.synchronizedMap(new HashMap<OperableStructure,Double>());
	}


	/**
     Computes the normalized string kernel between two strings.
     @param s1 string 1
     @param s2 string 2
     @return normalized value, with 1 for exact match.
	 */
	public double normKernel(TaggedBagOfNgrams s1, TaggedBagOfNgrams s2) 
	{
		double k1 = selfKernel(s1);
		double k2 = selfKernel(s2);
		
		double k = kernel(s1, s2);
		if (k == 0)
			return 0;
		
		// normalize
		return k / Math.sqrt (k1 * k2);				
	}


	public double selfKernel(TaggedBagOfNgrams s)
	{
		if (m_bCache) {
			// get string representation of relation instance
			
			//System.out.println(strText);
			//for (int i = 0; i < s.length; i++)
			//	strText += s[i][0] + " ";
			// get cached value
			Double dblk = (Double) m_mapStoK.get(s);
			if (dblk == null) {
				double k = kernel(s, s);
				m_mapStoK.put(s, k);
				return k;
			}
			
			return dblk.doubleValue();
		}

		return kernel(s, s);
	}


	public double kernel(TaggedBagOfNgrams s1, TaggedBagOfNgrams s2)
	{
		return computeGlobalKernel(s1, s2)+computeLocalKernel(s1, s2);
	}
	
	private double computeGlobalKernel(TaggedBagOfNgrams s1, TaggedBagOfNgrams s2){
		return (countCommonNgrams(s1.getNgramsB(),s2.getNgramsB()) +
				countCommonNgrams(s1.getNgramsFB(),s2.getNgramsFB()) +
				countCommonNgrams(s1.getNgramsBA(),s2.getNgramsBA()))/3.0;
	}
	
	private double countCommonNgrams(Map<List<String>,Integer>[] ngrams1, Map<List<String>,Integer>[] ngrams2){
		double inner=0;
		for(int i=0; i<ngrams1.length; i++){
			for(Entry<List<String>,Integer> entry : ngrams1[i].entrySet()){
				Integer other = ngrams2[i].get(entry.getKey());
				if(other!=null){
					inner+=entry.getValue()*other;
				}
			}
		}
		
		double norm1=0;
		for(int i=0; i<ngrams1.length; i++){
			for(Entry<List<String>,Integer> entry : ngrams1[i].entrySet()){
				Integer other = ngrams1[i].get(entry.getKey());
				if(other!=null){
					inner+=entry.getValue()*other;
				}
			}
		}
		
		double norm2=0;
		for(int i=0; i<ngrams1.length; i++){
			for(Entry<List<String>,Integer> entry : ngrams2[i].entrySet()){
				Integer other = ngrams2[i].get(entry.getKey());
				if(other!=null){
					inner+=entry.getValue()*other;
				}
			}
		}
		
		
		if(norm1==0 || norm2==0){
			return 0;
		}
		
		return inner/(norm1*norm2);
	}
	
	private double computeLocalKernel(TaggedBagOfNgrams s1, TaggedBagOfNgrams s2){
		return (countCommonFeatures(s1.getFeatLeft(),s2.getFeatLeft()) +
				countCommonFeatures(s1.getFeatRight(),s2.getFeatRight()))/2.0;
	}
	
	private double countCommonFeatures(List<String>[] feats1,
			List<String>[] feats2) {
		int sizeWindow=feats1.length;
		int numFeats1=0;
		int numFeats2=0;
		double inner=0;
		for(int j=0; j<sizeWindow; j++){
			if(feats1[j]!=null && feats2[j]!=null){
				int sizeFeats=feats1[j].size();		
				for(int i=0; i<sizeFeats; i++){
					if(feats1[j].get(i).equals(feats2[j].get(i))){
						inner++;
					}
				}
			}
			
			if(feats1[j]!=null){
				numFeats1+=feats1[j].size();
			}
			if(feats2[j]!=null){
				numFeats2+=feats2[j].size();
			}
		}
		
		return inner/Math.sqrt(numFeats1*numFeats2);
	}


	@Override
	public OperableStructure createOperableStructure(CandidateSentence sent) {
		return new TaggedBagOfNgrams(sent);
	}

	@Override
	protected List<FeatureGenerator> createMandatoryFeatureGenerators() {
		List<FeatureGenerator> fg = new ArrayList<FeatureGenerator>();

		//TODO: the tokenizer should be received in the constructor
		try {
			//The only mandatory feature is the chunker... However, the chunker
			//also depends on the results of the tokenizer. Thus, we need to create a
			//tokenizer and a DependentFeatureGenerator in this case
			fg.add(new EntityBasedChunkingFG(new OpenNLPTokenizationFG("en-token.bin")));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return fg;
	}

	
	static Map<OperableStructure,Map<OperableStructure,Double>> dynamic = Collections.synchronizedMap(new HashMap<OperableStructure,Map<OperableStructure,Double>>());
	
	@Override
	public double evaluate(OperableStructure s1, OperableStructure s2) {
		double result;
		//System.out.println("S1= " + normKernel(s1,s1));
		//System.out.println("S2= " + normKernel(s2,s2));
		
		//if(s1.hashCode()>s2.hashCode()){
		//	OperableStructure temp=s2;
		//	s2=s1;
		//	s1=temp;
		//}
		
		//Map<OperableStructure,Double> dyn1 = dynamic.get(s1);
		//if(dyn1!=null){
		//	Double dyn2 = dyn1.get(s2);
		//	if(dyn2!=null){
		//		//System.out.println("Reusing");
		//		return dyn2;
		//	}
		//}
		
		if(m_bNorm){
			result=normKernel((TaggedBagOfNgrams)s1, (TaggedBagOfNgrams) s2);
		}else{
			result=kernel( (TaggedBagOfNgrams)s1, (TaggedBagOfNgrams) s2);
		}
		
		//dyn1 = dynamic.get(s1);
		//if(dyn1==null){
		//	dyn1=Collections.synchronizedMap(new HashMap<OperableStructure,Double>());
		//}
		//dyn1.put(s2, result);
		//dynamic.put(s1, dyn1);
		
		//num++;
		//if(num%10000==0){
		//	System.out.println(num);
		//}

		return result ;
	}

}
