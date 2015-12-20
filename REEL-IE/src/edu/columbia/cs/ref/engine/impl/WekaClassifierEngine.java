package edu.columbia.cs.ref.engine.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.filters.StringInputStream;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenInformationExtractionFG;
import edu.columbia.cs.ref.engine.Engine;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.WekableStructure;
import edu.columbia.cs.ref.model.feature.impl.WekaInstanceFS;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.re.impl.WekaClassifierModel;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.washington.cs.knowitall.extractor.conf.BooleanFeatureSet;
import edu.washington.cs.knowitall.extractor.conf.LabeledBinaryExtraction;
import edu.washington.cs.knowitall.extractor.conf.LabeledBinaryExtractionReader;
import edu.washington.cs.knowitall.extractor.conf.ReVerbFeatures;
import edu.washington.cs.knowitall.extractor.conf.WekaDataSet;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;

/**
 * The Class WekaClassifierEngine is an implementation of an Engine that produces models
 * by using generic classifiers from <a href="http://www.cs.waikato.ac.nz/ml/weka/">Weka</a>.
 * 
 * <br>
 * <br>
 * 
 * It receives as input the classifier that will be used, the features that will be considered,
 * a structure configuration and a set of relationship extractions to
 * be extracted.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class WekaClassifierEngine implements Engine {

	/** The classifier. */
	private Classifier classifier;
	
	/** The feature set. */
	private BooleanFeatureSet<ChunkedBinaryExtraction> featureSet;
	
	/** The conf. */
	private StructureConfiguration conf;
	
	/** The relationship types. */
	private Set<RelationshipType> relationshipTypes;
	
	/**
	 * Instantiates a new Weka classifier engine.
	 *
	 * @param classifier the classifier to be used (not yet trained)
	 * @param featureSet the features to be used by the classifier
	 * @param conf the structure configuration
	 * @param relationshipTypes the relationship types to extract
	 */
	public WekaClassifierEngine(Classifier classifier, BooleanFeatureSet<ChunkedBinaryExtraction> featureSet, StructureConfiguration conf, Set<RelationshipType> relationshipTypes){
	
		this.classifier = classifier;
		
		this.featureSet = featureSet;
		
		this.conf=conf;
		
		this.relationshipTypes=relationshipTypes;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.engine.Engine#train(java.util.List)
	 */
	@Override
	public Model train(Collection<OperableStructure> list) {

		for(RelationshipType type : relationshipTypes){
			
			Instance sampleInstance = null;
			for(OperableStructure first : list){
				sampleInstance = ((WekableStructure)first).getInstance();
				break;
			}
			
			FastVector attributes = generateAttributeFastVector(sampleInstance);
			
			Instances instances = new Instances("train", attributes, 0);
			
			instances.setClassIndex(featureSet.getNumFeatures());
	
			for (OperableStructure operableStructure : list) {
			
				Instance instance = ((WekableStructure)operableStructure).getInstance();
				
				instance.setValue((Attribute)attributes.elementAt(featureSet.getNumFeatures()), generateLabel(type,operableStructure));
				
				Instance inst = new Instance(instance);
				
				instances.add(inst);
				
				inst.setDataset(instances);
			}
	
			try {
				classifier.buildClassifier(instances);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return new WekaClassifierModel(classifier, type.getType(), conf, relationshipTypes);
		}
		return null;
		
	}
	
	private String generateLabel(RelationshipType type, OperableStructure operableStructure) {
		Set<String> labels = operableStructure.getLabels();
		if(labels.contains(type.getType())){
			return WekaClassifierModel.POSITIVE_LABEL;
		}
		return WekaClassifierModel.NEGATIVE_LABEL;
	}
	
	private FastVector generateAttributeFastVector(
			Instance sampleInstance) {
		
		int numFeatures = sampleInstance.numAttributes();

		FastVector attributes = new FastVector(numFeatures); // has space for class attribute already 
		// Construct a numeric attribute for each feature in the set
		for (int i = 0; i < numFeatures; i++) {

			attributes.addElement(sampleInstance.attribute(i));

		}
		
		return attributes;

	}
	
}
