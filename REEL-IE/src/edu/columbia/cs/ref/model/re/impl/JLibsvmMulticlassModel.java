package edu.columbia.cs.ref.model.re.impl;

import java.util.HashSet;
import java.util.Set;

import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.multi.MultiClassModel;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.Kernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.re.Model.PredictionProperties;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class JLibsvmMulticlassModel is an extention of the class Model that is based
 * on a multiclass SVM classfier from <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>.
 * 
 * <br>
 * <br>
 * 
 * This model should not be created directly by the used. Instead, it should be created using
 * an instance of the class <code>JLibSVMMulticlassEngine</code>
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class JLibsvmMulticlassModel extends Model {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5785782738918799493L;
	
	/** The svm model. */
	private transient MultiClassModel<String,OperableStructure> svmModel;
	
	/** The conf. */
	private StructureConfiguration conf;
	
	/** The relationship types. */
	private Set<RelationshipType> relationshipTypes;
	
	/**
	 * Instantiates a new j libsvm multiclass model model.
	 *
	 * @param model the multiclass classification model for relationship extraction
	 * @param conf the structure configuration for the relationship extraction task this model performs
	 * @param relationshipTypes the relationship type that this model extracts
	 */
	public JLibsvmMulticlassModel(MultiClassModel<String, OperableStructure> model, StructureConfiguration conf, Set<RelationshipType> relationshipTypes) {
		svmModel=model;
		this.conf=conf;
		this.relationshipTypes=relationshipTypes;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getAvailablePredictionProperties()
	 */
	@Override
	protected PredictionProperties[] getAvailablePredictionProperties() {
		return new PredictionProperties[]{};
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getPredictionPropertyValue(edu.columbia.cs.ref.model.re.Model.PredictionProperties)
	 */
	@Override
	protected Object getPredictionPropertyValue(
			PredictionProperties predictionProperties) {

		return null;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getPredictedLabel(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	protected Set<String> getPredictedLabel(OperableStructure s) {
		Set<String> result = new HashSet<String>();
		String label = svmModel.predictLabel(s);
		if(!label.equals(RelationshipType.NOT_A_RELATIONSHIP)){
			result.add(label);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getStructureConfiguration()
	 */
	@Override
	public StructureConfiguration getStructureConfiguration() {
		return conf;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getRelationshipTypes()
	 */
	@Override
	public Set<RelationshipType> getRelationshipTypes() {
		return relationshipTypes;
	}

}
