package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Sequence;

import edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.SentenceFeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Sentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.washington.cs.knowitall.util.DefaultObjects;

/**
 * This class is used for the implementation of the ReVerb confidence function that is described in: 
 * <b> "Identifying Relations for Open Information Extraction" </b>. A. Fader and S. Soderland and O. Etzioni. In Conference on Empirical Methods in Natural Language Processing 2011, 2011.
 * For further information, <a href="http://reverb.cs.washington.edu/"> ReVerb Website </a>.
 * 
 * <br>
 * <br>
 * 
 * The Class KnowItAllChunkingFG is a candidate sentence feature generator that
 * produces the features needed to perform the chunking used by KnowItAll. This
 * feature generator uses the information from a tokenizer and a part-of-speech
 * tagger that are passed as input to the constructor.
 * 
 * <br>
 * <br>
 * 
 * The chunker used in this class is the default chunker used in <a href="http://reverb.cs.washington.edu/">ReVerb</a> and
 * that can be obtained by calling the static method <code>DefaultObjects.getDefaultChunker()</code>.
 *
 * <br>
 * <br>
 *
 * @see <a href="http://reverb.cs.washington.edu/">ReVerb Website</a>
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class KnowItAllChunkingFG extends CandidateSentenceFeatureGenerator<SequenceFS<String>> {

	/** The tokenizer. */
	private FeatureGenerator<SequenceFS<String>> tokenizer;
	
	/** The pos tagger. */
	private FeatureGenerator<SequenceFS<String>> posTagger;
	
	/**
	 * Instantiates a new KnowItAllChunkingFG given a tokenizer and a part-of-speech
	 * tagger.
	 *
	 * @param tokenizer the tokenizer
	 * @param posTagger the part-of-speech tagger
	 */
	public KnowItAllChunkingFG(FeatureGenerator<SequenceFS<String>> tokenizer,
							   FeatureGenerator<SequenceFS<String>> posTagger){
		this.tokenizer = tokenizer;
		this.posTagger = posTagger;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected SequenceFS<String> extractFeatures(CandidateSentence sentence) {
		// TODO Auto-generated method stub

		SequenceFS<String> tokens = sentence.getFeatures(tokenizer); 
		SequenceFS<String> pos = sentence.getFeatures(posTagger);
		
		String[] tkns = generateArray(tokens);
		String[] post = generateArray(pos);
		
		try {
			String[] chunk = DefaultObjects.getDefaultChunker().chunk(tkns,post);
			return new SequenceFS<String>(chunk);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}

	private String[] generateArray(SequenceFS<String> tokens) {
		
		String[] ret = new String[tokens.size()];
		
		for (int i = 0; i < ret.length; i++) {
			
			ret[i] = tokens.getElement(i);
			
		}
		
		return ret;
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> retrieveRequiredFeatureGenerators() {
		ArrayList<FeatureGenerator> ret = new ArrayList<FeatureGenerator>();
		
		ret.add(tokenizer);
		ret.add(posTagger);
	
		return ret;
	}


}
