package edu.columbia.cs.ref.model.core.structure.impl;

import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * For further information, <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>.
 * 
 * <br><br>
 * 
 * <b>Description</b><br><br>
 * 
 * This operable structure is composed by <code>Relationship</code>.
 * 
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class RelationOperableStructure extends OperableStructure {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The relation. */
	private Relationship relation;

	/**
	 * Instantiates a new relation operable structure given an input relationship
	 *
	 * @param relation the relationship that composes this structure
	 */
	public RelationOperableStructure(Relationship relation) {
		super(null);
		this.relation = relation;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.core.structure.OperableStructure#initialize()
	 */
	@Override
	public void initialize() {
		;
	}

	/**
	 * Returns the relationship that composes this structure.
	 *
	 * @return the relationship that composes this structure
	 */
	public Relationship getRelation(){
		return relation;
	}
}
