package edu.columbia.cs.ref.model.core.impl;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.SparseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseRCDoubleMatrix2D;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.columbia.cs.utils.DependencyGraph;
import edu.columbia.cs.utils.SimpleGraphNode;
import edu.columbia.cs.utils.SimpleGraphUtils;
import edu.columbia.cs.utils.TokenInformation;


public class LocalizedDependencyGraphsKernel implements KernelFunction<DependencyGraph>, Serializable {

	// gap penalty
	double m_lambda;
	
	SimpleGraphUtils util = new SimpleGraphUtils();
	private boolean m_bCache=true;
	static SparseDoubleAlgebra al;
	private boolean m_bNorm=true;
	
	static{
		al = new SparseDoubleAlgebra();
	}
	
	public double normKernel(DependencyGraph s1, DependencyGraph s2) 
	{
		double k1 = selfKernel(s1);
		double k2 = selfKernel(s2);
		
		double k = kernel(s1, s2);
		
		if (k == 0)
			return 0;
		
		// normalize
		return k / Math.sqrt (k1 * k2);				
	}
	
	public double selfKernel(DependencyGraph s)
	{
		return kernel(s, s);
	}
	
	public double kernel(DependencyGraph s1, DependencyGraph s2){
		int sizeG1 = s1.getNodes().length;
		int sizeG2 = s2.getNodes().length;
		int sizeProd = sizeG1*sizeG2;
		
		if(sizeProd==0){
			return 0;
		}
		
		
		DoubleMatrix2D IminusT = util.getProductAdjacencyMatrix(s1, s2);
		SimpleGraphNode<TokenInformation>[] nodes1 = s1.getNodes();
		SimpleGraphNode<TokenInformation>[] nodes2 = s2.getNodes();
		
		DenseDoubleMatrix1D start = new DenseDoubleMatrix1D(sizeProd);
		double[] beginProbG1 = s1.getInitialProbVector();
		double[] beginProbG2 = s2.getInitialProbVector();
		for(int i=0; i<sizeG1; i++){
			for(int j=0; j<sizeG2; j++){
				int index = i*sizeG2+j;
				start.setQuick(index, beginProbG1[i]*beginProbG2[j]*nodes1[i].getLabel().getSimilarity(nodes2[j].getLabel()));
			}
		}
		
		DenseDoubleMatrix1D end = new DenseDoubleMatrix1D(sizeProd);
		double[] endProbG1 = s1.getFinalProbVector();
		double[] endProbG2 = s2.getFinalProbVector();
		for(int i=0; i<sizeG1; i++){
			for(int j=0; j<sizeG2; j++){
				int index = i*sizeG2+j;
				end.setQuick(index, endProbG1[i]*endProbG2[j]);
			}
		}
		
		DoubleMatrix1D x = al.solve(IminusT, end);
		double d = x.zDotProduct(start);
		
		//System.out.println(d + " " + end.zDotProduct(start));

		/*if(d<0){
			System.out.println(x);
			System.out.println(b);
			System.out.println(IminusT);
		}*/
		//sumResults+=d/(sizeProd*sizeProd);
		//numResults++;
		return d;
		//return 0;
	}
	
	@Override
	public double evaluate(DependencyGraph s1, DependencyGraph s2) {
		double result;
		if(m_bNorm){
			result=normKernel(s1, s2);
		}else{
			result=kernel(s1, s2);
		}

		return result;
	}
}
