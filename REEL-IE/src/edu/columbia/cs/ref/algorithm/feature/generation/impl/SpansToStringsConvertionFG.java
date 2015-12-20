package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.InvalidFormatException;
import edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;

/**
 * The Class SpansToStringsConvertionFG is a candidate sentence feature generator that 
 * transforms the results of a span-based tokenization into a string-based tokenization.
 * 
 * <br>
 * <br>
 * 
 * This class receives as input another feature generator that must produce the span-based
 * tokenization.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class SpansToStringsConvertionFG extends CandidateSentenceFeatureGenerator<SequenceFS<String>> {

	/** The tokenizer. */
	private FeatureGenerator<SequenceFS<Span>> tokenizer;
	
	/**
	 * Instantiates a new SpansToStringsConvertionFG
	 *
	 * @param tokenizer the feature generator that produces the span-based tokenization
	 * @throws InvalidFormatException the invalid format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SpansToStringsConvertionFG(FeatureGenerator<SequenceFS<Span>> tokenizer) throws InvalidFormatException, IOException{
		this.tokenizer = tokenizer;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected SequenceFS<String> extractFeatures(CandidateSentence sentence) {
		
		SequenceFS<Span> spans = sentence.getFeatures(tokenizer); //for all but OIE
		
//		SequenceFS<Span> spans = sentence.getSentence().getFeatures(tokenizer); //for openIE

		
		return new SequenceFS<String>(getTokens(spans, sentence));
		
	}

	private String[] getTokens(SequenceFS<Span> spans, CandidateSentence sentence){
		String value = sentence.getSentence().getValue();
		String[] tokens = new String[spans.size()];
		
		for(int i=0; i<spans.size(); i++){
			Span s = spans.getElement(i);
			tokens[i]=value.substring(s.getStart(),s.getEnd());
		}
		
		return tokens;
	}

	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> retrieveRequiredFeatureGenerators() {
		
		ArrayList<FeatureGenerator> ret = new ArrayList<FeatureGenerator>();
		
		ret.add(tokenizer);
		
		return ret;
		
	}

}
