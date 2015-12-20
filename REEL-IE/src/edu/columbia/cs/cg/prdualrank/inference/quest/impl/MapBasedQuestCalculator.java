/**
 * 
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
package edu.columbia.cs.cg.prdualrank.inference.quest.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
 * QuestCalculator based on Maps. This implementation matched the one described in the PRDualRank paper. Performs well for sparse graphs. For fully-connected graphs see {@link MatricesBasedQuestCalculator}
 * 
 * <br>
 * For more information, please read the Inference formulas in <b>Figure 4</b> of the mentioned paper.
 *
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>
 * @see MatricesBasedQuestCalculator 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class MapBasedQuestCalculator<T extends Matchable,D extends Document> implements QuestCalculator<T,D> {
	private Map<Pattern<T,D>,Pair<Double,Double>> patternTable;
	private Map<Relationship,Pair<Double,Double>> tupleTable;

	private Map<Pattern<T,D>,Double> patternPrecision = null;
	private Map<Pattern<T,D>,Double> patternRecall = null;
	private Map<Relationship,Double> tuplePrecision = null;
	private Map<Relationship,Double> tupleRecall = null;
	
	private ConvergenceFinder convergence;
	private Set<Relationship> seeds;

	/**
	 * Instantiates a new map based quest calculator.
	 *
	 * @param convergence the convergence method used to stop the execution.
	 */
	public MapBasedQuestCalculator(ConvergenceFinder convergence) {
		
		this.convergence = convergence;
		patternTable = new HashMap<Pattern<T,D>, Pair<Double,Double>>();
		tupleTable = new HashMap<Relationship, Pair<Double,Double>>();
	}

	private void initializeSeedRecall() {
		double numSeeds=seeds.size();
		for(Relationship r : seeds){
			setRecall(r,1.0/numSeeds,tupleTable);
		}
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#runQuestP(edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph)
	 */
	@Override
	public void runQuestP(PRDualRankGraph<T,D> gs) {
		convergence.reset();
		
		while(!convergence.converged()){
			
			for (Pattern<T,D> pattern : gs.getPatterns()) {
				
				double precision = calculatePrecision(pattern,gs);
				setPrecision(pattern,precision,patternTable);
				
			}
			
			for (Relationship tuple: gs.getTuples()){
				
				double precision = calculatePrecision(tuple,gs);
				setPrecision(tuple,precision,tupleTable);
				
			}
			
		}
		generatePatternMaps();
		generateTupleMaps();
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#runQuestR(edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph)
	 */
	@Override
	public void runQuestR(PRDualRankGraph<T,D> gs) {
		convergence.reset();
		
		while(!convergence.converged()){		
			for (Pattern<T,D> pattern : gs.getPatterns()) {
				
				double recall = calculateRecall(pattern, gs);
				setRecall(pattern,recall,patternTable);
				
			}
			
			for (Relationship tuple: gs.getTuples()){
				
				double recall = calculateRecall(tuple,gs);
				setRecall(tuple,recall,tupleTable);
			}
			
		}
		generatePatternMaps();
		generateTupleMaps();
	}

	private double calculatePrecision(Relationship tuple,PRDualRankGraph<T,D> gs) {
		
		if (seeds.contains(tuple))
			return getPrecision(tuple);
		
		double precision = 0.0;
		
		for (Pattern<T,D> pattern : gs.getMatchingPatterns(tuple)) {
			
			precision += getPrecision(pattern)*(double)gs.getMatchingFrequency(tuple, pattern)/(double)gs.getFrequency(tuple);
			
		}
		
		return precision;
		
	}

	private double getPrecision(Pattern<T,D> pattern) {
		return patternTable.get(pattern).first();
	}

	private <E> void setPrecision(E pattern, double precision, Map<E,Pair<Double,Double>> table) {
		Pair<Double, Double> value = table.get(pattern);
		
		double recall = 0.0;
		
		if (value != null)
			recall = value.second();
			
		table.put(pattern, new Pair<Double,Double>(precision,recall));
		
	}

	private double calculatePrecision(Pattern<T,D> pattern, PRDualRankGraph<T,D> gs) {
		
		double precision = 0.0;
		
		for (Relationship tuple : gs.getMatchingTuples(pattern)) {
			
			precision += getPrecision(tuple)*(double)gs.getMatchingFrequency(pattern,tuple)/(double)gs.getFreqency(pattern);
			
		}
		
		return precision;
	}

	private double getPrecision(Relationship tuple) {
		
		if (seeds.contains(tuple)){
			return P0(tuple);
		}
		
		return getPrecision(tuple,tupleTable);
		
	}

	private double P0(Relationship tuple) {
		return 1.0;
	}

	private <E> double getPrecision(E key,
			Map<Relationship, Pair<Double, Double>> table) {
		
		Pair<Double,Double> pair =  table.get(key);
		
		if (pair == null){
			return 0.0;
		}
		
		return pair.first();
	}

	private double calculateRecall(Relationship tuple,PRDualRankGraph<T,D> gs) {
		
		double recall = 0.0;
		
		for (Pattern<T,D> pattern : gs.getMatchingPatterns(tuple)) {
			
			recall += getRecall(pattern,patternTable)*(double)gs.getMatchingFrequency(tuple, pattern)/(double)gs.getFreqency(pattern);
			
		}
		
		return recall;
	}

	private <E> double getRecall(E key, Map<E,Pair<Double,Double>> table) {
		Pair<Double,Double> pair = table.get(key);
		
		if (pair == null){
			return 0;
		}

		return pair.second();
	}

	private <E> void setRecall(E key, double recall, Map<E,Pair<Double,Double>> table) {
		
		Pair<Double,Double> value = table.get(key);
		
		double precision = 0.0;
		
		if (value != null)	
			precision = value.first();
			
		table.put(key, new Pair<Double,Double>(precision,recall));
		
	}

	private double calculateRecall(Pattern<T,D> pattern, PRDualRankGraph<T,D> gs) {
		
		double recall = 0.0;
		
		for (Relationship tuple : gs.getMatchingTuples(pattern)) {

			recall += getRecall(tuple,tupleTable)*(double)gs.getMatchingFrequency(pattern, tuple)/(double)gs.getFrequency(tuple);
			
		}
		
		return recall;
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getPatternPrecisionMap()
	 */
	@Override
	public Map<Pattern<T,D>, Double> getPatternPrecisionMap() {
		return patternPrecision;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getTuplePrecisionMap()
	 */
	@Override
	public Map<Relationship, Double> getTuplePrecisionMap() {
		return tuplePrecision;
	}

	private void generateTupleMaps() {
		
		tuplePrecision = new HashMap<Relationship, Double>();
		tupleRecall = new HashMap<Relationship, Double>();
		loadMap(tupleTable,tuplePrecision,tupleRecall);
	}

	private <E> void loadMap(Map<E, Pair<Double, Double>> table,
			Map<E, Double> precision,
			Map<E, Double> recall) {

		for (Entry<E, Pair<Double, Double>> entry : table.entrySet()) {
			precision.put(entry.getKey(), entry.getValue().first());
			recall.put(entry.getKey(), entry.getValue().second());
			
		}

		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getPatternRecallMap()
	 */
	@Override
	public Map<Pattern<T,D>, Double> getPatternRecallMap() {
		return patternRecall;
	}

	private void generatePatternMaps() {
		
		patternPrecision = new HashMap<Pattern<T,D>, Double>();
		patternRecall = new HashMap<Pattern<T,D>, Double>();
		
		loadMap(patternTable, patternPrecision, patternRecall);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#getTupleRecallMap()
	 */
	@Override
	public Map<Relationship, Double> getTupleRecallMap() {
		return tupleRecall;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator#setSeeds(java.util.Set)
	 */
	@Override
	public void setSeeds(Set<Relationship> seeds) {
		this.seeds = seeds;
		initializeSeedRecall();
	}
	
}
