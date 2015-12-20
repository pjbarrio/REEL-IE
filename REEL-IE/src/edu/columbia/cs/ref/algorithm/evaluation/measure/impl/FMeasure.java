package edu.columbia.cs.ref.algorithm.evaluation.measure.impl;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.columbia.cs.ref.algorithm.evaluation.measure.Measure;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model.PredictionProperties;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class FMeasure represents the F-Measure. The f-measure corresponds
 * to a weighted harmonic mean between recall and precision. The weight is
 * controled by a parameter β.
 * 
 * <br>
 * <br>
 * 
 * The equation for the F-Measure with parameter β is:
 * 
 * <br>
 * <br>
 * 
 * <img src="fmeasure.png"/>
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class FMeasure implements Measure {
	
	/** The recall. */
	private Measure recall = new Recall();
	
	/** The precision. */
	private Measure precision = new Precision();
	
	/** The beta. */
	private double beta;
	
	/**
	 * Instantiates a new f measure with a given parameter β
	 *
	 * @param beta the β parameter
	 */
	public FMeasure(double beta){
		this.beta=beta;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.evaluation.measure.Measure#getValue(java.util.Map, java.util.Map)
	 */
	@Override
	public double getValue(Map<OperableStructure, Set<String>> labels,
			Map<OperableStructure, Map<PredictionProperties, Object>> properties) {
		double recall = this.recall.getValue(labels, properties);
		double precision = this.precision.getValue(labels, properties);
		
		double normalization = (1+beta*beta);
		double result = (recall*precision)/((beta*beta*precision)+recall);
		return normalization*result;
	}

}
