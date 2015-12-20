package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.util.ArrayList;
import java.util.List;

import edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.utils.POStoGenericPOSConverter;

/**
 * The Class GenericPartOfSpeechFG is a candidate sentence feature generator that
 * produces generic part-of-speech tags with regard for the results produced by
 * a part-of-speech tagger given as input to the constructor.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class GenericPartOfSpeechFG extends CandidateSentenceFeatureGenerator<SequenceFS<String>> {

	/** The pos tagger. */
	private FeatureGenerator<SequenceFS<String>> posTagger;
	
	/**
	 * Instantiates a new generic part-of-speech generator.
	 *
	 * @param posTagger the part-of-speech tagger that is used as a basis to produce the result of this feature generator
	 */
	public GenericPartOfSpeechFG(FeatureGenerator<SequenceFS<String>> posTagger){
		this.posTagger = posTagger;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected SequenceFS<String> extractFeatures(CandidateSentence sentence) {
		SequenceFS<String> pos = sentence.getFeatures(posTagger);

		String[] gpos = new String[pos.size()];
		for(int i=0; i<pos.size(); i++){
			gpos[i]=POStoGenericPOSConverter.convertPOS(pos.getElement(i));
		}

		return new SequenceFS<String>(gpos);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> retrieveRequiredFeatureGenerators() {
		ArrayList<FeatureGenerator> ret = new ArrayList<FeatureGenerator>();
		
		ret.add(posTagger);
	
		return ret;
	}
}
