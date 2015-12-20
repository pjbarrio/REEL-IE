package edu.columbia.cs.ref.model.re.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class JLibsvmBinaryModel is an extention of the class Model that corresponds to a composition of
 * binary SVM classfiers from <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>.
 * 
 * <br>
 * <br>
 * 
 * This model should not be created directly by the used. Instead, it should be created using
 * an instance of the class <code>JLibSVMBinaryEngine</code>
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class JLibsvmCompositeBinaryModel extends Model{
	
	/** The models. */
	private Set<JLibsvmBinaryModel> models;
	
	/** The conf. */
	private StructureConfiguration conf;
	
	/** The relationship types. */
	private Set<RelationshipType> relationshipTypes;

	/**
	 * Instantiates a new JLibsvmCompositeBinaryModel.
	 *
	 * @param conf the structure configuration for the relationship extraction task this model performs
	 * @param relationshipTypes the relationship types that this model extracts
	 */
	public JLibsvmCompositeBinaryModel(StructureConfiguration conf, Set<RelationshipType> relationshipTypes) {
		models = new HashSet<JLibsvmBinaryModel>();
		this.relationshipTypes=relationshipTypes;
		this.conf=conf;
	}

	/**
	 * Adds the an individual binary model to the composite model
	 *
	 * @param model the model to be added
	 */
	public void addModel(JLibsvmBinaryModel model){
		models.add(model);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getPredictedLabel(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	public Set<String> getPredictedLabel(OperableStructure s) {
		Set<String> labels = new HashSet<String>();
		for(JLibsvmBinaryModel model : models){
			labels.addAll(model.getPredictedLabel(s));
		}

		return labels;
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

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		out.writeInt(models.size());
		for(JLibsvmBinaryModel model : models){
			out.writeObject(model);
		}
		out.writeObject(conf);
		out.writeObject(relationshipTypes);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		int sizeModels = in.readInt();
		models = new HashSet<JLibsvmBinaryModel>();
		for(int i=0; i<sizeModels; i++){
			models.add((JLibsvmBinaryModel) in.readObject());
		}
		conf=(StructureConfiguration) in.readObject();
		relationshipTypes=(Set<RelationshipType>) in.readObject();
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
