

import weka.classifiers.Classifier;
import edu.washington.cs.knowitall.extractor.conf.BooleanFeatureSet;
import edu.washington.cs.knowitall.extractor.conf.LabeledBinaryExtraction;
import edu.washington.cs.knowitall.extractor.conf.ReVerbFeatures;
import edu.washington.cs.knowitall.extractor.conf.WekaDataSet;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;

/***
 * Used to train the ReVerb confidence function using the features described
 * by <code>ReVerbFeatures</code>. Given a set of <code>LabeledBinaryExtraction</code>
 * instances, this class featurizes them and trains a logistic regression classifier
 * using Weka's <code>Logistic</code> class. 
 * 
 * This class can be called from the command-line to train a classifier and save the
 * resulting model to a file.
 * 
 * @author afader
 *
 */
public class ReVerbClassifierTrainer {
	
	private BooleanFeatureSet<ChunkedBinaryExtraction> featureSet;
	private Classifier classifier;
	private WekaDataSet<ChunkedBinaryExtraction> dataSet;
	
	/**
	 * Constructs and trains a new Logistic classifier using the given examples.
	 * @param examples
	 * @throws Exception
	 */

	public ReVerbClassifierTrainer(Iterable<LabeledBinaryExtraction> examples, Classifier classifier) throws Exception {
		ReVerbFeatures feats = new ReVerbFeatures();
		featureSet = feats.getFeatureSet();
		createDataSet(examples);
		this.classifier = classifier;
		train();
	}
	
	/**
	 * @return the data set used to train the classifier
	 */
	public WekaDataSet<ChunkedBinaryExtraction> getDataSet() {
		return dataSet;
	}
	
	/**
	 * @return the trained classifier.
	 */
	public Classifier getClassifier() {
		return classifier;
	}
	
	private void createDataSet(Iterable<LabeledBinaryExtraction> examples) {
		dataSet = new WekaDataSet<ChunkedBinaryExtraction>("train", featureSet);
		for (LabeledBinaryExtraction extr : examples) {
			int label = extr.isPositive() ? 1 : 0;
			dataSet.addInstance(extr, label);
		}
	}

	private void train() throws Exception {
		classifier.buildClassifier(dataSet.getWekaInstances());
	}

}
