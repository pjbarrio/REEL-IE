package edu.columbia.cs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DependencyGraph extends SimpleGraph<TokenInformation,String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1429362502044671798L;
	private double[] nodesInitialProb;
	private double[][] edgesProb;
	private boolean[][] inShortestPath;
	private double[] nodesFinalProb;
	private int numNodes;
	private int entityN1=-1;
	private int entityN2=-1;
	private List<Pair<Pair<Integer,Integer>,String>> shortestPathEdges = new ArrayList<Pair<Pair<Integer,Integer>,String>>();

	private double w;
	
	public DependencyGraph(int numNodes, double w) {
		super(numNodes);
		this.w=w;
		this.numNodes=numNodes;
		nodesInitialProb=new double[numNodes];
		edgesProb=new double[numNodes][numNodes];
		inShortestPath=new boolean[numNodes][numNodes];
		nodesFinalProb=new double[numNodes];
		for(int i=0; i<numNodes; i++){
			nodesInitialProb[i]=1.0/numNodes;
			nodesFinalProb[i]=1.0;
		}
	}
	
	@Override
	public void addNode(int pos, TokenInformation label){
		super.addNode(pos, label);
		if(label.isEntity1()){
			entityN1=pos;
		}else if(label.isEntity2()){
			entityN2=pos;
		}
	}

	@Override
	public void addEdge(int origin, int destiny, String label) {
		super.addEdge(origin, destiny, label);
		int numNonZeroEdges=0;
		for(int j=0; j<numNodes; j++){
			if(containsEdge(origin, j)){
				numNonZeroEdges++;
			}
		}
		double newProbValue=1.0/numNonZeroEdges;
		for(int j=0; j<numNodes; j++){
			if(containsEdge(origin, j)){
				edgesProb[origin][j]=newProbValue;
			}
		}
		nodesFinalProb[origin]=newProbValue;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		SimpleGraphNode<TokenInformation>[] nodes = getNodes();
		buf.append("(");
		for(int i=0; i<numNodes;i++){
			if(i==0){
				buf.append(nodesInitialProb[i]);
			}else{
				buf.append(","+nodesInitialProb[i]);
			}
		}
		buf.append(")\n");
		for(Pair<Pair<Integer,Integer>,String> p : getEdges()){
			int n1 = p.first().first();
			int n2 = p.first().second();
			String label = p.second();
			buf.append("(" + n1 + "," + n2+ ")" +nodes[n1] + "-" + label + "->" + nodes[n2] + " (" + edgesProb[n1][n2] + " - " + inShortestPath[n1][n2] + ")\n");
		}
		buf.append("(");
		for(int i=0; i<numNodes;i++){
			if(i==0){
				buf.append(nodesFinalProb[i]);
			}else{
				buf.append(","+nodesFinalProb[i]);
			}
		}
		buf.append(")\n");
		buf.append("Prots: (" + entityN1 + "," + entityN2 +")\n");
		buf.append("Shortest path edges: " + shortestPathEdges + "\n");
		
		return buf.toString();
	}
	
	public double[] getInitialProbVector(){
		return nodesInitialProb;
	}
	
	public double[] getFinalProbVector(){
		return nodesFinalProb;
	}
	
	public double getTransitionProb(int origin, int dest){
		return edgesProb[origin][dest];
	}
	
	public boolean isInShortestPath(int origin, int dest){
		return inShortestPath[origin][dest];
	}
	
	public void normalizePaths(){
		Vertex prot1=null;
		Vertex prot2=null;
		
		List<Vertex> v = new ArrayList<Vertex>();
		for(int k=0; k<numNodes; k++){
			Vertex newV = new Vertex(k,new String[0]);
			if(k==entityN1){
				prot1=newV;
			}else if(k==entityN2){
				prot2=newV;
			}

			v.add(newV);
		}
		
		for(Vertex origin : v){
			int originId = origin.id;
			for(int destinyId=0; destinyId<numNodes; destinyId++){
				if(containsEdge(originId,destinyId)){
					Vertex destiny = v.get(destinyId);
					origin.addEdge(new Edge(origin,destiny,1,true));
					destiny.addEdge(new Edge(destiny,origin,1,false));
				}
			}
		}
		
		Dijkstra.computePaths(prot1);
		List<Edge> path = Dijkstra.getShortestPathTo(prot2);
		Map<Integer,List<Integer>> edgesFromOrigin = new HashMap<Integer,List<Integer>>();
		for(Edge e : path){
			int origin;
			int destiny;
			if(e.sign==true){
				origin = e.origin.id;
				destiny = e.target.id;
			}else{
				origin = e.target.id;
				destiny = e.origin.id;
			}
			
			SimpleGraphNode<TokenInformation>[] nodes = this.getNodes();
			nodes[origin].getLabel().setInShortestPath(true);
			nodes[destiny].getLabel().setInShortestPath(true);
			inShortestPath[origin][destiny]=true;
			
			Pair<Integer,Integer> edgeIndexes = new Pair<Integer,Integer>(origin,destiny);
			Pair<Pair<Integer,Integer>,String> result = new Pair<Pair<Integer,Integer>,String>(edgeIndexes,(String) getEdgeLabel(origin, destiny));
			shortestPathEdges.add(result);
			List<Integer> list = edgesFromOrigin.get(origin);
			if(list==null){
				list = new ArrayList<Integer>();
			}
			list.add(destiny);
			edgesFromOrigin.put(origin, list);
		}
		
		for(int origin : edgesFromOrigin.keySet()){
			normalizeForOrigin(origin,edgesFromOrigin.get(origin));
		}
		
		//normalizeStartValues();
	}

	private void normalizeStartValues() {
		double sumProbs = 0;
		for(int origin=0; origin<numNodes; origin++){
			double prob = nodesInitialProb[origin];
			if(origin==entityN1 || origin==entityN2){
				prob=prob/w;
			}else{
				prob=prob*w;
			}
			sumProbs+=prob;
			nodesInitialProb[origin]=prob;
		}
		for(int origin=0; origin<numNodes; origin++){
			nodesInitialProb[origin]=nodesInitialProb[origin]/sumProbs;
		}
	}

	private void normalizeForOrigin(int origin, List<Integer> list) {
		double sumProbs = 0;
		for(int i=0; i<numNodes; i++){
			double prob = edgesProb[origin][i];
			if(list.contains(i)){
				prob=prob/w;
			}else{
				prob=prob*w;
			}
			sumProbs+=prob;
			edgesProb[origin][i]=prob;
		}
		/*if(origin==entityN1 || origin==entityN2){
			nodesFinalProb[origin]=nodesFinalProb[origin]/w;
		}else{
			nodesFinalProb[origin]=nodesFinalProb[origin]*w;
		}
		sumProbs+=nodesFinalProb[origin];*/
		
		for(int i=0; i<numNodes; i++){
			edgesProb[origin][i]=edgesProb[origin][i]/sumProbs;
		}
		//nodesFinalProb[origin]=nodesFinalProb[origin]/sumProbs;
	}
	
	public List<Pair<Pair<Integer, Integer>, String>> getShortestPathEdges() {
		return shortestPathEdges;
	}
	
	public DependencyGraph getShortestPathDependencyGraph(){
		int numNodes = shortestPathEdges.size()+1;
		
		if(numNodes==1){
			return new DependencyGraph(0, 1.0);
		}
			
		
		HashMap<Integer,Integer> translatorTable = new HashMap<Integer,Integer>();
		DependencyGraph newGraph = new DependencyGraph(numNodes, 1.0);
		for(Pair<Pair<Integer, Integer>, String> edge : shortestPathEdges){
			int origin=edge.first().first();
			int destiny=edge.first().second();
			Integer originNew = translatorTable.get(origin);
			if(originNew==null){
				originNew=translatorTable.size();
				translatorTable.put(origin, originNew);
				newGraph.addNode(originNew, getNodes()[origin].getLabel().copyNoShortestPath());
			}
			Integer destinyNew = translatorTable.get(destiny);
			if(destinyNew==null){
				destinyNew=translatorTable.size();
				translatorTable.put(destiny, destinyNew);
				newGraph.addNode(destinyNew, getNodes()[destiny].getLabel().copyNoShortestPath());
			}
			newGraph.addEdge(originNew, destinyNew, edge.second());
		}
		
		//newGraph.normalizePaths();
		
		return newGraph;
	}
	
	
	public DependencyGraph getNotShortestPathDependencyGraph(){
		DependencyGraph newGraph = new DependencyGraph(numNodes, 1.0);
		
		SimpleGraphNode<TokenInformation>[] node = getNodes();
		int nodesLength=node.length;
		
		for(int i=0; i<nodesLength; i++){
			newGraph.addNode(i, node[i].getLabel().copyNoShortestPath());
		}
		
		for(Pair<Pair<Integer,Integer>,String> edge : this.getEdges()){
			int origin=edge.first().first();
			int destiny=edge.first().second();
			newGraph.addEdge(origin, destiny, edge.second());
		}
		
		//newGraph.normalizePaths();
		
		return newGraph;
	}
}
