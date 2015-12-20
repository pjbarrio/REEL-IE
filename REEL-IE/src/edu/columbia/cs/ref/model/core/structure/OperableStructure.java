package edu.columbia.cs.ref.model.core.structure;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.feature.FeatureSet;
import edu.columbia.cs.ref.model.feature.impl.GraphFS;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.ref.model.feature.impl.WekaInstanceFS;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class OperableStructure corresponds to the structure that a given core
 * knows how to process.
 * 
 * <br>
 * <br>
 * 
 * An OperableStructure is typically composed by a basic structure that is build
 * using the <code>initialize</code> method. However, this structure can be typically
 * enriched with additional features using the <code>enrich</code> method.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class OperableStructure implements Serializable {

	/** The candidate sentence. */
	private CandidateSentence candidateSentence;
	
	/** The features table. */
	private Hashtable<Class<? extends FeatureGenerator>, FeatureSet> featuresTable;
	
	/** The has computed hash. */
	private transient boolean hasComputedHash=false;
	
	/** The hash code. */
	private transient int hashCode=-1;
	
	/**
	 * Instantiates a new operable structure for an input candidate sentence
	 *
	 * @param c the candidate sentence
	 */
	public OperableStructure(CandidateSentence c){
		this.candidateSentence=c;
		featuresTable = new Hashtable<Class<? extends FeatureGenerator>, FeatureSet>();
	}

	/**
	 * Initializes the basic structure of the operable structure
	 */
	public abstract void initialize();

	/**
	 * Returns the candidate sentence associated with this operable structure.
	 *
	 * @return the candidate sentence associated with this operable structure.
	 */
	public CandidateSentence getCandidateSentence() {
		return candidateSentence;
	}
	
	/**
	 * Gets the feature set with a given identifier
	 *
	 * @param <E> the type of feature set
	 * @param featureGenerator the identifier of the feature generator
	 * @return the feature set associated with the input identifier
	 */
	public <E extends FeatureSet> E getFeatures(Class<? extends FeatureGenerator<E>> featureGenerator){
		return (E) featuresTable.get(featureGenerator);
	}

	/**
	 * Gets the features set generated with a give feature generator
	 *
	 * @param <E> the type of feature set
	 * @param featureGenerator the feature generator used to generate the features we want to retrieve
	 * @return the features set generated with the input feature generator
	 */
	public <E extends FeatureSet> E getFeatures(FeatureGenerator<E> featureGenerator){
		return (E) featuresTable.get(featureGenerator.getClass());
	}

	/**
	 * Stores the feature set generator with the input feature generator
	 *
	 * @param <E> the type of feature set
	 * @param featureGenerator the feature generator used to generate the features we want to store
	 * @param fs the features set generated with the input feature generator
	 */
	public <E extends FeatureSet> void setFeatures(FeatureGenerator<E> featureGenerator, E fs){
		featuresTable.put(featureGenerator.getClass(),fs);
	}

	/**
	 * Returns the labels associated with this operable structure.
	 *
	 * @return the labels associate with this operable structure
	 */
	public Set<String> getLabels() {
		return candidateSentence.getLabels();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof OperableStructure){
			return candidateSentence.getId().equals(((OperableStructure) o).candidateSentence.getId());
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		if(!hasComputedHash){
			hashCode=candidateSentence.getId().hashCode();
			hasComputedHash=true;
		}
		return hashCode;
	}

	/**
	 * Enriches this structure with a new type of feature
	 *
	 * @param fs the feature set to enrich this structure
	 */
	public void enrich(FeatureSet fs){
		fs.enrichMe(this);
	}
	
	/**
	 * Adds a graph feature to the operable structure
	 *
	 * @param graph the feature set to enrich this structure
	 */
	public void add(GraphFS<? extends Serializable,? extends Serializable> graph){
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds a sequence feature to the operable structure
	 *
	 * @param sequence the feature set to enrich this structure
	 */
	public void add(SequenceFS<? extends Serializable> sequence){
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds a weka feature to the operable structure
	 *
	 * @param wekaInstanceFS the feature set to enrich this structure
	 */
	public void add(WekaInstanceFS wekaInstanceFS) {
		throw new UnsupportedOperationException();
	}
}
