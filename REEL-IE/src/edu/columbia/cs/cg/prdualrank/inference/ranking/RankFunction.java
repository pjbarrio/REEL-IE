package edu.columbia.cs.cg.prdualrank.inference.ranking;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.columbia.cs.ref.algorithm.evaluation.measure.impl.FMeasure;

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
 * Abstract class to provide the behavior of different ranking functions that are based on precision and recall. 
 * <br>
 * Classes that extend this one have to overwrite <b>requiresPrecision</b> and/or <b>requiredRecall</b> accordingly to the measure. For instance, <b> {@link FMeasure} </b> 
 * implements both, because it requires both. <br>
 * For more information about ranking, see <b>Section 6</b> of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @see FMeasure
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public abstract class RankFunction <T> {

	private class ObjectAfterRankComparator<T> implements Comparator<T>{

		private Comparator<? super T> comparator;

		public ObjectAfterRankComparator(Comparator<? super T> comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(T o1, T o2) {
			
			int comp = comparator.compare(o1, o2);
			
			if (comp == 0){
				return -1;
			}
			
			return 1;
		}
		
	}
	
	protected Map<T, Double> precisionMap;
	protected Map<T, Double> recallMap;
	private Set<T> elements;

	/**
	 * The ranking function has to inform if precision is required in its ranking function.
	 *
	 * @return if the ranking function is based in precision
	 */
	public boolean requiresPrecision() {
		return false;
	}

	/**
	 * The ranking function has to inform if recall is required in its ranking function.
	 *
	 * @return if the ranking function is based in recall
	 */
	public boolean requiresRecall() {
		return false;
	}

	/**
	 * sets the precision map used to generate the ranking.
	 *
	 * @param precisionMap the map with all the stored precisions.
	 */
	public void setPrecision(Map<T, Double> precisionMap) {

		this.precisionMap = precisionMap;
		this.elements = precisionMap.keySet();
	}

	/**
	 * sets the recall map used to generate the ranking.
	 *
	 * @param recallMap the map with all the stored recall.
	 */
	public void setRecall(Map<T, Double> recallMap) {

		this.recallMap = recallMap;
		this.elements = recallMap.keySet();
	}

	/**
	 * Generates the ranking of the stored objects.
	 *
	 * @return the objects sorted according to the rank function.
	 */
	public SortedSet<T> rank() {
		
		SortedSet<T> ret = new TreeSet<T>(new ObjectAfterRankComparator<T>(getComparator()));
		
		for (T element : elements) {
			
			ret.add(element);
			
		}

		return ret;

	}

	/**
	 * Gets the comparator object used in the ranking function.
	 *
	 * @return the comparator to rank T objects 
	 */
	protected abstract Comparator<? super T> getComparator();

}
