/**
 * The graph connecting patterns and tuples as defined in PRDualRank paper.
 * <br>
 * This graph associates a Matchable Object with a Document object storing the frequencies with which they occur in the data
 * 
 * <br>
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * <br>
 * For further information, 
 * 
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */
package edu.columbia.cs.cg.prdualrank.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
 * The graph connecting patterns and tuples as defined in PRDualRank paper.
 * <br>
 * This graph associates a Matchable Object with a Document object storing the frequencies with which they occur in the data.
 * 
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

public class PRDualRankGraph<T extends Matchable,D extends Document> {

	Map<Pattern<T,D>,Integer> patternFrequency;
	Map<Relationship, Integer> tupleFrequency;
	Map<Pattern<T,D>,Map<Relationship,Integer>> patternTupleFrequency;
	Map<Relationship,Map<Pattern<T,D>, Integer>> tuplePatternFrequency;
	
	/**
	 * Instantiates a new PRDualRank graph.
	 */
	public PRDualRankGraph(){
		patternFrequency = new HashMap<Pattern<T,D>, Integer>();
		tupleFrequency = new HashMap<Relationship, Integer>();
		patternTupleFrequency = new HashMap<Pattern<T,D>, Map<Relationship,Integer>>();
		tuplePatternFrequency = new HashMap<Relationship, Map<Pattern<T,D>,Integer>>();
	}
	
	
	/**
	 * Adds a new connection between a pattern and a tuple. Also includes the frequency with which this connection (called 'context'
	 * in the paper) appears in the collection being processed.
	 *
	 * @param pattern the pattern to be added in the graph.
	 * @param tuple the tuple to be connected to the pattern.
	 * @param frequency the frequency with which this context appears in the 'snippets'
	 */
	public void addContext(Pattern<T,D> pattern, Relationship tuple, int frequency) {
		
		updateFrequency(patternFrequency,pattern,frequency);
		updateFrequency(tupleFrequency,tuple,frequency);
		updateContext(patternTupleFrequency,pattern,tuple,frequency);
		updateContext(tuplePatternFrequency,tuple,pattern,frequency);
		
	}


	private <I,O> void updateContext(
			Map<I, Map<O, Integer>> contextTable,
			I index, O object, int frequency) {
		
		Map<O,Integer> objectFreq = contextTable.get(index);
		
		if (objectFreq == null){
			objectFreq = new HashMap<O, Integer>();
		}
		
		updateFrequency(objectFreq, object, frequency);
		
		contextTable.put(index, objectFreq);
	}


	private <F> void updateFrequency(Map<F, Integer> freqTable,
			F element, int frequency) {
		
		Integer freq = freqTable.get(element);
		
		if (freq == null){
			freq = 0;
		}
		
		freqTable.put(element, freq + frequency);
	}


	/**
	 * Gets the patterns from the graph.
	 *
	 * @return the patterns
	 */
	public Set<Pattern<T,D>> getPatterns() {
		return patternFrequency.keySet();
	}

	/**
	 * Gets the tuples from the graph.
	 *
	 * @return the tuples
	 */
	public Set<Relationship> getTuples() {
		return tupleFrequency.keySet();
	}


	/**
	 * Gets the matching frequency between a pattern and a tuple.
	 *
	 * @param pattern the pattern
	 * @param tuple the tuple
	 * @return the frequency which which this pair appears in the graph.
	 */
	public int getMatchingFrequency(Pattern<T,D> pattern, Relationship tuple) {
		
		return patternTupleFrequency.get(pattern).get(tuple);
		
	}


	/**
	 * Gets the matching tuples given a pattern
	 *
	 * @param pattern the pattern
	 * @return the matching tuples
	 */
	public Set<Relationship> getMatchingTuples(Pattern<T,D> pattern) {
		return patternTupleFrequency.get(pattern).keySet();
	}


	/**
	 * Gets the total frequency of a pattern in the graph. It is the sum of all the individual frequencies with
	 * all the matching tuples.
	 *
	 * @param pattern the pattern
	 * @return the freqency with which the pattern appears in the collection.
	 */
	public double getFreqency(Pattern<T,D> pattern) {
		return patternFrequency.get(pattern);
	}


	/**
	 * Gets the total frequency of a tuple. It includes all the patterns that match this tuple.
	 *
	 * @param tuple the tuple
	 * @return the frequency in the graph.
	 */
	public int getFrequency(Relationship tuple) {
		return tupleFrequency.get(tuple);
	}


	/**
	 * Gets the matching frequency between a tuple and a pattern. Should return the same value than 
	 * getMatchingFrequency(pattern,tuple) if the graph is consistent.
	 *
	 * @param tuple the tuple
	 * @param pattern the pattern
	 * @return the matching frequency with which the tuple and the pattern appear in the collection (represented in the graph)
	 */
	public int getMatchingFrequency(Relationship tuple, Pattern<T,D> pattern) {
		return tuplePatternFrequency.get(tuple).get(pattern);
	}


	/**
	 * Gets the matching patterns of a given tuple. The meaning of 'Match' depends on the patterns. For instance, for search patterns,
	 * 'to match' means to hit the document containing the tuple. However for extraction patterns, it means to match the context of
	 * the tuple in some document.
	 *
	 * @param tuple the tuple
	 * @return the patterns that 'match' the tuple. 
	 */
	public Set<Pattern<T,D>> getMatchingPatterns(Relationship tuple) {
		
		return tuplePatternFrequency.get(tuple).keySet();
		
	}
	
}
