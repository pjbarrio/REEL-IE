package edu.columbia.cs.ref.algorithm.feature.generation;

import java.io.Serializable;
import java.util.List;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;

/**
 * The Class FeatureGenerator represents any object that can produce features
 * on a given featurable object.
 * 
 * <br>
 * <br>
 * 
 * A feature generator is parameterized by the type of feature that it produces.
 *
 * @param <E> the type of feature that the generator produces.
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class FeatureGenerator<E extends FeatureSet> implements Serializable {
	
	/** The fgs. */
	private List<FeatureGenerator> fgs;
	
	/**
	 * Method to produce the features into an Operable structure
	 *
	 * @param s the operable structure to be enriched with the features
	 */
	public final void generateFeatures(OperableStructure s){
		
		E fs = s.getFeatures(this);
		
		if (fs == null){
			
			fs = getFeatures(s,this);
			
			if (fs == null){
				
				fs = process(s);
				
				setFeatures(s,this,fs);
				
			}

			s.setFeatures(this,fs);
			
		}
		
		
	}
	
	/**
	 * Sets the features properly according to the type of information that is used to produce it.
	 *
	 * @param <E> the type of feature that the generator produces.
	 * @param s the structure to be enriched with the features
	 * @param featureGenerator the feature generator used to produce the feature.
	 * @param fs the feature that is to be strored
	 */
	protected abstract <E extends FeatureSet> void setFeatures(OperableStructure s, FeatureGenerator<E> featureGenerator,
			E fs);

	/**
	 * Gets the features for a given feature generator that are stored in a given operable structure.
	 *
	 * @param <E> the type of feature that the generator produces.
	 * @param s the structure to be enriched with the features
	 * @param featureGenerator the feature generator used to produce the feature.
	 * @return the features to be retrieved
	 */
	protected abstract <E extends FeatureSet> E getFeatures(OperableStructure s, FeatureGenerator<E> featureGenerator);
	
	/*public Class<? extends FeatureGenerator> getKey() {
		return this.getClass();
	}*/
	/**
	 * Executes the required feature generators and extracts features for a given operable structure.
	 *
	 * @param s the operable structure for which the features will be produced
	 * @return the feature produced for the input operable structure
	 */
	protected E process(OperableStructure s) {
		int sizeList = getRequiredFeatureGenerators().size();
		for(int i=0; i<sizeList; i++){
			getRequiredFeatureGenerators().get(i).generateFeatures(s);
		}
		return extractFeatures(s);
	}
	
	/**
	 * Extract features for a given operable structure.
	 *
	 * @param s the operable structure for which the features will be produced
	 * @return the feature produced for the input operable structure
	 */
	protected abstract E extractFeatures(OperableStructure s);

	private List<FeatureGenerator> getRequiredFeatureGenerators() {
		
		if (fgs == null){
			fgs = retrieveRequiredFeatureGenerators();
		}
		return fgs;
	}

	/**
	 * Retrieve the feature generators that this feature generator is dependent on.
	 *
	 * @return the list of feature generators that this feature generator is dependent on.
	 */
	protected abstract List<FeatureGenerator> retrieveRequiredFeatureGenerators();
}
