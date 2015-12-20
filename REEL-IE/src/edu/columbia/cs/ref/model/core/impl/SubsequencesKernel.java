package edu.columbia.cs.ref.model.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.InvalidFormatException;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntityBasedChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.Kernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.impl.TaggedSequence;

public class SubsequencesKernel extends Kernel {

	static final int DEFAULT_FORE = 4;
	static final int DEFAULT_INTRA = 4;
	static final int DEFAULT_AFTER = 4;
	static final double DEFAULT_LAMBDA = 0.75;
	static final boolean DEFAULT_CACHE = true; 
	static final boolean DEFAULT_NORM = true; 
	//static double results=0;
	static double num=0;

	// max length of fore subseq
	int m_nFore;
	// max length of intra subseq
	int m_nIntra;
	// max length of after subseq
	int m_nAfter;
	// gap penalty
	double m_lambda;
	// true if cached self kernels
	boolean m_bCache;
	// true if normalized kernels
	boolean m_bNorm;
	
	protected Map<TaggedSequence,Double> m_mapStoK;
	
	public SubsequencesKernel(int nFore, int nIntra, int nAfter,
			double lambda, boolean bCache, boolean bNorm)
	{
		m_nFore = nFore;
		m_nIntra = nIntra;
		m_nAfter = nAfter;
		m_lambda = lambda;
		m_bCache = bCache;
		m_bNorm = bNorm;
		
		m_mapStoK = Collections.synchronizedMap(new HashMap<TaggedSequence,Double>());
	}

	public SubsequencesKernel() 
	{
		m_nFore = DEFAULT_FORE;
		m_nIntra = DEFAULT_INTRA;
		m_nAfter = DEFAULT_AFTER;
		m_lambda = DEFAULT_LAMBDA;
		m_bCache = DEFAULT_CACHE;
		m_bNorm = DEFAULT_NORM;
		
		m_mapStoK = Collections.synchronizedMap(new HashMap<TaggedSequence,Double>());
	}
	
	@Override
	public OperableStructure createOperableStructure(CandidateSentence sent) {
		return new TaggedSequence(sent);
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

	
	public double normKernel(TaggedSequence s1, TaggedSequence s2) 
	{
		double k1 = selfKernel(s1);
		double k2 = selfKernel(s2);
		
		double k = kernel(s1, s2);
		if (k == 0)
			return 0;
		
		// normalize
		return k / Math.sqrt (k1 * k2);				
	}


	public double selfKernel(TaggedSequence s)
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


	public double kernel(TaggedSequence s1, TaggedSequence s2)
	{
		String[][] fore1 = s1.getInstanceFB();
		String[][] fore2 = s2.getInstanceFB();

		String[][] intra1 = s1.getInstanceB();
		String[][] intra2 = s2.getInstanceB();

		String[][] after1 = s1.getInstanceBA();
		String[][] after2 = s2.getInstanceBA();

		int nLenIntra = Math.min(Math.max(m_nIntra,
				Math.max(m_nFore - 1, m_nAfter - 1)),
				Math.min(intra1.length, intra2.length));
		double[] sk = stringKernel(intra1, intra2, nLenIntra, 1.0);
		double[] intraK = new double[nLenIntra];
		for (int i = 0; i < intraK.length; i++) {
			intraK[i] = sk[i] *
			commonEquals(s1.getEntity1Feats(),s2.getEntity1Feats())*
			commonEquals(s1.getEntity2Feats(),s2.getEntity2Feats())*
			Math.pow(m_lambda, intra1.length + 2 + intra2.length + 2);
		}

		double foreKernel = foreKernel(fore1, fore2, intraK);
		double intraKernel = intraKernel(intraK);
		double afterKernel = afterKernel(after1, after2, intraK);
		double modKernel = 0.0;
		if (intra1.length == 0 && intra2.length == 0)
			modKernel = commonEquals(s1.getEntity1Feats(),s2.getEntity1Feats())*
			commonEquals(s1.getEntity2Feats(),s2.getEntity2Feats())*
			Math.pow(m_lambda, 2 + 2);
		
		return foreKernel + intraKernel + afterKernel + modKernel;
	}

	protected double foreKernel(String[][] fore1, String[][] fore2, 
			double[] intraK)
	{
		double k = 0.0;
		double[] foreK = sKernel(fore1, fore2, m_nFore - 1, m_lambda);

		for (int i = 1; i < m_nFore; i++) {
			for (int j = 1; j <= m_nFore - i && j <= intraK.length; j++)
				k = k + foreK[i - 1] * intraK[j - 1];
		}

		return k;
	}


	protected double intraKernel(double[] intraK)
	{
		double k = 0.0;
		for (int i = 0; i < m_nIntra && i < intraK.length; i++)
			k = k + intraK[i];

		return k;
	}


	protected double afterKernel(String[][] after1, String[][] after2, 
			double[] intraK)
	{
		//    String[][] aft1 = mirror(after1);
		//    String[][] aft2 = mirror(after2);
		double k = 0.0;
		double[] afterK = sKernel(after1, after2, m_nAfter - 1, m_lambda);

		for (int i = 1; i < m_nAfter; i++) {
			for (int j = 1; j <= m_nAfter - i && j <= intraK.length; j++)
				k = k + afterK[i - 1] * intraK[j - 1];
		}

		return k;
	}


	protected double[] stringKernel(String[][] s, String[][] t, 
			int n, double lambda)
	{
		int slen = s.length;
		int tlen = t.length;

		double[][] K = new double[n + 1][slen * tlen];

		for (int j = 0; j < slen; j++)
			for (int k = 0; k < tlen; k++)
				K[0][k * slen + j] = 1;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < slen - 1; j++) {
				double sum = 0.0;
				for (int k = 0; k < tlen - 1; k++) {
					sum = lambda * (sum + 
							lambda * common(t[k], s[j])  * K[i][k * slen + j]);
					K[i + 1][(k + 1) * slen + j + 1] = 
						lambda * K[i + 1][(k + 1) * slen  + j] + sum;
				}
			}
		}

