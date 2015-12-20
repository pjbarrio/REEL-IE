package edu.columbia.cs.utils;

import java.io.IOException;

import weka.classifiers.Classifier;
import edu.washington.cs.knowitall.extractor.conf.BooleanFeatureSet;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbFeatures;
import edu.washington.cs.knowitall.extractor.conf.WekaClassifierConfFunction;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;

/**
 * This class is used for the implementation of the ReVerb confidence function that is described in: 
 * <b> "Identifying Relations for Open Information Extraction" </b>. A. Fader and S. Soderland and O. Etzioni. In Conference on Empirical Methods in Natural Language Processing 2011, 2011.
 * For further information, <a href="http://reverb.cs.washington.edu/"> ReVerb Website </a>.
 * 
 * <br><br>
 * 
 * The idea of this class is to make the confidence function independent of the classifier
 * used. So, instead of relying on a logistic regression, this class can receive any
 * classifier available in <a href="http://www.cs.waikato.ac.nz/ml/weka/">Weka</a>.
 *
 * @see <a href="http://reverb.cs.washington.edu/">ReVerb Website</a>, <a href="http://www.cs.waikato.ac.nz/ml/weka/">Weka</a>
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class AlternativeOpenIEConfFunction {
	
	/** The classifier. */
	private Classifier classifier;
	
	/** The reverb features. */
	private AlternativeOpenIEFeatures reverbFeatures;
	
	/** The feature set. */
	private BooleanFeatureSet<ChunkedBinaryExtraction> featureSet;
	
	/** The func. */
	private WekaClassifierConfFunction<ChunkedBinaryExtraction> func;
	
	/**
	 * Instantiates a new confidence function that uses the default classifier from ReVerb (Logistic regression).
	 * 
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public AlternativeOpenIEConfFunction() throws IOException {
		classifier = DefaultObjects.getDefaultConfClassifier();
		reverbFeatures = new AlternativeOpenIEFeatures();
		featureSet = reverbFeatures.getFeatureSet();
		func = new WekaClassifierConfFunction<ChunkedBinaryExtraction>(featureSet, classifier);
	}

	/**
	 * Instantiates a new confidence function that uses a classifier given as input
	 *
	 * @param c the classifier used to compute the confidence function
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public AlternativeOpenIEConfFunction(Classifier c) throws IOException {
		classifier = c;
		reverbFeatures = new AlternativeOpenIEFeatures();
		featureSet = reverbFeatures.getFeatureSet();
		func = new WekaClassifierConfFunction<ChunkedBinaryExtraction>(featureSet, classifier);
	}
	

	/**
	 * Gets the confidence score of the extraction
	 *
	 * @param extr the extraction for which we are trying to determine the confidence 
	 * @return the confidence value of the input extraction
	 * @throws ConfidenceFunctionException the confidence function exception
	 */
	public double getConf(ChunkedBinaryExtraction extr) throws ConfidenceFunctionException {
        try {
            return func.getConf(extr);
        }
        catch (Exception e) {
            throw new ConfidenceFunctionException(e);
        }
	}
}
