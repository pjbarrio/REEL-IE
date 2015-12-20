package edu.columbia.cs.cg.prdualrank.graph.generator;

import java.util.Map;
import java.util.Set;

import edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.pattern.resources.Matchable;
import edu.columbia.cs.ref.model.relationship.Relationship;

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
 * Defines the behavior of a graph generator. 
 * <br>
 * Further implementations can be seen by looking at the subtypes of this class.
 * <br>
 * The difference in Graph Generators relies on the definition of frequency and matching. For instance, <b>Search Patterns</b> match <b>Documents</b> while <b>Extraction Patterns</b> match <b>Context of tuples</b>
 * <br>
 * <br>
 * For more information about Graphs, see <b>Section 4</b> of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public abstract class GraphGenerator<T extends Matchable,D extends Document> {

	/**
	 * Generates the graph given the top tuples and the patterns that match them.
	 *
	 * @param topTuples the top tuples
	 * @param patterns the patterns
	 * @param documents the documents processed during execution.
	 * @return the PrDualRank graph instance.
	 */
	public PRDualRankGraph<T,D> generateGraph(Set<Relationship> topTuples,
			Set<Pattern<T,D>> patterns, Set<D> documents) {
		
		PRDualRankGraph<T,D> ret = new PRDualRankGraph<T,D>();
		
		for (Pattern<T,D> pattern : patterns) {
			
			Map<Relationship, Integer> tuples = findTuples(documents,pattern);
			
			for (Relationship tuple : tuples.keySet()) {
								
				if (topTuples.contains(tuple)){
					
					ret.addContext(pattern,tuple,tuples.get(tuple));
				
				}

			}
			
		}
		
		return ret;
		
	}

	/**
	 * Generates a map containing the matching tuples and the frequency with which they co-exist in the documents.
	 *
	 * @param documents the documents to be analyzed.
	 * @param pattern the pattern to be used in the collection of documents.
	 * @return the map containing the matching tuples and their frequency in the collection.
	 */
	protected abstract Map<Relationship, Integer> findTuples(Set<D> documents,
			Pattern<T,D> pattern);
	
}
