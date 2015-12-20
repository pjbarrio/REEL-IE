package edu.columbia.cs.ref.model.core.structure;

import weka.core.Instance;

/**
 * The Interface WekableStructure. Structures that implement this interface can
 * be processed by Weka.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface WekableStructure {
	
	/**
	 * Returns Instance representation of this object.
	 *
	 * @return Instance representation of this object
	 */
	public Instance getInstance();
}
