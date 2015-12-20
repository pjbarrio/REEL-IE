package edu.columbia.cs.ref.model.feature.impl;

import java.io.Serializable;
import java.util.Arrays;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;
import edu.columbia.cs.utils.SimpleGraph;

/**
 * The Class GraphFS is an implementation of a FeatureSet for which
 * the features are represented in the form of a graph.
 *
 * @param <N> the type of the nodes of the graph
 * @param <L> the type of the edges of the graph
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class GraphFS<N extends Serializable, L extends Serializable> extends FeatureSet {
	
	/** The graph. */
	private SimpleGraph<N,L> graph;
	
	/**
	 * Instantiates a new GraphFS given an input graph containing the structured
	 * information of this feature.
	 *
	 * @param graph the input graph
	 */
	public GraphFS(SimpleGraph<N,L> graph){
		this.graph=graph;
	}
	
	/**
	 * Returns the graph representation of the features.
	 *
	 * @return the graph representation of the features
	 */
	public SimpleGraph<N,L> getGraph(){
		return graph;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return graph.toString();
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.feature.FeatureSet#enrichMe(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	public void enrichMe(OperableStructure operableStructure) {
		operableStructure.add(this);
	}
}
