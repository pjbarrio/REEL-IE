package edu.columbia.cs.ref.algorithm.evaluation.measure.impl;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.columbia.cs.ref.algorithm.evaluation.measure.Measure;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model.PredictionProperties;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class NumberOfExpectedPositiveAnswers counts the number of positive answers in
 * the golden labels of the testing data.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class NumberOfExpectedPositiveAnswers implements Measure {

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.evaluation.measure.Measure#getValue(java.util.Map, java.util.Map)
	 */
	@Override
	public double getValue(Map<OperableStructure, Set<String>> labels,
			Map<OperableStructure, Map<PredictionProperties, Object>> properties) {
		int result = 0;
		
		for(Entry<OperableStructure,Set<String>> entry : labels.entrySet()){
			Set<String> realLabel = entry.getKey().getLabels();
			result+=realLabel.size();
		}
		
		return result;
	}

}
