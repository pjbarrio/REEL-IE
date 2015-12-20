package edu.columbia.cs.utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleGraph <T,U extends Serializable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6195178378668015263L;
	private int numNodes;
	private SimpleGraphNode<T>[] nodes;
	private int[][] A;
	private Object[][] X;
	private List<Pair<Pair<Integer,Integer>,U>> edges= new ArrayList<Pair<Pair<Integer,Integer>,U>>(); 
	private int numEdges=0;
	
	public SimpleGraph(int numNodes) {
		
		this.numNodes = numNodes;
		nodes = new SimpleGraphNode[numNodes];
		A = new int[numNodes][numNodes];
		X = new Object[numNodes][numNodes];
	}
	
	public int getNumEdges(){
		return numEdges;
	}
	
	public void addNode(int pos, T label) {
		nodes[pos] = new SimpleGraphNode<T>(label);
	}
	
	public T getNodeLabel(int pos){
		return nodes[pos].getLabel();
	}
	
	public SimpleGraphNode<T>[] getNodes() {
		return nodes;
	}
	
	public void addEdge(int origin, int destiny, U label) {
		A[origin][destiny]++;
		X[origin][destiny] = label;
		numEdges++;
		Pair<Integer,Integer> p = new Pair<Integer,Integer>(origin, destiny);
		Pair<Pair<Integer,Integer>,U> result = new Pair<Pair<Integer,Integer>,U>(p,label);
		edges.add(result);	
	}
	
	public List<Pair<Pair<Integer,Integer>,U>> getEdges(){
		return edges;
	}
	
	public boolean containsEdge(int origin, int destiny){
		return X[origin][destiny]!=null;
	}
	
	public Object getEdgeLabel(int origin, int destiny){
		return X[origin][destiny];
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(Pair<Pair<Integer,Integer>,U> p : edges){
			int n1 = p.first().first();
			int n2 = p.first().second();
			U label = p.second();
			buf.append(nodes[n1] + "-" + label + "->" + nodes[n2] + "\n");
		}
		
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof SimpleGraph){
			return Arrays.equals(nodes, ((SimpleGraph) o).nodes) && edges.equals(((SimpleGraph) o).edges);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(nodes);
	}
}
