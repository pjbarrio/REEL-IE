package edu.columbia.cs.ref.engine;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Interface Engine forces the implementation of methods needed for
 * a given relationship extraction engine.
 * 
 * <br>
 * <br>
 * 
 * The most important method is the method train which, given a list of
 * labeled operable structures that corresponds to the training data, produces
 * a model for relationship extraction.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface Engine {
	
	/**
	 * Given a list of
	 * labeled operable structures that corresponds to the training data, produces
	 * a model for relationship extraction.
	 *
	 * @param list the training data
	 * @return the relationship extraction model produced by this engine with the provided training data.
	 */
	public Model train(Collection<OperableStructure> list);
}
