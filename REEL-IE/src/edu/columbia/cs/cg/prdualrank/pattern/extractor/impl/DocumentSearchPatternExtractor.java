package edu.columbia.cs.cg.prdualrank.pattern.extractor.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.columbia.cs.cg.prdualrank.pattern.extractor.SearchPatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.resource.TupleContext;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;

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
 * This class defines the behavior of the generation of search pattern as stated in <b>Algorithm PatternSearch(To,S,E)</b> in Figure 9 on Section 5 and <b>Definition 1</b> in Section 3.1.
 * 
 * <br><br>
 * For an efficient implementation (different to what is described in the paper, but discussed with the authors) refer to {@link WindowedSearchPatternExtractor}.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @see WindowedSearchPatternExtractor
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class DocumentSearchPatternExtractor<T extends Document> extends SearchPatternExtractor<Document> {

	
	/**
	 * Instantiates a new document search pattern extractor.
	 *
	 * @param ngram the maximum size of ngrams to be calculated in order to generate the search patterns. 
	 * @param numberOfPhrases the maximum number of ngrams to be combined in the search pattern generation.
	 */
	public DocumentSearchPatternExtractor(int ngram, int numberOfPhrases){
		
		super(ngram,numberOfPhrases);
		
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor#getTupleContexts(edu.columbia.cs.cg.document.TokenizedDocument, edu.columbia.cs.cg.relations.Relationship, java.util.List)
	 */

	@Override
	protected List<TupleContext> getTupleContexts(
			TokenizedDocument document, Relationship relationship,
			List<Relationship> matchingRelationships) {
		
		List<Span> tupleSpans = new ArrayList<Span>();
		
		for (Relationship matchingRelationship : matchingRelationships) {

			for (String role : matchingRelationship.getRoles()) {

				Entity entity = matchingRelationship.getRole(role);
				
				if (entity != Entity.NULL_ENTITY){
					//update indexes.
					
					Span auxSpan = document.getEntitySpan(entity);
					
					tupleSpans.add(auxSpan); //return the indexes whitin the array
					
				}
				
			}
			
		}
		
		if (tupleSpans.isEmpty())
			return new ArrayList<TupleContext>(0);
		
		List<Span> realSpans = Span.calculateNotOverlappingSpans(tupleSpans);

		TupleContext tc = new TupleContext(realSpans);
		
		String[] first = Arrays.copyOfRange(document.getTokenizedString(), 0, realSpans.get(0).getStart());
		
		tc.addWords(first);
		
		for (int i = 0; i < realSpans.size() - 1; i++) {
			
			String[] middle = Arrays.copyOfRange(document.getTokenizedString(), realSpans.get(i).getEnd()+1, Math.max(realSpans.get(i).getEnd(), realSpans.get(i+1).getStart()));
		
			tc.addWords(middle);
			
		}
		
		String[] last = Arrays.copyOfRange(document.getTokenizedString(), Math.min(realSpans.get(realSpans.size()-1).getEnd()+1, document.getTokenizedString().length-1), document.getTokenizedString().length);
		
		tc.addWords(last);
		
		List<TupleContext> ret = new ArrayList<TupleContext>(1);
		
		ret.add(tc);
		
		return ret;
		
	}

}
