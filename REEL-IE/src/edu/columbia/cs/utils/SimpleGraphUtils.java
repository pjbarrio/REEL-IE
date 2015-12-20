package edu.columbia.cs.utils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;


import cern.colt.matrix.tdouble.DoubleFactory1D;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseCCDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseCCMDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseRCDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseRCMDoubleMatrix2D;


public class SimpleGraphUtils implements Serializable{
	
	HashMap<Integer,SparseDoubleMatrix2D> cacheIdentities = new HashMap<Integer,SparseDoubleMatrix2D>();
	
	
	public SparseRCDoubleMatrix2D getProductAdjacencyMatrix(DependencyGraph g1, DependencyGraph g2) {
		SimpleGraphNode<TokenInformation>[] nodes1 = g1.getNodes();
		SimpleGraphNode<TokenInformation>[] nodes2 = g2.getNodes();
		int n1Length = nodes1.length;
		int n2Length = nodes2.length;
		int numNodes = n1Length*n2Length;
		
		//SparseRCDoubleMatrix2D matrix = new SparseRCDoubleMatrix2D(numNodes,numNodes);
		SparseDoubleMatrix2D identity = cacheIdentities.get(numNodes);
		if(identity==null){
			identity=((SparseDoubleMatrix2D) DoubleFactory2D.sparse.identity(numNodes));
			cacheIdentities.put(numNodes, identity);
		}
		SparseRCDoubleMatrix2D matrix = identity.getRowCompressed(false); 
		/*for(int i=0; i<numNodes; i++){
			matrix.setQuick(i, i, 1);
		}*/
		
		List<Pair<Pair<Integer,Integer>,String>> e1 = g1.getEdges();
		List<Pair<Pair<Integer,Integer>,String>> e2 = g2.getEdges();
		
		for(Pair<Pair<Integer,Integer>,String> p1 : e1){
			for(Pair<Pair<Integer,Integer>,String> p2 : e2){
				int i1=p1.first().first();
				int i2=p1.first().second();
				int j1=p2.first().first();
				int j2=p2.first().second();
				SimpleGraphNode<TokenInformation> node12 = nodes1[i2];
				SimpleGraphNode<TokenInformation> node22 = nodes2[j2];
				String label1 = p1.second();
				String label2 = p2.second();
				
				TokenInformation info12 = node12.getLabel();
				TokenInformation info22 = node22.getLabel();
				
				boolean inShortestPathE1 = g1.isInShortestPath(i1, i2);
				boolean inShortestPathE2 = g2.isInShortestPath(j1, j2);
				
				int lin = i1*n2Length+j1;
				int col = i2*n2Length+j2;
				
				float sim =0;
				if(inShortestPathE1==inShortestPathE2){
					sim = (float)DependencyRelationComparison.computeDistanceBetweenLinks((String)label1, (String)label2);
				}
				if(sim!=0){
					sim*=(float)(info12.getSimilarity(info22))*g1.getTransitionProb(i1, i2)*g2.getTransitionProb(j1, j2);
				}
				
				if(sim!=0){
					if(lin==col){
						if(1-sim!=1){
							matrix.setQuick(lin, col, 1-sim);
						}
					}else{
						if(sim!=0){
							matrix.setQuick(lin, col, -sim);
						}
					}
				}
			}
		}
		
		return matrix;
	}
}