		double[] result = new double[n];
		for (int l = 0; l < result.length; l++) {
			result[l] = 0.0;
			for (int j = 0; j < slen; j++) {
				for (int k = 0; k < tlen; k++)
					result[l] = result[l] + lambda * lambda * common(t[k], s[j]) 
					* K[l][k * slen + j];
			}
		}

		//    double powl = Math.pow(lambda, n);
		//    return r / (powl * powl);
		return result;
	}


	protected double[] sKernel(String[][] s, String[][] t, int n, double lambda)
	{
		int slen = s.length;
		int tlen = t.length;

		double[][] K = new double[n + 1][(slen + 1) * (tlen + 1)];

		for (int j = 0; j < slen + 1; j++)
			for (int k = 0; k < tlen + 1; k++)
				K[0][k * (slen + 1) + j] = 1;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < slen; j++) {
				double sum = 0.0;
				for (int k = 0; k < tlen; k++) {
					sum = lambda * (sum + 
							lambda * common(t[k], s[j]) * K[i][k * (slen + 1) + j]);
					K[i + 1][(k + 1) * (slen + 1) + j + 1] = 
						lambda * K[i + 1][(k + 1) * (slen + 1) + j] + sum;
				}
			}
		}

		//    double powl = Math.pow(lambda, n);
		//    return K[n][tlen * (slen + 1) + slen] / (powl * powl);
		double[] result = new double[n];
		for (int i = 0; i < result.length; i++)
			result[i] = K[i + 1][tlen * (slen + 1) + slen];
		return result;
	}


	protected int common(String[] s, String[] t)
	{
		assert s.length == t.length;
		int nCount = 0;
		for (int i = 0; i < s.length; i++)
			if (s[i] != null && s[i]==t[i])
				nCount++;
		
		return nCount;
	}
	
	protected int commonEquals(String[] s, String[] t)
	{
		assert s.length == t.length;
		int nCount = 0;
		for (int i = 0; i < s.length; i++)
			if (s[i] != null && s[i].equals(t[i]))
				nCount++;
		
		return nCount;
	}
	
	static Map<OperableStructure,Map<OperableStructure,Double>> dynamic = Collections.synchronizedMap(new HashMap<OperableStructure,Map<OperableStructure,Double>>());
	
	@Override
	public double evaluate(OperableStructure s1, OperableStructure s2) {
		double result;
		
//		if(s1.hashCode()>s2.hashCode()){
//			OperableStructure temp=s2;
//			s2=s1;
//			s1=temp;
//		}
		
//		Map<OperableStructure,Double> dyn1 = dynamic.get(s1);
//		if(dyn1!=null){
//			Double dyn2 = dyn1.get(s2);
//			if(dyn2!=null){
//				//System.out.println("Reusing");
//				return dyn2;
//			}
//		}
		
		((TaggedSequence)s1).normalizeFeatures();
		((TaggedSequence)s2).normalizeFeatures();
		if(m_bNorm){
			result=normKernel((TaggedSequence)s1, (TaggedSequence)s2);
		}else{
			result=kernel((TaggedSequence)s1, (TaggedSequence)s2);
		}
		
//		dyn1 = dynamic.get(s1);
//		if(dyn1==null){
//			dyn1=Collections.synchronizedMap(new HashMap<OperableStructure,Double>());
//		}
//		dyn1.put(s2, result);
//		dynamic.put(s1, dyn1);
//		
//		num++;
//		if(num%10000==0){
//			System.out.println(num);
//		}
		
		return result ;
	}

}
