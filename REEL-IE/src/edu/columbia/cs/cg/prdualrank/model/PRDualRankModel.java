package edu.columbia.cs.cg.prdualrank.model;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.pattern.resources.Matchable;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

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
 * The model used to make PRDualRank part of the <b>Relationship Extraction Framework</b>. 
 * <br>
 * The model contains all the ranked <b>Search and Extraction Patterns</b>. Also includes additional information (ranking and values) of the tuples extracted during generation
 * of patterns. <br> With all of this information we can observe the Pattern - Recall Duality properties.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class PRDualRankModel<S extends Matchable,E extends Matchable, D extends Document> extends Model {

	private SortedSet<Pattern<S,D>> sPatterns;
	private SortedSet<Pattern<E,D>> ePatterns;
	private SortedSet<Relationship> sTuples;
	private SortedSet<Relationship> eTuples;

	/**
	 * Instantiates a new pR dual rank model.
	 *
	 * @param searchPatterns the search patterns generated during execution.
	 * @param extractionPatterns the extraction patterns generated during execution.
	 * @param searchTuples the search tuples extracted during the generation of search patterns.
	 * @param extractionTuples the extraction tuples extracted during the generation of extraction patterns.
	 */
	public PRDualRankModel(SortedSet<Pattern<S,D>> searchPatterns, SortedSet<Pattern<E,D>> extractionPatterns, SortedSet<Relationship> searchTuples, SortedSet<Relationship> extractionTuples) {
		
		this.sPatterns = searchPatterns;
		this.ePatterns = extractionPatterns;
		this.sTuples = searchTuples;
		this.eTuples = extractionTuples;
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.model.Model#getPredictedLabel(edu.columbia.cs.og.structure.OperableStructure)
	 */
	@Override
	protected Set<String> getPredictedLabel(OperableStructure s) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.model.Model#getPredictionPropertyValue(edu.columbia.cs.model.Model.PredictionProperties)
	 */
	@Override
	protected Object getPredictionPropertyValue(
			PredictionProperties predictionProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.model.Model#getAvailablePredictionProperties()
	 */
	@Override
	protected PredictionProperties[] getAvailablePredictionProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.model.Model#getRelationshipTypes()
	 */
	@Override
	public Set<RelationshipType> getRelationshipTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.model.Model#getStructureConfiguration()
	 */
	@Override
	public StructureConfiguration getStructureConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String ret = "Search Patterns: ";
		
		for (Pattern<S,D> pattern : this.sPatterns) {
			ret = ret + "\n" + pattern.toString();
		}

		ret = ret + "\nExtraction Patterns: ";
		
		for (Pattern<E,D> pattern : this.ePatterns) {
			ret = ret + "\n" + pattern.toString();
		}

		return ret;
	}
}
