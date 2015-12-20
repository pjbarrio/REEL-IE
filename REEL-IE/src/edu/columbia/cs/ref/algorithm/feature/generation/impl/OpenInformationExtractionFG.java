package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.feature.FeatureSet;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.ref.model.feature.impl.WekaInstanceFS;
import edu.columbia.cs.ref.model.re.impl.WekaClassifierModel;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.utils.AlternativeOpenIEFeatures;
import edu.washington.cs.knowitall.extractor.conf.BooleanFeatureSet;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedArgumentExtraction;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedExtraction;
import edu.washington.cs.knowitall.util.Range;

/**
 * This class is used for the implementation of the ReVerb confidence function that is described in: 
 * <b> "Identifying Relations for Open Information Extraction" </b>. A. Fader and S. Soderland and O. Etzioni. In Conference on Empirical Methods in Natural Language Processing 2011, 2011.
 * For further information, <a href="http://reverb.cs.washington.edu/"> ReVerb Website </a>.
 * 
 * <br>
 * <br>
 * 
 * The Class OpenInformationExtractionFG is a candidate sentence feature generator which transforms all the
 * information from the features needed for Open Information Extraction into a WekaInstanceFS. Thus, the 
 * constructor of this class receives the information needed by Open Information Extraction: a tokenizer,
 * a part-of-speech tagger, a chunker and an entity based splitter.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenInformationExtractionFG extends
		CandidateSentenceFeatureGenerator<WekaInstanceFS> {

	/** The Constant TMP_LABEL_OPTIMIZATION. */
	private static final String TMP_LABEL_OPTIMIZATION = WekaClassifierModel.NEGATIVE_LABEL;
	
	/** The tokenizer. */
	private FeatureGenerator<SequenceFS<String>> tokenizer;
	
	/** The pos tagger. */
	private FeatureGenerator<SequenceFS<String>> posTagger;
	
	/** The chunker. */
	private FeatureGenerator<SequenceFS<String>> chunker;
	
	/** The section split. */
	private FeatureGenerator<SequenceFS<Span>> sectionSplit;
	
	/**
	 * Instantiates a new OpenInformationExtractionFG.
	 *
	 * @param featureSet the feature set needed by open information extraction
	 * @param tokenizer the tokenizer
	 * @param posTagger the part-of-speech tagger
	 * @param chunker the chunker
	 * @param sectionSplit the entity-based sentence split
	 */
	public OpenInformationExtractionFG(AlternativeOpenIEFeatures featureSet,
			FeatureGenerator<SequenceFS<String>> tokenizer,
			FeatureGenerator<SequenceFS<String>> posTagger,
			FeatureGenerator<SequenceFS<String>> chunker,
			FeatureGenerator<SequenceFS<Span>> sectionSplit){
		this(featureSet);
		this.tokenizer=tokenizer;
		this.posTagger=posTagger;
		this.chunker=chunker;
		this.sectionSplit=sectionSplit;
	}
	
	/** The I efeatures. */
	private AlternativeOpenIEFeatures IEfeatures; //= new ReVerbFeatures().getFeatureSet();

	/** The num features. */
	private int numFeatures;

	/** The attributes. */
	private FastVector attributes = null;
	
	/** The dataset. */
	private Instances dataset = null;
	
	private OpenInformationExtractionFG(AlternativeOpenIEFeatures featureSet){
		
		this.IEfeatures = featureSet;
		
		numFeatures = featureSet.getFeatureSet().getNumFeatures();
		
		attributes = new FastVector(numFeatures+1);
		
		List<String> featureNames = featureSet.getFeatureSet().getFeatureNames();
		for (int i = 0; i < numFeatures; i++) {
			attributes.addElement(new Attribute(featureNames.get(i)));
			
		}		
		
		//just for optimization, we add the class attribute.
		
		FastVector classVals = new FastVector(2);
		
		classVals.addElement(WekaClassifierModel.NEGATIVE_LABEL);
		
		classVals.addElement(WekaClassifierModel.POSITIVE_LABEL);
		
		Attribute classAttr = new Attribute("class", classVals);
		
		attributes.addElement(classAttr);
		
		dataset = new Instances("",attributes, 0);

	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected WekaInstanceFS extractFeatures(CandidateSentence candidateSentence) {
		
		SequenceFS<String> tokensString = candidateSentence.getFeatures(tokenizer);
		
		String[] tokens = generateArray(tokensString);

		SequenceFS<String> pos = candidateSentence.getFeatures(posTagger);
		
		String[] posTags = generateArray(pos);

		SequenceFS<String> chunks = candidateSentence.getFeatures(chunker);

		String[] npChunkTags = generateArray(chunks); 
		
		ChunkedSentence sent = new ChunkedSentence(tokens, posTags, npChunkTags);

		SequenceFS<Span> span = candidateSentence.getFeatures(sectionSplit);
		
		// Next two lines define arg1: first is the tokens, then is the range. Only need 
		// the range to construct the extraction.
		Range arg1Range = readRange(span.getElement(1));
		// Same for the relation and arg2
		Range relRange = readRange(span.getElement(2));
		Range arg2Range = readRange(span.getElement(3));
		
		// Construct the extraction
		ChunkedExtraction rel = new ChunkedExtraction(sent, relRange);
		ChunkedArgumentExtraction arg1 = new ChunkedArgumentExtraction(sent, arg1Range, rel);
		ChunkedArgumentExtraction arg2 = new ChunkedArgumentExtraction(sent, arg2Range, rel);
		ChunkedBinaryExtraction labeled = new ChunkedBinaryExtraction(rel, arg1, arg2);
				
		Instance inst = new Instance(numFeatures + 1);
		inst.setDataset(dataset);
		double[] featureVals = IEfeatures.getFeatureSet().featurizeToDouble(labeled);
		for (int i = 0; i < numFeatures; i++) {
			//System.out.println(i + " " + numFeatures + " " + attributes.elementAt(i) + " " + featureVals[i]);
			inst.setValue((Attribute)attributes.elementAt(i), featureVals[i]);
		}

		//just for optimization, we add the class attribute.
		
		inst.setValue((Attribute)attributes.elementAt(numFeatures), TMP_LABEL_OPTIMIZATION);
		
		return new WekaInstanceFS(inst);

	}

	private String[] generateArray(SequenceFS<String> sequence) {
		
		return sequence.toArray();
		
	}

	private Range readRange(Span span){
		int start = span.getStart();
		int length = span.getEnd() - span.getStart() + 1;
		return new Range(start, length);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> retrieveRequiredFeatureGenerators() {
		ArrayList<FeatureGenerator> ret = new ArrayList<FeatureGenerator>();
		
		ret.add(tokenizer);
		ret.add(posTagger);
		ret.add(chunker);
		ret.add(sectionSplit);
	
		return ret;
	}

}
