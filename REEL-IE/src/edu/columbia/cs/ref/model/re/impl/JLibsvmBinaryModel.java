package edu.columbia.cs.ref.model.re.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import cern.colt.Arrays;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.SolutionModel;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.Kernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class JLibsvmBinaryModel is an extention of the class Model that is based
 * on a binary SVM classfier from <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>.
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
public class JLibsvmBinaryModel extends Model{
	
	/** The Constant availableStatistics. */
	private static final PredictionProperties[] availableStatistics = new PredictionProperties[]{PredictionProperties.CONFIDENCE,PredictionProperties.PROBABILITY_POSITIVE};
	
	/** The svm model. */
	private transient BinaryModel<String,OperableStructure> svmModel;
	
	/** The last positive probability. */
	private double lastPositiveProbability;
	
	/** The last negative probability. */
	private double lastNegativeProbability;
	
	/** The last confidence. */
	private double lastConfidence;
	
	/** The true label. */
	private String trueLabel;
	
	/** The false label. */
	private String falseLabel;
	
	/** The conf. */
	private StructureConfiguration conf;
	
	/** The relationship type. */
	private Set<RelationshipType> relationshipType;
	
	/**
	 * Instantiates a new JLibsvmBinaryModel.
	 */
	public JLibsvmBinaryModel(){
		
	}
	
	/**
	 * Instantiates a new JLibsvmBinaryModel.
	 *
	 * @param binary the binary classification model for relationship extraction
	 * @param conf the structure configuration for the relationship extraction task this model performs
	 * @param relationshipType the relationship type that this model extracts
	 */
	public JLibsvmBinaryModel(BinaryModel<String,OperableStructure> binary, StructureConfiguration conf, Set<RelationshipType> relationshipType){
		svmModel=binary;
		trueLabel=svmModel.getTrueLabel();
		falseLabel=svmModel.getFalseLabel();
		this.conf=conf;
		this.relationshipType=relationshipType;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getPredictedLabel(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	public Set<String> getPredictedLabel(OperableStructure s) {
		float predictedLabel=svmModel.predictValue(s);
		//String predictedLabel=svmModel.predictLabel(s);
		
		//lastPositiveProbability=svmModel.getTrueProbability(s);
		//lastNegativeProbability=1-lastPositiveProbability;
		//lastConfidence = Math.max(lastPositiveProbability, lastNegativeProbability);
		Set<String> result = new HashSet<String>();
		if(predictedLabel>0){
			if(!trueLabel.equals(RelationshipType.NOT_A_RELATIONSHIP)){
				result.add(trueLabel);
			}
		}else{
			if(!falseLabel.equals(RelationshipType.NOT_A_RELATIONSHIP)){
				result.add(falseLabel);
			}
		}
		
		return result;
		//return predictedLabel;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getAvailablePredictionProperties()
	 */
	@Override
	protected PredictionProperties[] getAvailablePredictionProperties() {
		return availableStatistics;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.re.Model#getPredictionPropertyValue(edu.columbia.cs.ref.model.re.Model.PredictionProperties)
	 */
	@Override
	protected Object getPredictionPropertyValue(
			PredictionProperties predictionProperties) {
		
		switch (predictionProperties) {
		case CONFIDENCE:
			
			return getConfidenceValue();
			
		case PROBABILITY_POSITIVE:
			
			return getProbability_Positive();
			
		//case PROBABILITY_NEGATIVE:
			
		//	return getProbability_Negative();
			
		default:
			return null;
		}
		
	}

	private Object getProbability_Positive() {
		return lastPositiveProbability;
	}
	
	private Object getProbability_Negative() {
		return lastNegativeProbability;
	}

	private Object getConfidenceValue() {
		return lastConfidence;
	}

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		//svmModel.compact();
		int numSvs= svmModel.numSVs;
		Object[] objs = svmModel.SVs;
		OperableStructure[] svs = new OperableStructure[numSvs];
		for(int i=0; i<numSvs; i++){
			svs[i]=(OperableStructure) objs[i];
		}
		JLibsvmModelInformation modelInfo = new JLibsvmModelInformation(svmModel.numSVs, svs, svmModel.alphas, svmModel.rho,svmModel.param.kernel,
				trueLabel,falseLabel);
		out.writeObject(modelInfo);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		BinaryModel<String,OperableStructure> model = new BinaryModel<String,OperableStructure>();
		JLibsvmModelInformation modelInfo = (JLibsvmModelInformation)in.readObject();
		
		model.alphas=modelInfo.getAlpha();
		model.numSVs=modelInfo.getNumSV();
		model.rho=modelInfo.getRho();
		model.SVs=modelInfo.getSupportVectors();
		ImmutableSvmParameterPoint.Builder<String, OperableStructure> builder = new ImmutableSvmParameterPoint.Builder<String, OperableStructure>();
		builder.kernel = modelInfo.getKernel();
		model.param = builder.build();
		
		svmModel=model;
		trueLabel=modelInfo.getTrueLabel();
		falseLabel=modelInfo.getFalseLabel();
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
		return relationshipType;
	}
}
