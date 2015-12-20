package edu.columbia.cs.cg.prdualrank.inference;

import java.util.SortedSet;

import edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph;
import edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator;
import edu.columbia.cs.cg.prdualrank.inference.ranking.RankFunction;
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
 * Defines the inference procedure to rank patterns as described in PRDualRank paper.
 * 
 * <br>
 * For more information, please read the Inference formulas QuestP and QuestR in <b>Figure 4</b> of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class InferencePRDualRank<T extends Matchable,D extends Document> {

	private SortedSet<Relationship> rankedTuples;
	private SortedSet<Pattern<T,D>> rankedPatterns;

	/**
	 * Ranks the patterns and tuples in the graph based on the specified ranking function, using the passed questCalculator.
	 *
	 * @param gs the graph connecting tuples and patterns.
	 * @param patternRankFunction the pattern rank function used to rank patterns.
	 * @param tupleRankFunction the tuple rank function used to rank tuples.
	 * @param questCalculator the quest calculator used to calculate the required metrics (precision or recall in this case)
	 */
	public void rank(PRDualRankGraph<T,D> gs, RankFunction<Pattern<T,D>> patternRankFunction, RankFunction<Relationship> tupleRankFunction, QuestCalculator<T,D> questCalculator) {
	
		if (patternRankFunction.requiresPrecision() || tupleRankFunction.requiresPrecision()){
			questCalculator.runQuestP(gs);
			if (patternRankFunction.requiresPrecision()){
				patternRankFunction.setPrecision(questCalculator.getPatternPrecisionMap());
			}
			if (tupleRankFunction.requiresPrecision()){
				tupleRankFunction.setPrecision(questCalculator.getTuplePrecisionMap());
			}
		}
		
		if (patternRankFunction.requiresRecall() || tupleRankFunction.requiresRecall()){
			questCalculator.runQuestR(gs);
			if (patternRankFunction.requiresRecall()){
				patternRankFunction.setRecall(questCalculator.getPatternRecallMap());
			}
			if (tupleRankFunction.requiresRecall()){
				tupleRankFunction.setRecall(questCalculator.getTupleRecallMap());
			}
		}
		
		rankedPatterns = patternRankFunction.rank();
		
		rankedTuples = tupleRankFunction.rank();
		
	}

	/**
	 * Gets the ranked tuples.
	 *
	 * @return the ranked tuples according to the tuple ranking function.
	 */
	public SortedSet<Relationship> getRankedTuples() {
		return rankedTuples;
	}

	/**
	 * Gets the ranked patterns according to the pattern ranking function.
	 *
	 * @return the ranked patterns
	 */
	public SortedSet<Pattern<T,D>> getRankedPatterns() {
		return rankedPatterns;
	}

}
