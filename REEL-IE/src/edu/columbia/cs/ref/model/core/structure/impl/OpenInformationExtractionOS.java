package edu.columbia.cs.ref.model.core.structure.impl;

import weka.core.Instance;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntitySplitsFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.KnowItAllChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenInformationExtractionFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPPartOfSpeechFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.SpansToStringsConvertionFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.WekableStructure;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;

/**
 * This class is used for the implementation of the ReVerb confidence function that is described in: 
 * <b> "Identifying Relations for Open Information Extraction" </b>. A. Fader and S. Soderland and O. Etzioni. In Conference on Empirical Methods in Natural Language Processing 2011, 2011.
 * For further information, <a href="http://reverb.cs.washington.edu/"> ReVerb Website </a>.
 *
 * <br>
 * <br>
 * 
 * This operable structure stores a representation of a sentence as an Instance that uses
 * the features used by ReVerb.
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenInformationExtractionOS extends OperableStructure implements WekableStructure {

	/** The tokens. */
	private String tokens;
	
	/** The pos. */
	private String pos;
	
	/** The chunks. */
	private String chunks;
	
	/** The first. */
	private String first;
	
	/** The first indexes. */
	private String firstIndexes;
	
	/** The middle. */
	private String middle;
	
	/** The middle indexes. */
	private String middleIndexes;
	
	/** The second. */
	private String second;
	
	/** The second indexes. */
	private String secondIndexes;
	
	/** The string. */
	private String string;
	
	/** The instance. */
	private Instance instance;
	
	
	/**
	 * Instantiates a new OpenInformationExtractionOS given a candidate sentence.
	 *
	 * @param c the candidate sentence associated to this structure
	 */
	public OpenInformationExtractionOS(CandidateSentence c) {
		super(c);
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2183612647555553754L;
	

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.core.structure.OperableStructure#initialize()
	 */
	@Override
	public void initialize() {
		string = null;	
		
		SequenceFS<String> tokensString = getFeatures(SpansToStringsConvertionFG.class);
		
		tokens = getString(tokensString);
		
		pos = getString(getFeatures(OpenNLPPartOfSpeechFG.class));
		
		chunks = getString(getFeatures(KnowItAllChunkingFG.class));

		SequenceFS<Span> span = getFeatures(EntitySplitsFG.class);
		
		first = getString(span.getElement(1),tokensString);
		
		firstIndexes = getString(span.getElement(1));

		middle = getString(span.getElement(2),tokensString);
		
		middleIndexes = getString(span.getElement(2));

		second = getString(span.getElement(3),tokensString);
		
		secondIndexes = getString(span.getElement(3));
		
		instance=getFeatures(OpenInformationExtractionFG.class).getInstance();
	}

	private String getString(Span span) {
		
		return span.getStart() + " " + (span.getEnd() - span.getStart() + 1);
		
	}

	private String getString(Span element, SequenceFS<String> tokensString) {
		
		int index = element.getStart();
		
		int end = element.getEnd();
		
		String ret = "";
		
		if(end<index){
			return ret;
		}
		
		for (int i = index; i <= end; i++) {
			
			ret = ret + " " + tokensString.getElement(i);
			
		}
		
		return ret.substring(1);

	}

	private String getString(SequenceFS<String> features) {
		
		String ret = "";
		
		for (int i = 0; i < features.size(); i++) {
			
			ret = ret + " " + features.getElement(i);
			
		}
		
		return ret.substring(1);
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		
		return getStringVersion();
	
	}

	private String getStringVersion() {
		
		if (string == null){
			string = tokens + "\n" + pos + "\n" + chunks + "\n" + first + "\n" + firstIndexes + "\n" + middle + "\n" + middleIndexes + "\n" + second + "\n" + secondIndexes;
		}
		return string;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.core.structure.WekableStructure#getInstance()
	 */
	@Override
	public Instance getInstance() {
		return instance;
	}
}
