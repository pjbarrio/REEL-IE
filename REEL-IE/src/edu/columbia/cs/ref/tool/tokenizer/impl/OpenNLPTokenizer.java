package edu.columbia.cs.ref.tool.tokenizer.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;

/**
 * The OpenNLPTokenizer is an implementation of the Tokenizer interface that uses
 * OpenNLP models to split the text into tokens.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenNLPTokenizer implements Tokenizer {

	/** The tokenizer. */
	private TokenizerME tokenizer = null;	
	
	/** The path to tokenizer. */
	private String pathToTokenizer;
	
	/**
	 * Instantiates a new OpenNLP tokenizer. It receives as input the path to the
	 * model.
	 *
	 * @param path the path
	 */
	public OpenNLPTokenizer(String path){
		pathToTokenizer=path;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.document.tokenized.tokenizer.Tokenizer#tokenize(java.lang.String)
	 */
	@Override
	public Span[] tokenize(String text) {
		
		Span[] ret = convert(getTokenizer().tokenizePos(text));
		
		return ret;
	}

	private Span[] convert(opennlp.tools.util.Span[] tokenizePos) {
		
		Span[] ret = new Span[tokenizePos.length];
		
		for (int i = 0; i < ret.length; i++) {
			
			ret[i] = new Span(tokenizePos[i]);
			
		}
		
		return ret;
	}

	private TokenizerME getTokenizer() {
		
		if (tokenizer == null){
			
			InputStream modelIn;
			
			try {
				
				modelIn = new FileInputStream(pathToTokenizer);
				
				TokenizerModel tokModel = new TokenizerModel(modelIn);
				
				modelIn.close();
				
				tokenizer = new TokenizerME(tokModel);
			
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

		return tokenizer;
	}

	
}
