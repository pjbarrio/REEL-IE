package edu.columbia.cs.ref.algorithm.feature.generation;

import java.util.List;

import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;

/**
 * The Class CandidateSentenceFeatureGenerator is an extension of a FeatureGenerator
 * that produces and stores the features on a candidate sentence.
 *
 * @param <E> the type of feature that the generator produces.
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class CandidateSentenceFeatureGenerator<E extends FeatureSet> extends FeatureGenerator<E> {

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	protected final E extractFeatures(OperableStructure s) {
		return extractFeatures(s.getCandidateSentence());
	}

	/**
	 * Extract features for the input candidate sentence.
	 *
	 * @param candidateSentence the candidate sentence
	 * @return the features produced by this feature generator for the input candidate sentence
	 */
	protected abstract E extractFeatures(CandidateSentence candidateSentence);

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator#getFeatures(edu.columbia.cs.ref.model.core.structure.OperableStructure, edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator)
	 */
	@Override
	protected <E extends FeatureSet> E getFeatures(OperableStructure s,
			FeatureGenerator<E> featureGenerator){
		return s.getCandidateSentence().getFeatures(featureGenerator);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator#setFeatures(edu.columbia.cs.ref.model.core.structure.OperableStructure, edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator, edu.columbia.cs.ref.model.feature.FeatureSet)
	 */
	@Override
	protected <E extends FeatureSet> void setFeatures(OperableStructure s,
			FeatureGenerator<E> featureGenerator, E fs){		
		s.getCandidateSentence().setFeatures(featureGenerator, fs);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	protected abstract List<FeatureGenerator> retrieveRequiredFeatureGenerators();
}
