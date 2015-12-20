/**
 * This class defines the behavior of the generation of search pattern as discussed with one of the authors of PRDualRank: <a href="mailto:fang2@illinois.edu"><b>Yuan Fang</b></a>.
 * <br>
 * Notice that it differs from the one described in the paper on the <b>Universe of words</b> used to generate the <b>Search Patterns</b>.
 * 
 * <br>
 * In this case, a window around the tuples is used. However, in the paper, such window is not described.
 * 
 * <br>
 * According to the authors, this approach was used to run the experiments to obtain better performance (in time) of the results. Processing all the words in a document is computationally expensive.
 * 
 * <br>
 * For the original implementation, please see the DocumentSearchPatternExtractor class. Consider that the execution time will increase considerably compared to this implementation.
 * 
 * <br>
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * <br>
 * For further information, 
 * 
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */
package edu.columbia.cs.cg.prdualrank.pattern.extractor.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * This class defines the behavior of the generation of search pattern as discussed with one of the authors of PRDualRank: <a href="mailto:fang2@illinois.edu"><b>Yuan Fang</b></a>.
 * <br>
 * Notice that it differs from the one described in the paper on the <b>Universe of words</b> used to generate the <b>Search Patterns</b>.
 * 
 * <br>
 * In this case, a window around the tuples is used. However, in the paper, such window is not described.
 * 
 * <br>
 * According to the authors, this approach was used to run the experiments to obtain better performance (in time) of the results. Processing all the words in a document is computationally expensive.
 * 
 * <br>
 * For the original implementation, please see the DocumentSearchPatternExtractor class. Consider that the execution time will increase considerably compared to this implementation.
 * 
 * <br><br>
 * No definition can be pointed out to the paper due to implementation details described above.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>
 * @see DocumentSearchPatternExtractor 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class WindowedSearchPatternExtractor<T extends Document> extends SearchPatternExtractor<Document> {

	private int window;

	/**
	 * Instantiates a new windowed search pattern extractor.
	 *
	 * @param window the number of words around the tuple to be used to generate the search patterns.
	 * @param ngram the maximum size of ngrams to be calculated in order to generate the search patterns. 
	 * @param numberOfPhrases the maximum number of ngrams to be combined in the search pattern generation.
	 */
	public WindowedSearchPatternExtractor(int window, int ngram, int numberOfPhrases) {
		super(ngram,numberOfPhrases);
		this.window = window;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor#getTupleContexts(edu.columbia.cs.cg.document.TokenizedDocument, edu.columbia.cs.cg.relations.Relationship, java.util.List)
	 */
	@Override
	protected List<TupleContext> getTupleContexts(TokenizedDocument document,
			Relationship relationship, List<Relationship> matchingRelationships) {
		
		//find the first index, find the last one and make a window around it.
		
		List<TupleContext> contexts = new ArrayList<TupleContext>();
		
		for (Relationship matchingRelationship : matchingRelationships) {

			List<Span> tupleSpans = new ArrayList<Span>();
			
			for (String role : matchingRelationship.getRoles()) {

				Entity entity = matchingRelationship.getRole(role);
				
				if (entity != Entity.NULL_ENTITY){
					//update indexes.
					
					Span auxSpan = document.getEntitySpan(entity);
					
					tupleSpans.add(auxSpan); //return the indexes whitin the array
					
				}
				
			}
			
			if (tupleSpans.size() == 0)
				continue;
			
			List<Span> realSpans = Span.calculateNotOverlappingSpans(tupleSpans);

			Collections.sort(realSpans);
			
			TupleContext tc = new TupleContext(realSpans);
			
			String[] first = Arrays.copyOfRange(document.getTokenizedString(), Math.max(0, realSpans.get(0).getStart() - window), realSpans.get(0).getStart());
			
			tc.addWords(first);
			
			for (int i = 0; i < realSpans.size() - 1; i++) {
				
				String[] middle = Arrays.copyOfRange(document.getTokenizedString(), realSpans.get(i).getEnd()+1, Math.max(realSpans.get(i).getEnd(), realSpans.get(i+1).getStart()));
			
				tc.addWords(middle);
				
			}
			
			String[] last = Arrays.copyOfRange(document.getTokenizedString(), Math.min(realSpans.get(realSpans.size()-1).getEnd()+1, document.getTokenizedString().length-1), Math.min(realSpans.get(realSpans.size()-1).getEnd()+window, document.getTokenizedString().length));
			
			tc.addWords(last);
			
			contexts.add(tc);
		}
		
		return contexts;
		
	}


}
