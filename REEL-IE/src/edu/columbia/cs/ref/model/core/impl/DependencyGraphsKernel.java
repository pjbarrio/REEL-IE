package edu.columbia.cs.ref.model.core.impl;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import opennlp.tools.util.InvalidFormatException;

import cern.colt.matrix.tdouble.algo.SparseDoubleAlgebra;

import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntityBasedChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.StanfordNLPDependencyGraphFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.core.Kernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.impl.TaggedGraph;
import edu.columbia.cs.utils.SimpleGraphUtils;


public class DependencyGraphsKernel extends Kernel {
	static int numExec=0;

	// gap penalty
	double m_lambda;
	
	SimpleGraphUtils util = new SimpleGraphUtils();
	private boolean m_bCache=true;
	static SparseDoubleAlgebra al;
	protected Map<TaggedGraph,Double> m_mapStoK;
	private boolean m_bNorm=true;
	private LocalizedDependencyGraphsKernel innerKernel = new LocalizedDependencyGraphsKernel();
	private LocalizedDependencyGraphsKernel outerKernel = new LocalizedDependencyGraphsKernel();
	private LocalizedDependencyGraphsKernel fullKernel = new LocalizedDependencyGraphsKernel();
	
	static{
		al = new SparseDoubleAlgebra();
	}

	public DependencyGraphsKernel(double gamma, double lambda)
	{
		m_lambda = lambda;
		m_mapStoK = Collections.synchronizedMap(new HashMap<TaggedGraph,Double>());
	}


	public DependencyGraphsKernel() 
	{
		// Default values.
		m_mapStoK = Collections.synchronizedMap(new HashMap<TaggedGraph,Double>());
		
	}
	
	public double normKernel(TaggedGraph s1, TaggedGraph s2) 
	{
		double k1 = selfKernel(s1);
		double k2 = selfKernel(s2);
		
		double k = kernel(s1, s2);
		if (k == 0)
			return 0;
		
		// normalize
		return k / Math.sqrt (k1 * k2);				
	}
	
	public double selfKernel(TaggedGraph s)
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
	
	public double kernel(TaggedGraph s1, TaggedGraph s2){
		return /*fullKernel.evaluate(s1.getGraph(),s2.getGraph()) +*/
		 innerKernel.evaluate(s1.getGraph().getShortestPathDependencyGraph(), 
				              s2.getGraph().getShortestPathDependencyGraph())/* +
	     /*outerKernel.evaluate(s1.getGraph().getNotShortestPathDependencyGraph(), 
	    		              s2.getGraph().getNotShortestPathDependencyGraph())*/;
	}
	
	public static Map<OperableStructure,Map<OperableStructure,Double>> dynamic = Collections.synchronizedMap(new HashMap<OperableStructure,Map<OperableStructure,Double>>());


	@Override
	public OperableStructure createOperableStructure(CandidateSentence sent) {
		return new TaggedGraph(sent);
	}

	@Override
	protected List<FeatureGenerator> createMandatoryFeatureGenerators() {
		List<FeatureGenerator> fg = new ArrayList<FeatureGenerator>();

		//TODO: the tokenizer should be received in the constructor
		try {
			//The only mandatory feature is the chunker... However, the chunker
			//also depends on the results of the tokenizer. Thus, we need to create a
			//tokenizer and a DependentFeatureGenerator in this case
			fg.add(new StanfordNLPDependencyGraphFG("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz",new EntityBasedChunkingFG(new OpenNLPTokenizationFG("en-token.bin"))));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return fg;
	}


	@Override
	public double evaluate(OperableStructure s1, OperableStructure s2) {
//		if(s1.hashCode()>s2.hashCode()){
//			OperableStructure temp=s2;
//			s2=s1;
//			s1=temp;
//		}
		
//		Map<OperableStructure,Double> dyn1 = dynamic.get(s1);
//		if(dyn1!=null){
//			Double dyn2 = dyn1.get(s2);
//			if(dyn2!=null){
//				return dyn2;
//			}
//		}
		
		double result;
		if(m_bNorm){
			result=normKernel((TaggedGraph)s1, (TaggedGraph)s2);
		}else{
			result=kernel((TaggedGraph)s1, (TaggedGraph)s2);
		}
		
//		dyn1 = dynamic.get(s1);
//		if(dyn1==null){
//			dyn1=Collections.synchronizedMap(new HashMap<OperableStructure,Double>());
//		}
//		dyn1.put(s2, result);
//		dynamic.put(s1, dyn1);
		
//		numExec++;
//		if(numExec%10000==0){
//			System.out.println(numExec);
//		}
		
		return result;
	}
}
