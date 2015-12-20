package edu.columbia.cs.ref.model.re;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class Model represents a classification model that can be used for a specific
 * relationship extraction task.
 * 
 * <br>
 * <br>
 * 
 * The most important method of this class is the method predict label, which
 * receives an operable structure representing a sentence and returns the
 * predicted labels assigned by the classification model.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class Model implements Serializable{
	
	/**
	 * The Enumerator PredictionProperties establishes identifiers for several
	 * properties that may be captured during the prediction of the labels.
	 *
	 * @author      Pablo Barrio
	 * @author		Goncalo Simoes
	 * @version     0.1
	 * @since       2011-09-27
	 */
	public enum PredictionProperties {
		
		/** The confidence associated with the prediction */
		CONFIDENCE,
		
		/** The probability of the prediction representing a relationship */
		PROBABILITY_POSITIVE,
		
		/** The probability of the prediction not representing a relationship */
		PROBABILITY_NEGATIVE
	}
	
	/** The current prediction properties. */
	private Map<OperableStructure,Map<PredictionProperties,Object>> currentPredictionProperties =
		new HashMap<OperableStructure,Map<PredictionProperties,Object>>();
	
	/**
	 * The method that retrieves the labels associated to an operable structure
	 * according to the mode
	 *
	 * @param s the operable structure for which the label is retrieved
	 * @return the predicted labels
	 */
	protected abstract Set<String> getPredictedLabel(OperableStructure s);
	
	/**
	 * The method that retrieves the labels associated to an operable structure
	 * according to the model and stores the properties that are captured during
	 * the prediction of the labels.
	 *
	 * @param s the operable structure for which the label is retrieved
	 * @return the predicted labels
	 */
	public Set<String> predictLabel(OperableStructure s){
		Set<String> result = getPredictedLabel(s);
		PredictionProperties[] pprops = getAvailablePredictionProperties();
		
		for (int i = 0; i < pprops.length; i++) {
			PredictionProperties prop = pprops[i];
			savePredictionPropertyValue(s,prop,getPredictionPropertyValue(prop));
		}
		
		return result;
	}
	
	
	/**
	 * Returns the properties that were captured during the prediction of the labels
	 * for a given Operable Structure. 
	 *
	 * @param the operable structure for which the properties are retrieved
	 * @return the map containing all the properties for the input operable structure
	 */
	public Map<PredictionProperties,Object> obtainPredictionPropertyValue(OperableStructure s){
		Map<PredictionProperties,Object> result = currentPredictionProperties.get(s);
		if(result==null){
			predictLabel(s);
			return currentPredictionProperties.get(s);
		}
		return result;
	}
	
	/**
	 * Stores the properties that were captured during the prediction of the labels
	 * for a given Operable Structure. 
	 *
	 * @param os the operable structure for which the properties are retrieved
	 * @param predictionProperties the prediction property to be stored
	 * @param predictionPropertyValue the value of the prediction property to be stored
	 */
	protected void savePredictionPropertyValue(
			OperableStructure os,
			PredictionProperties predictionProperties,
			Object predictionPropertyValue) {
		Map<PredictionProperties,Object> props = currentPredictionProperties.get(os);
		if(props==null){
			props=new HashMap<PredictionProperties,Object>();
		}
		props.put(predictionProperties, predictionPropertyValue);
		currentPredictionProperties.put(os, props);
	}
	
	/**
	 * Returns the value of the prediction property for a given property
	 *
	 * @param predictionProperties the prediction properties
	 * @return the prediction property value that we want to retrieve
	 */
	protected abstract Object getPredictionPropertyValue(
			PredictionProperties predictionProperties);
	
	/**
	 * Returns an array containing all the available prediction properties for the model
	 *
	 * @return the available prediction properties
	 */
	protected abstract PredictionProperties[] getAvailablePredictionProperties();
	
	
	/**
	 * Returns the relationship types.
	 *
	 * @return the relationship types
	 */
	public abstract Set<RelationshipType> getRelationshipTypes();
	
	/**
	 * Returns the structure configuration.
	 *
	 * @return the structure configuration
	 */
	public abstract StructureConfiguration getStructureConfiguration();
}

