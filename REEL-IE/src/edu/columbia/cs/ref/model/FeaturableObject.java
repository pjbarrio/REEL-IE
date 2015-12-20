package edu.columbia.cs.ref.model;

import java.util.Hashtable;

import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.feature.FeatureSet;

/**
 * The Class FeaturableObject represents objects that can be enriched
 * with features.
 * 
 * <br>
 * <br>
 * 
 * Essentially, this class provides methods to store and retrieve features
 * of a given type on an object.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class FeaturableObject {

	/** The features table. */
	private Hashtable<Class<? extends FeatureGenerator>, FeatureSet> featuresTable;

	/**
	 * Instantiates a new featurable object.
	 */
	public FeaturableObject(){

		featuresTable = new Hashtable<Class<? extends FeatureGenerator>, FeatureSet>();

	}

	/**
	 * Retrieves the features that were produced by a given feature generator.
	 *
	 * @param <E> the type of feature to be retrieved
	 * @param featureGenerator the feature generator that produced the feature to be retrieved
	 * @return the feature produced by the input feature generator
	 */
	public <E extends FeatureSet> E getFeatures(FeatureGenerator<E> featureGenerator){
		return (E) featuresTable.get(featureGenerator.getClass());
	}

	/**
	 * Stores the features that were produced by a given feature generator.
	 *
	 * @param <E> the type of feature to be stored
	 * @param featureGenerator the feature generator that produced the feature to be stored
	 * @param fs the feature to be stored
	 */
	public <E extends FeatureSet> void setFeatures(FeatureGenerator<E> featureGenerator, E fs){
		featuresTable.put(featureGenerator.getClass(), fs);
	}

}
