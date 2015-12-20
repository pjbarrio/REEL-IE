package edu.columbia.cs.ref.tool.chunker.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.columbia.cs.ref.tool.chunker.Chunker;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

/**
 * The Class OpenNLPChunker is an implementation of a Chunker that uses
 * the ChunkerME models from <a href="http://incubator.apache.org/opennlp/">OpenNLP</a>.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenNLPChunker implements Chunker {

	/** The chunker. */
	private ChunkerME chunker = null;	
	
	/** The path to tokenizer. */
	private String pathToTokenizer;
	
	/**
	 * Instantiates a new OpenNLP chunker.
	 *
	 * @param path the path to the model to be used.
	 */
	public OpenNLPChunker(String path){
		pathToTokenizer=path;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.tool.chunker.Chunker#chunk(java.lang.String[], java.lang.String[])
	 */
	@Override
	public String[] chunk(String[] toks, String[] tags) {
		return getChunker().chunk(toks, tags);
	}
	
	/**
	 * Gets the chunker.
	 *
	 * @return the chunker
	 */
	private ChunkerME getChunker() {
		
		if (chunker == null){
			
			InputStream modelIn;
			
			try {
				
				ChunkerModel modelModel=null;
				modelIn = new FileInputStream(pathToTokenizer);
				modelModel = new ChunkerModel(modelIn);
				modelIn.close();
				chunker  = new ChunkerME(modelModel);
			
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

		return chunker;
	}

}
