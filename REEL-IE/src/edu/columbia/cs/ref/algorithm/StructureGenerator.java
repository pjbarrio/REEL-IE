package edu.columbia.cs.ref.algorithm;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;

/**
 * The Class StructureGenerator is responsible for generating all the operable structures from a given
 * set of candidate sentences with regard for a given configuration file.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class StructureGenerator {
	
	/**
	 * Generate all the operable structures from a given
	 * set of candidate sentences with regard for a given configuration file.
	 *
	 * @param candidates the candidate sentences
	 * @param config the structure configuration
	 * @return the generated operable structures
	 */
	public static Set<OperableStructure> generateStructures(Set<CandidateSentence> candidates, StructureConfiguration config){
		Set<OperableStructure> result = new HashSet<OperableStructure>();
		for(CandidateSentence sent : candidates){
			result.add(config.getOperableStructure(sent));
		}
		return result;
	}
}
