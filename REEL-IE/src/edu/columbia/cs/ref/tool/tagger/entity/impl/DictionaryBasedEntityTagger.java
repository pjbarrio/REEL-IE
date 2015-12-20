package edu.columbia.cs.ref.tool.tagger.entity.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Segment;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tagger.entity.EntityTagger;
import edu.columbia.cs.ref.tool.tagger.entity.impl.resources.InstanceBasedTokenizedFactory;
import edu.columbia.cs.ref.tool.tagger.span.impl.EntitySpan;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;
import edu.columbia.cs.utils.Dictionary;

/**
 * The DictionaryBasedEntityTagger extends EntityTagger and corresponds to an implementation
 * of the Aho-Corasick dictionary matching algorithm described in <b> "Efficient string matching:
 * An aid to bibliographic search" </b>. A. Aho and M. Corasick. In Communications of the ACM 18 (6),
 * pages 333-340, June 1975.
 * 
 * <br>
 * <br>
 * 
 * This class uses the implementation of Aho-Corasick from 
 * <a href="http://alias-i.com/lingpipe/">Lingpipe</a> which uses information from the
 * tokenization in the matching.
 * 
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class DictionaryBasedEntityTagger extends EntityTagger<EntitySpan,Entity> {

	/** The Constant CHUNK_SCORE. This is actually not used since we do not consider
	 *  the confidence of the annotator
	 */
	private static final double CHUNK_SCORE = 1.0;
	
	/** The number of documents. Used to generate the ids. */
	private int processedDocuments;
	
	/** The dictionary. */
	private MapDictionary<String> dictionary;
	
	/** The matcher. */
	private ExactDictionaryChunker matcher;
	
	/** The tokenizer. */
	private Tokenizer tokenizer;
	private String tag;

	/**
	 * Instantiates a new dictionary based entity tagger.
	 *
	 * @param tag the tag to be assigned to entities that match the dictionary
	 * @param dictionary the dictionary
	 * @param tokenizer the tokenizer
	 */
	public DictionaryBasedEntityTagger(String tag, Dictionary dictionary, Tokenizer tokenizer){
		super(tag);
		this.tag = tag;
		processedDocuments = 0;
		this.tokenizer = tokenizer;
		createMatchingDictionary(dictionary);

	}

	/**
	 * Creates the Lingpipe representation of a dictionary using the our representation
	 * of a dictionary
	 *
	 * @param dict the dictionary used by this entity tagger
	 */
	private void createMatchingDictionary(Dictionary dict) {
		
		dictionary = new MapDictionary<String>();
		
		Set<String> entries = dict.getEntries();

		for (String entry : entries) {
			
			dictionary.addEntry(new DictionaryEntry<String>(entry,dict.getName(),CHUNK_SCORE));
			
		}
	
		matcher = new ExactDictionaryChunker(dictionary,getTokenizerFactory(),
                                     true,true);
	}

	/**
	 * Gets the tokenizer factory.
	 *
	 * @return the tokenizer factory
	 */
	private TokenizerFactory getTokenizerFactory() {
		
		return InstanceBasedTokenizedFactory.getInstance(tokenizer);
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.document.tagger.entity.EntityTagger#findSpans(edu.columbia.cs.cg.document.Document)
	 */
	@Override
	protected List<EntitySpan> findSpans(Document d) {
		
		processedDocuments++;
		
		List<EntitySpan> entitySpans = new ArrayList<EntitySpan>();
		
		int matches = 0;
		
		for (Segment segment : d.getPlainText()) {
			
			String content = segment.getValue();
			
			int offset = segment.getOffset();
			
			Chunking chunking = matcher.chunk(content);
	        
			for (Chunk chunk : chunking.chunkSet()) {
	            
				matches++;
				
	        	int start = chunk.start();
	            
	        	int end = chunk.end();
	            
	        	String value = content.substring(start,end);
	            
	            entitySpans.add(new EntitySpan(createId(processedDocuments,matches,chunk.type(),tag),tag, value, offset + start, value.length()));
	        
			}
					
		}
		
		return entitySpans;
		
	}

	/**
	 * Assigns an Id to the entity. The id is of the form:
	 * 
	 * idDocument-idEntityDoc-typeChunk-tagAnnotator
	 *
	 * @param idDocument the processed document
	 * @param idEntityDoc the matches
	 * @param typeChunk the type
	 * @param tagAnnotator the tag
	 * @return the id in the form "idDocument-idEntityDoc-typeChunk-tagAnnotator"
	 */
	private String createId(int idDocument, int dEntityDoc, String typeChunk, String tagAnnotator) {
		
		return idDocument + "-" + dEntityDoc + "-" + typeChunk + "-" + tagAnnotator;
		
	}


}
