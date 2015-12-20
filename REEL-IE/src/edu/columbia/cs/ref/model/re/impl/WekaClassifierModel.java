package edu.columbia.cs.ref.model.re.impl;

import java.util.HashSet;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenInformationExtractionFG;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.impl.OpenInformationExtractionCore;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.WekableStructure;
import edu.columbia.cs.ref.model.feature.impl.WekaInstanceFS;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class JLibsvmMulticlassModel is an extention of the class Model that is based
 * on a generic classfier from <a href="http://www.cs.waikato.ac.nz/ml/weka/">Weka</a>.
 * 
 * <br>
 * <br>
 * 
 * This model should not be created directly by the used. Instead, it should be created using
 * an instance of the class <code>WekaClassifierEngine</code>
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class WekaClassifierModel extends Model {

	/** The Constant POSITIVE_LABEL. */
	public static transient final String POSITIVE_LABEL = "positive";

	/** The Constant NEGATIVE_LABEL. */
	public static transient final String NEGATIVE_LABEL = RelationshipType.NOT_A_RELATIONSHIP;
	
	/** The classifier. */
	private Classifier classifier;
	
	/** The positive label. */
	private String positiveLabel;

	/** The conf. */
	private StructureConfiguration conf;
	
	/** The relationship types. */
	private Set<RelationshipType> relationshipTypes;
	
	/**
	 * Instantiates a WekaClassifierModel.
	 *
	 * @param classifier the already trained classifier used in the relationship extraction task
	 * @param positiveLabel the positive label of the classifier
	 * @param conf the structure configuration for the relationship extraction task this model performs
	 * @param relationshipTypes the relationship types that this model extracts
	 */
	public WekaClassifierModel(Classifier classifier, String positiveLabel, StructureConfiguration conf, Set<RelationshipType> relationshipTypes){
		this.classifier = classifier;
		this.positiveLabel=positiveLabel;
		this.conf=conf;
		this.relationshipTypes=relationshipTypes;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getPredictedLabel(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	protected Set<String> getPredictedLabel(OperableStructure s) {
		Set<String> result = new HashSet<String>();
		
		Instance instance = ((WekableStructure)s).getInstance();		
		FastVector attributes = generateAttributeFastVector(instance);
		
		Instances instances = new Instances("test", attributes, 0);
		
		instances.setClassIndex(attributes.size()-1);
		
		instance.setDataset(instances);
		
		try {
			double classification = classifier.classifyInstance(instance);
			if (classification != 0.0){
				result.add(positiveLabel);
				return result;
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getPredictionPropertyValue(edu.columbia.cs.ref.model.re.Model.PredictionProperties)
	 */
	@Override
	protected Object getPredictionPropertyValue(
			PredictionProperties predictionProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getAvailablePredictionProperties()
	 */
	@Override
	protected PredictionProperties[] getAvailablePredictionProperties() {
		return new PredictionProperties[]{};
	}


	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getRelationshipTypes()
	 */
	@Override
	public Set<RelationshipType> getRelationshipTypes() {
		return relationshipTypes;
	}


	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getStructureConfiguration()
	 */
	@Override
	public StructureConfiguration getStructureConfiguration() {
		return conf;
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
