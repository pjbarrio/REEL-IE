package edu.columbia.cs.cg.prdualrank.inference.quest;

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
 * Defines the behavior of the QUEST functions defined in PRDualRank.
 * 
 * <br>
 * For more information, please read the Inference formulas in <b>Figure 4</b> of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public interface QuestCalculator<T extends Matchable,D extends Document> {

	/**
	 * Run QuestP (precision) as stated in PRDualRank paper.
	 *
	 * @param gs the graph containing related elements (patterns and tuples, for instance).
	 */
	void runQuestP(PRDualRankGraph<T,D> gs);

	/**
	 * Run QuestR (recall) as stated in PRDualRank paper.
	 *
	 * @param gs the graph containing related elements (patterns and tuples, for instance).
	 */
	void runQuestR(PRDualRankGraph<T,D> gs);

	/**
	 * Gets the Pattern-Precision map (containing the calculated precisions by QuestP)
	 *
	 * @return the Pattern-Precision map
	 */
	Map<Pattern<T,D>,Double> getPatternPrecisionMap();

	/**
	 * Gets the Tuple-Precision map (containing the calculated precisions by QuestP).
	 *
	 * @return the Tuple-Precision map
	 */
	Map<Relationship,Double> getTuplePrecisionMap();

	/**
	 * Gets the Pattern-Recall map (containing the calculated recalls by QuestR).
	 *
	 * @return the Pattern-Recall map
	 */
	Map<Pattern<T,D>,Double> getPatternRecallMap();

	/**
	 * Gets the Tuple-Recall map (containing the calculated recalls by QuestR).
	 *
	 * @return the Tuple-Recall map
	 */
	Map<Relationship,Double> getTupleRecallMap();

	/**
	 * Sets the initial seeds.
	 *
	 * @param seeds the initial seeds
	 */
	void setSeeds(Set<Relationship> seeds);

}
