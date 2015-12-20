package edu.columbia.cs.ref.tool.loader.document.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.loader.document.DocumentLoader;
import edu.columbia.cs.ref.tool.preprocessor.Preprocessor;
import edu.columbia.cs.ref.tool.segmentator.DocumentSegmentator;
import edu.columbia.cs.ref.tool.tagger.Tagger;

/**
 * Loader for a raw document
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class RawDocumentLoader extends DocumentLoader{

	/** The preprocessor. */
	private Preprocessor preprocessor;
	
	/** The taggers. */
	private Tagger[] taggers;
	
	/** The doc segmentator. */
	private DocumentSegmentator docSegmentator;

	/**
	 * Constructor of the loader
	 *
	 * @param relationshipTypes Represents the types of relationships to be extracted from the collection
	 * @param preprocessor Represents the preprocessor needed to load the raw documents
	 * @param docSegmentator Represents the segmentators needed to load the raw documents
	 * @param taggers Represents the entity tagger used to load the raw documents
	 */
	public RawDocumentLoader(Set<RelationshipType> relationshipTypes, Preprocessor preprocessor, DocumentSegmentator docSegmentator, Tagger...taggers) {
		super(relationshipTypes);
		this.preprocessor = preprocessor;
		this.taggers = taggers;
		this.docSegmentator = docSegmentator;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.tool.loader.document.DocumentLoader#load(java.io.File)
	 */
	@Override
	public Set<Document> load(File file) {
		
		throw new UnsupportedOperationException();
		
	}

	/**
	 * Method that loads a document from a reader
	 * 
	 * @param reader represents a reader from a given origin
	 * @return a document with the content from the input reader
	 */
	public Document load(Reader reader){

		BufferedReader br = new BufferedReader(reader);
		
		try {
			
			StringBuilder sb = new StringBuilder();
			
			String line = br.readLine();
			
			if (line!=null)
				sb.append(line);
			
			while ((line = br.readLine())!=null){
				
				sb.append("\n" + line);
				
			}
			
			br.close();
			
			return load(sb.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
		
	}

	/**
	 * Method that loads a document from a string
	 * 
	 * @param content represents a string with the content of a document
	 * @return a document with the content from the input string
	 */
	public Document load(String content){
		
		String preprocessedContent = preprocessor.process(content);
		
		Document d = new Document(docSegmentator.segmentate(preprocessedContent));
		
		for (Tagger tagger : taggers) {
			
			tagger.enrich(d);
			
		}
		
		return d;
	}

}
