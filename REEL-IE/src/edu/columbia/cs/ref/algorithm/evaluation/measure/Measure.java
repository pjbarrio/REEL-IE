package edu.columbia.cs.ref.algorithm.evaluation.measure;

import java.util.Map;
import java.util.Set;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model.PredictionProperties;

/**
 * The Interface Measure represents an evaluation metric for relationship extraction.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface Measure {
	
	/**
	 * Obtains the value of the metric
	 *
	 * @param labels map that associates a label to each operable structure
	 * @param properties map that stores the properties of the prediction
	 * @return the value of the metric
	 */
	public double getValue(Map<OperableStructure, Set<String>> labels,
			Map<OperableStructure,Map<PredictionProperties,Object>> properties);
}
