package edu.columbia.cs.ref.model.feature.impl;

import java.io.Serializable;
import java.util.Arrays;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;

/**
 * The Class SequenceFS is an implementation of a FeatureSet for which
 * the features are represented in the form of a sequence.
 *
 * @param <E> the type of the atomic elements of the features
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class SequenceFS<E extends Serializable> extends FeatureSet {
	
	/** The sequence. */
	private E[] sequence;
	
	/**
	 * Instantiates a new SequenceFS given an input sequence in the form of an array.
	 *
	 * @param sequence the input sequence in the form of an array
	 */
	public SequenceFS(E[] sequence){
		this.sequence=sequence;
	}

	/**
	 * Returns the size of the sequence
	 *
	 * @return the size of the sequence
	 */
	public int size(){
		return sequence.length;
	}
	
	/**
	 * Returns the element in the input position
	 *
	 * @param position the input position
	 * @return the element in the input position
	 */
	public E getElement(int position){
		return sequence[position];
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return Arrays.toString(sequence);
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.feature.FeatureSet#enrichMe(edu.columbia.cs.ref.model.core.structure.OperableStructure)
	 */
	@Override
	public void enrichMe(OperableStructure operableStructure) {
		operableStructure.add(this);
	}

	/**
	 * Returns the content of the sequence in the form of an array
	 *
	 * @return the content of the sequence in the form of an array
	 */
	public E[] toArray() {
		return sequence;
	}
}
