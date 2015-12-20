package edu.columbia.cs.cg.prdualrank.inference.ranking.impl;

import java.util.Comparator;

import edu.columbia.cs.cg.prdualrank.inference.ranking.RankFunction;

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
 * Ranks objects according to their recall. Recall values are already calculated and provided. For more information about ranking, see <b>Section 6</b> of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class RecallBasedRankFunction<T> extends RankFunction<T> {

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.ranking.RankFunction#getComparator()
	 */
	@Override
	protected Comparator<T> getComparator() {
		return new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return recallMap.get(o2).compareTo(recallMap.get(o1));
			}
		};
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.inference.ranking.RankFunction#requiresRecall()
	 */
	@Override
	public boolean requiresRecall() {
		return true;
	}
}
