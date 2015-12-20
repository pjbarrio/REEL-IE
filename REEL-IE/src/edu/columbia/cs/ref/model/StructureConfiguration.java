package edu.columbia.cs.ref.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;

/**
 * The Class StructureConfiguration.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class StructureConfiguration implements Serializable {
	
	/** The user fg. */
	private List<FeatureGenerator> userFg;
	
	/** The classification core. */
	private Core classificationCore;
	
	/** The user fg keys. */
	private List<Class<? extends FeatureGenerator>> userFgKeys;
	
	/**
	 * Instantiates a new structure configuration.
	 *
	 * @param cCore the c core
	 */
	public StructureConfiguration(Core cCore){
		classificationCore=cCore;
		userFg=new ArrayList<FeatureGenerator>();
		userFgKeys=new ArrayList<Class<? extends FeatureGenerator>>();
	}
	
	/**
	 * Adds the feature generator.
	 *
	 * @param fg the fg
	 */
	public void addFeatureGenerator(FeatureGenerator fg){
		userFg.add(fg);
		userFgKeys.add(fg.getClass());
	}
	
	/**
	 * Gets the operable structure.
	 *
	 * @param sent the sent
	 * @return the operable structure
	 */
	public OperableStructure getOperableStructure(CandidateSentence sent){
		OperableStructure newStructure = classificationCore.getStructure(sent);
		
		int numFeatures = userFg.size();
		for(int i=0; i<numFeatures; i++){
			userFg.get(i).generateFeatures(newStructure);
			FeatureSet fs = newStructure.getFeatures(userFg.get(i));
			newStructure.enrich(fs);
		}
		
		return newStructure;
	
	}
	
	/**
	 * Gets the core.
	 *
	 * @return the core
	 */
	public Core getCore(){
		return classificationCore;
	}
	
	/**
	 * Gets the user fg.
	 *
	 * @return the user fg
	 */
	public List<FeatureGenerator> getUserFG(){
		return userFg;
	}
}
