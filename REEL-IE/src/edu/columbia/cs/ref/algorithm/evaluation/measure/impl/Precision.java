package edu.columbia.cs.ref.algorithm.evaluation.measure.impl;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.columbia.cs.ref.algorithm.evaluation.measure.Measure;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model.PredictionProperties;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class Precision computes precision. Precision is the ratio between
 * the number of true positives and the number of positives in the gold data.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Precision implements Measure {
	
	/** The true positives. */
	private Measure truePositives = new NumberOfTruePositives();
	
	/** The number positives. */
	private Measure numberPositives = new NumberOfPositiveAnswers();

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.evaluation.measure.Measure#getValue(java.util.Map, java.util.Map)
	 */
	@Override
	public double getValue(Map<OperableStructure, Set<String>> labels,
			Map<OperableStructure, Map<PredictionProperties, Object>> properties) {
		return truePositives.getValue(labels, properties)/numberPositives.getValue(labels, properties);
	}

}
