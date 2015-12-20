package edu.columbia.cs.ref.model.core;

import java.io.Serializable;
import java.util.List;

import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;

/**
 * The core represents the technique that is used to perform the Relationship
 * Extraction task.
 * 
 * <br>
 * <br>
 * 
 * There are some key points that are defined in the core: (i) the type of structure
 * that is used by the relationship extraction task; (ii) the way the data is processed;
 * (iii) the features that are mandatory in order for the technique to work.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class Core implements Serializable{
	
	/** The mandatory fg. */
	List<FeatureGenerator> mandatoryFG=null;
	
	/**
	 * Gets the mandatory feature generators.
	 *
	 * @return the mandatory feature generators
	 */
	public List<FeatureGenerator> getMandatoryFeatureGenerators(){
		if(mandatoryFG==null){
			mandatoryFG=createMandatoryFeatureGenerators();
		}
		return mandatoryFG;
	}
	
	/**
	 * Creates the mandatory feature generators.
	 *
	 * @return the list of mandatory feature generators
	 */
	protected abstract List<FeatureGenerator> createMandatoryFeatureGenerators();

	/**
	 * Transforms a <code>CandidateSentence</code> into the basic structure that
	 * is needed for the core to process the data. This includes: (i) the structure
	 * itself; (ii) the mandatory features needed.
	 *
	 * @param sent a candidate sentence that will be processed with this core
	 * @return a basic structure needed to process the data with this code
	 */
	public OperableStructure getStructure(CandidateSentence sent){
		OperableStructure newStructure = createOperableStructure(sent);
		for(FeatureGenerator fg : getMandatoryFeatureGenerators()){
			fg.generateFeatures(newStructure);
		}
		newStructure.initialize();
		return newStructure;
	}
	
	/**
	 * Creates an empty operable structure. This structure still does not contain the information
	 * from the mandatory features.
	 *
	 * @param sent the candidate sentence that will be processed with this core
	 * @return an empty operable structure that still does not contain the mandatory features (just a reference to the candidate sentence).
	 */
	protected abstract OperableStructure createOperableStructure(CandidateSentence sent);
}
