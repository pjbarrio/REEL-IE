package edu.columbia.cs.cg.prdualrank.pattern.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.columbia.cs.cg.pattern.prdualrank.SearchPattern;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.DocumentSearchPatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.WindowedSearchPatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.resource.TupleContext;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.utils.NAryCartesianProduct;

/**
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * For further information, <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>.
 * 
 * <br><br>
 * 
 * <b>Description</b><br><br>
 * 
 * Gives the interface for any search pattern extractor in PRDualRank. The word "any" relies on all the different kind of <b>Search Patterns</b> that can be implemented. 
 * <br> Two of these <b>Search Patterns</b> can be seen in {@link DocumentSearchPatternExtractor} and {@link WindowedSearchPatternExtractor}.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>
 * @see DocumentSearchPatternExtractor
 * @see WindowedSearchPatternExtractor 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public abstract class SearchPatternExtractor<T extends Document> implements PatternExtractor<Document> {
	
	private int ngram;
	private int numberOfPhrases;

	public SearchPatternExtractor(int ngram, int numberOfPhrases) {
		this.ngram = ngram;
		this.numberOfPhrases = numberOfPhrases;
	}

	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor#extractPatterns(edu.columbia.cs.cg.document.TokenizedDocument, edu.columbia.cs.cg.relations.Relationship, java.util.List)
	 */

	@Override
	public Map<Pattern<Document, TokenizedDocument>, Integer> extractPatterns(
			TokenizedDocument document, Relationship relationship,
			List<Relationship> matchingRelationships) {
		
		List<TupleContext> tcs = getTupleContexts(document,relationship,matchingRelationships);
		
		return generateSearchPatterns(tcs, ngram, numberOfPhrases);
		
	}

	protected abstract List<TupleContext> getTupleContexts(TokenizedDocument document,
			Relationship relationship, List<Relationship> matchingRelationships);

	
	private Map<Pattern<Document,TokenizedDocument>, Integer> generateSearchPatterns(
			List<TupleContext> contexts, int ngram, int numberOfPhrases) {
		
		Set<String[]> ngrams = new HashSet<String[]>();
		
		for (TupleContext tupleContext : contexts) {
						
			Set<String[]> nGrams = tupleContext.generateNgrams(ngram);
			
			for (String[] nGram : nGrams) {
				
				if (SearchPattern.isPatternizable(nGram)){
					
					ngrams.add(nGram);
				}
			}
			
		}
		
		Map<Pattern<Document,TokenizedDocument>,Integer> searchPatterns = new HashMap<Pattern<Document,TokenizedDocument>, Integer>();
		
		Map<Integer,Set<String[]>> ngramsMap = new HashMap<Integer, Set<String[]>>();
		
		for (int i = 1; i <= numberOfPhrases; i++) {
			
			ngramsMap.put(i, ngrams);
			
			List<Map<Integer,String[]>> patternwords = NAryCartesianProduct.generateAllPossibilities(ngramsMap);
			
			for (Map<Integer, String[]> patternsWord : patternwords) {
				
				List<String[]> words = new ArrayList<String[]>(patternsWord.values());
				
				SearchPattern<Document,TokenizedDocument> sp = new SearchPattern<Document,TokenizedDocument>(words); 
				
				if (sp.isValid()){
					
					updateMap(searchPatterns,sp);
					
				}
				
			}
						
		}
		
		return searchPatterns;
		
	}

	private void updateMap(Map<Pattern<Document,TokenizedDocument>, Integer> searchPatterns,
			SearchPattern<Document,TokenizedDocument> sp) {
		
		Integer freq = searchPatterns.get(sp);
		
		if (freq == null){
			freq = 0;
		}
		
		searchPatterns.put(sp, freq + 1);
		
	}

	
	
}
