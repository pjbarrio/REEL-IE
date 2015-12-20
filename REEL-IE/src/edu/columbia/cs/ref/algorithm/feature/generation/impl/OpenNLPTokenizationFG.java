package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.SentenceFeatureGenerator;
import edu.columbia.cs.ref.model.Sentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.FeatureSet;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.ref.model.re.impl.JLibsvmModelInformation;

/**
 * The Class OpenNLPTokenizationFG is a candidate sentence feature generator that 
 * produces a tokenization of a sentence according to a previously.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenNLPTokenizationFG extends SentenceFeatureGenerator<SequenceFS<Span>> implements Serializable {

	/** The tokenizer. */
	private transient Tokenizer tokenizer;
	
	/** The path. */
	private String path;
	
	/**
	 * Instantiates a new OpenNLPTokenizationFG
	 *
	 * @param path the path to the tokenization model
	 * @throws InvalidFormatException the invalid format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public OpenNLPTokenizationFG(String path) throws InvalidFormatException, IOException{
		this.path=path;
		InputStream modelIn = new FileInputStream(path);
		TokenizerModel tokModel = new TokenizerModel(modelIn);
		modelIn.close();
		tokenizer = new TokenizerME(tokModel);
	}
	
	private Span[] convertSpans(opennlp.tools.util.Span[] spans){
		int size=spans.length;
		Span[] result = new Span[size];
		for(int i=0; i<size; i++){
			result[i]= new Span(spans[i]);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.SentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.Sentence)
	 */
	@Override
	protected SequenceFS<Span> extractFeatures(Sentence sentence) {
		String sentenceValue = sentence.getValue();
		Span[] tokenSpans = convertSpans(tokenizer.tokenizePos(sentenceValue));
		
		return new SequenceFS<Span>(tokenSpans);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.SentenceFeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> retrieveRequiredFeatureGenerators() {
		return new ArrayList<FeatureGenerator>();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		out.writeObject(path);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		path=(String) in.readObject();
		InputStream modelIn = new FileInputStream(path);
		TokenizerModel tokModel = new TokenizerModel(modelIn);
		modelIn.close();
		tokenizer = new TokenizerME(tokModel);
	}
}
