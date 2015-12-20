package edu.columbia.cs.cg.prdualrank.inference.quest.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.algo.SparseDoubleAlgebra;
import cern.colt.matrix.tdouble.algo.decomposition.DenseDoubleEigenvalueDecomposition;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.SparseRCDoubleMatrix2D;

import edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph;
import edu.columbia.cs.cg.prdualrank.inference.convergence.ConvergenceFinder;
import edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.pattern.resources.Matchable;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.utils.Pair;

/**
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * For further information, <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>.
 * 
 * <br><br>
 * 
 * <b>Description</b><br><br>
 * 
 * QuestCalculator based on <b>Matrix Multiplication</b>. Useful for fully-connected graphs or for graphs that will take a long number of iterations to converge.
 * <br>
 * For sparse graphs, see {@link MapBasedQuestCalculator}.
 * 
 * <br>
 * For more information, please read the Inference formulas in <b>Figure 4</b> of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @see MapBasedQuestCalculator
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class MatricesBasedQuestCalculator<T extends Matchable,D extends Document> implements QuestCalculator<T,D> {
	private Map<Pattern<T,D>,Integer> patternIds;
	private Map<Relationship,Integer> tupleIds;
	private Map<Integer,Pattern<T,D>> patternIdsInverse;
	private Map<Integer,Relationship> tupleIdsInverse;
	private Map<Relationship, Double> tuplesPrecisionMap;
	private Map<Relationship, Double> tuplesRecallMap;
	private Map<Pattern<T,D>, Double> patternsPrecisionMap;
	private Map<Pattern<T,D>, Double> patternsRecallMap;
	private Set<Relationship> seeds;
	private int numberIterations;
	private boolean keepInitialValueSeeds = true;
	private SparseDoubleAlgebra al = new SparseDoubleAlgebra();
	private DenseDoubleAlgebra denseAl = new DenseDoubleAlgebra();
	private boolean completeConvergence=true;
	private static final double COMPARISON_THRESHOLD = 1e-10;
	
	/**
	 * Instantiates a new matrices based quest calculator.
	 */
	public MatricesBasedQuestCalculator(){
		patternIds = new HashMap<Pattern<T,D>, Integer>();
		tupleIds = new HashMap<Relationship, Integer>();
		patternIdsInverse=new HashMap<Integer,Pattern<T,D>>();
		tupleIdsInverse=new HashMap<Integer,Relationship>();
		tuplesPrecisionMap=new HashMap<Relationship,Double>();
		tuplesRecallMap=new HashMap<Relationship,Double>();
		patternsPrecisionMap=new HashMap<Pattern<T,D>,Double>();
		patternsRecallMap=new HashMap<Pattern<T,D>,Double>();
	}

	/**
	 * Instantiates a new matrices based quest calculator specifying a limited number of iterations.
	 *
	 * @param numberIterations the number iterations to be simulated.
	 */
	public MatricesBasedQuestCalculator(int numberIterations) {
		this();
		this.completeConvergence=false;
		this.numberIterations=numberIterations;
	}
	
	private SparseDoubleMatrix1D getInitialPrecisionMatrix(PRDualRankGraph<T,D> gs){
		Set<Relationship> tuples = gs.getTuples();
		SparseDoubleMatrix1D seedTuples = new SparseDoubleMatrix1D(tuples.size());
		
		for(Relationship tuple : seeds){
			Integer tupleId = tupleIds.get(tuple);
			if(tupleId==null){
				tupleId=tupleIds.size();
				tupleIds.put(tuple, tupleId);
				tupleIdsInverse.put(tupleId, tuple);
			}
			
			seedTuples.set(tupleId,1.0);
		}
		
		return seedTuples;
	}
	
	private SparseDoubleMatrix1D getInitialRecallMatrix(PRDualRankGraph<T,D> gs){
		Set<Relationship> tuples = gs.getTuples();
		SparseDoubleMatrix1D seedTuples = new SparseDoubleMatrix1D(tuples.size());
		
		for(Relationship tuple : seeds){
			Integer tupleId = tupleIds.get(tuple);
			if(tupleId==null){
				tupleId=tupleIds.size();
				tupleIds.put(tuple, tupleId);
				tupleIdsInverse.put(tupleId, tuple);
			}
			
			seedTuples.set(tupleId,1.0/seeds.size());
		}
		
		return seedTuples;
	}
	
	private SparseRCDoubleMatrix2D getLeftMatrixForPrecision(PRDualRankGraph<T,D> gs){
		Set<Relationship> tuples = gs.getTuples();
		Set<Pattern<T,D>> patterns = gs.getPatterns();
		
		SparseRCDoubleMatrix2D matrix = new SparseRCDoubleMatrix2D(patterns.size(),tuples.size());
		
		for(Relationship tuple : tuples){
			Integer tupleId = tupleIds.get(tuple);
			if(tupleId==null){
				tupleId=tupleIds.size();
				tupleIds.put(tuple, tupleId);
				tupleIdsInverse.put(tupleId, tuple);
			}
			
			for(Pattern<T,D> pattern : gs.getMatchingPatterns(tuple)){
				Integer patternId = patternIds.get(pattern);
				if(patternId==null){
					patternId=patternIds.size();
					patternIds.put(pattern, patternId);
					patternIdsInverse.put(patternId, pattern);
				}
				
				double freqTransition = gs.getMatchingFrequency(pattern, tuple);
				double freqPattern= gs.getFreqency(pattern);
				double entryMatrix = freqTransition/freqPattern;
				
				matrix.set(patternId,tupleId,entryMatrix);
			}
		}
		
		return matrix;
	}
	
	private SparseRCDoubleMatrix2D getLeftMatrixForRecall(PRDualRankGraph<T,D> gs){
		Set<Relationship> tuples = gs.getTuples();
		Set<Pattern<T,D>> patterns = gs.getPatterns();
		
		SparseRCDoubleMatrix2D matrix = new SparseRCDoubleMatrix2D(patterns.size(),tuples.size());
		
		for(Relationship tuple : tuples){
			Integer tupleId = tupleIds.get(tuple);
			if(tupleId==null){
				tupleId=tupleIds.size();
				tupleIds.put(tuple, tupleId);
				tupleIdsInverse.put(tupleId, tuple);
			}
			
			for(Pattern<T,D> pattern : gs.getMatchingPatterns(tuple)){
				Integer patternId = patternIds.get(pattern);
				if(patternId==null){
					patternId=patternIds.size();
					patternIds.put(pattern, patternId);
					patternIdsInverse.put(patternId, pattern);
				}
				
				double freqTransition = gs.getMatchingFrequency(pattern, tuple);
				double freqTuple= gs.getFrequency(tuple);
				double entryMatrix = freqTransition/freqTuple;
				
				matrix.set(patternId,tupleId,entryMatrix);
			}
		}
		
		return matrix;
	}
	
	private SparseRCDoubleMatrix2D getRightMatrixForPrecision(PRDualRankGraph<T,D> gs){
		Set<Relationship> tuples = gs.getTuples();
		Set<Pattern<T,D>> patterns = gs.getPatterns();
		
		SparseRCDoubleMatrix2D matrix = new SparseRCDoubleMatrix2D(tuples.size(),patterns.size());
		
		for(Relationship tuple : tuples){
			Integer tupleId = tupleIds.get(tuple);
			if(tupleId==null){
				tupleId=tupleIds.size();
				tupleIds.put(tuple, tupleId);
				tupleIdsInverse.put(tupleId, tuple);
			}
			
			for(Pattern<T,D> pattern : gs.getMatchingPatterns(tuple)){
				Integer patternId = patternIds.get(pattern);
				if(patternId==null){
					patternId=patternIds.size();
					patternIds.put(pattern, patternId);
					patternIdsInverse.put(patternId, pattern);
				}
				
				double freqTransition = gs.getMatchingFrequency(pattern, tuple);
				double freqTuple= gs.getFrequency(tuple);
				double entryMatrix = freqTransition/freqTuple;
				
				matrix.set(tupleId,patternId,entryMatrix);
			}
		}
		
		return matrix;
	}
	
	private SparseRCDoubleMatrix2D getRightMatrixForRecall(PRDualRankGraph<T,D> gs){
		Set<Relationship> tuples = gs.getTuples();
		Set<Pattern<T,D>> patterns = gs.getPatterns();
		
		SparseRCDoubleMatrix2D matrix = new SparseRCDoubleMatrix2D(tuples.size(),patterns.size());
		
		for(Relationship tuple : tuples){
			Integer tupleId = tupleIds.get(tuple);
			if(tupleId==null){
				tupleId=tupleIds.size();
				tupleIds.put(tuple, tupleId);
				tupleIdsInverse.put(tupleId, tuple);
			}
			
			for(Pattern<T,D> pattern : gs.getMatchingPatterns(tuple)){
				Integer patternId = patternIds.get(pattern);
				if(patternId==null){
					patternId=patternIds.size();
					patternIds.put(pattern, patternId);
					patternIdsInverse.put(patternId, pattern);
				}
				
				double freqTransition = gs.getMatchingFrequency(pattern, tuple);
				double freqPattern= gs.getFreqency(pattern);
				double entryMatrix = freqTransition/freqPattern;
				
				matrix.set(tupleId,patternId,entryMatrix);
			}
		}
		
		return matrix;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#runQuestP(edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph)
	 */
	@Override
	public void runQuestP(PRDualRankGraph<T,D> gs) {
		SparseRCDoubleMatrix2D Mt = getLeftMatrixForPrecision(gs);
		SparseRCDoubleMatrix2D Mp = getRightMatrixForPrecision(gs);
		SparseDoubleMatrix1D p0t = getInitialPrecisionMatrix(gs);
		if(keepInitialValueSeeds){
			SparseRCDoubleMatrix2D transposeMt = new SparseRCDoubleMatrix2D(Mt.columns(),Mt.rows());
			for(int j=0; j<Mt.rows(); j++){
				for(int k=0; k<Mt.columns(); k++){
					transposeMt.set(k, j, Mt.get(j, k));
				}
			}
			int p0tSize=(int) p0t.size();
			for(int i=0; i<p0tSize; i++){
				if(p0t.get(i)!=0){
					SparseDoubleMatrix1D ei = new SparseDoubleMatrix1D(p0tSize);
					ei.set(i, 1);
					
					DoubleMatrix1D x = al.solve(transposeMt, ei);
					
					long size = x.size();
					double sum=0;
					for(int j=0; j<size;j++){
						sum+=x.get(j);
					}
					for(int j=0; j<size;j++){
						Mp.set(i,j, x.get(j)/sum);
					}
				}
			}
		}
		
		DoubleMatrix2D mult = denseAl.mult(Mp, Mt);
		DoubleMatrix1D patternsPrecision;
		DoubleMatrix1D tuplesPrecision;
		if(!completeConvergence){
			patternsPrecision=denseAl.mult(Mt,denseAl.mult(denseAl.pow(mult,numberIterations-1),p0t));
			tuplesPrecision=denseAl.mult(denseAl.pow(mult,numberIterations),p0t);
		}else{
			DenseDoubleEigenvalueDecomposition decomp = 
				new DenseDoubleEigenvalueDecomposition(mult);
			
			DoubleMatrix2D D = decomp.getD();
			for(int i=0; i<D.rows(); i++){
				if(Math.abs(D.get(i, i)-1.0)>COMPARISON_THRESHOLD){
					if(D.get(i, i)<1.0){
						D.set(i, i, 0);
					}else{
						D.set(i, i, Double.POSITIVE_INFINITY);
					}
					
				}
			}
			
			DoubleMatrix2D V = decomp.getV();
			DoubleMatrix2D Vinverse = denseAl.inverse(V);
			DoubleMatrix2D limitMatrix = denseAl.mult(denseAl.mult(V,D),Vinverse);
			
			for(int i=0; i<limitMatrix.rows(); i++){
				for(int j=0; j<limitMatrix.columns(); j++){
					if(limitMatrix.get(i, j)<COMPARISON_THRESHOLD){
						limitMatrix.set(i, j, 0);
					}
				}
			}
			patternsPrecision=denseAl.mult(Mt,denseAl.mult(limitMatrix,p0t));
			tuplesPrecision=denseAl.mult(limitMatrix,p0t);
		}
		
		int numTuples = (int) tuplesPrecision.size();
		for(int i=0; i<numTuples; i++){
			Relationship tuple = tupleIdsInverse.get(i);
			tuplesPrecisionMap.put(tuple, tuplesPrecision.get(i));
		}
		
		int numPatterns = (int) patternsPrecision.size();
		for(int i=0; i<numPatterns; i++){
			Pattern<T,D> pattern = patternIdsInverse.get(i);
			patternsPrecisionMap.put(pattern, patternsPrecision.get(i));
		}
		
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#runQuestR(edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph)
	 */
	@Override
	public void runQuestR(PRDualRankGraph<T,D> gs) {
		SparseRCDoubleMatrix2D Mt = getLeftMatrixForRecall(gs);
		SparseRCDoubleMatrix2D Mp = getRightMatrixForRecall(gs);
		SparseDoubleMatrix1D r0t = getInitialRecallMatrix(gs);
		
		DoubleMatrix2D mult = denseAl.mult(Mp, Mt);
		DoubleMatrix1D patternsRecall;
		DoubleMatrix1D tuplesRecall;
		if(!completeConvergence){
			patternsRecall=denseAl.mult(Mt,denseAl.mult(denseAl.pow(mult,numberIterations-1),r0t));
			tuplesRecall=denseAl.mult(denseAl.pow(mult,numberIterations),r0t);
		}else{
			DenseDoubleEigenvalueDecomposition decomp = 
				new DenseDoubleEigenvalueDecomposition(mult);
			
			DoubleMatrix2D D = decomp.getD();
			for(int i=0; i<D.rows(); i++){
				if(Math.abs(D.get(i, i)-1.0)>COMPARISON_THRESHOLD){
					if(D.get(i, i)<1.0){
						D.set(i, i, 0);
					}else{
						D.set(i, i, Double.POSITIVE_INFINITY);
					}
					
				}
			}
			
			DoubleMatrix2D V = decomp.getV();
			DoubleMatrix2D Vinverse = denseAl.inverse(V);
			DoubleMatrix2D limitMatrix = denseAl.mult(denseAl.mult(V,D),Vinverse);
			
			for(int i=0; i<limitMatrix.rows(); i++){
				for(int j=0; j<limitMatrix.columns(); j++){
					if(limitMatrix.get(i, j)<COMPARISON_THRESHOLD){
						limitMatrix.set(i, j, 0);
					}
				}
			}
			patternsRecall=denseAl.mult(Mt,denseAl.mult(limitMatrix,r0t));
			tuplesRecall=denseAl.mult(limitMatrix,r0t);
		}
		
		int numTuples = (int) tuplesRecall.size();
		for(int i=0; i<numTuples; i++){
			Relationship tuple = tupleIdsInverse.get(i);
			tuplesRecallMap.put(tuple, tuplesRecall.get(i));
		}
		
		int numPatterns = (int) patternsRecall.size();
		for(int i=0; i<numPatterns; i++){
			Pattern<T,D> pattern = patternIdsInverse.get(i);
			patternsRecallMap.put(pattern, patternsRecall.get(i));
		}
	}
	
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getTuplePrecisionMap()
	 */
	@Override
	public Map<Relationship, Double> getTuplePrecisionMap() {
		return tuplesPrecisionMap;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getTupleRecallMap()
	 */
	@Override
	public Map<Relationship, Double> getTupleRecallMap() {
		return tuplesRecallMap;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getPatternPrecisionMap()
	 */
	@Override
	public Map<Pattern<T,D>, Double> getPatternPrecisionMap() {
		return patternsPrecisionMap;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getPatternRecallMap()
	 */
	@Override
	public Map<Pattern<T,D>, Double> getPatternRecallMap() {
		return patternsRecallMap;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#setSeeds(java.util.Set)
	 */
	@Override
	public void setSeeds(Set<Relationship> seeds) {
		
		this.seeds = seeds;
		
	}
}
