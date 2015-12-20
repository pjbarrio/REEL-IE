package edu.columbia.cs.ref.tool.postagger.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.tool.postagger.POSTagger;

/**
 * The Class OpenNLPPOSTagger is an implementation of a POS tagger that uses
 * the POSTaggerME models from <a href="http://incubator.apache.org/opennlp/">OpenNLP</a>.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenNLPPOSTagger implements POSTagger {

	/** The tagger. */
	private POSTaggerME tagger = null;	
	
	/** The path to tokenizer. */
	private String pathToTokenizer;
	
	/**
	 * Instantiates a new OpenNLP POS tagger.
	 *
	 * @param path the path to the model to be used.
	 */
	public OpenNLPPOSTagger(String path){
		pathToTokenizer=path;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.tool.postagger.POSTagger#tag(java.lang.String[])
	 */
	@Override
	public String[] tag(String[] text) {
		return getPOSTagger().tag(text);
	}

	private POSTaggerME getPOSTagger() {
		
		if (tagger == null){
			
			InputStream modelIn;
			
			try {
				
				POSModel modelPOS=null;
				modelIn = new FileInputStream(pathToTokenizer);
				modelPOS = new POSModel(modelIn);
				modelIn.close();
				tagger = new POSTaggerME(modelPOS);
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (InvalidFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
						
		}

		return tagger;
	}

}
