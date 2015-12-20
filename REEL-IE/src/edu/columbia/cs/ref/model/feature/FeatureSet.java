package edu.columbia.cs.ref.model.feature;

import java.io.Serializable;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;

/**
 * The Class FeatureSet represents a feature that can be associated with a Featurable
 * Object.
 * 
 * <br>
 * <br>
 * 
 * A FeatureSet must implement the method enrichMe wich stores itself in an operable 
 * structure.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class FeatureSet implements Serializable{

	/**
	 * Stores this feature in an operable structure
	 *
	 * @param operableStructure the operable structure that must be enriched
	 */
	public abstract void enrichMe(OperableStructure operableStructure);
	
}
