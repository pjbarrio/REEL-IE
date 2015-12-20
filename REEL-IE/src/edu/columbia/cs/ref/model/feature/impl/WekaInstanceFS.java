package edu.columbia.cs.ref.model.feature.impl;

import weka.core.Instance;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;

/**
 * The Class WekaInstanceFS is an implementation of a FeatureSet for which
 * the features are represented in the form of a Weka <code>Instance</code>.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class WekaInstanceFS extends FeatureSet {

	/** The instance. */
	private Instance instance;

	/**
	 * Instantiates a new WekaInstanceFS given an input Weka <code>Instance</code>
	 *
	 * @param instance the input Weka <code>Instance</code>
	 */
	public WekaInstanceFS(Instance instance) {
		this.instance = instance;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.feature.FeatureSet#enrichMe(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	public void enrichMe(OperableStructure operableStructure) {
		operableStructure.add(this);
	}

	/**
	 * Returns the content of this feature set in the form of a Weka <code>Instance</code>
	 *
	 * @return the content of this feature set in the form of a Weka <code>Instance</code>
	 */
	public Instance getInstance(){
		return instance;
	}
}
