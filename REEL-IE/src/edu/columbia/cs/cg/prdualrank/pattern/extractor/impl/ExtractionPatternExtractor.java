package edu.columbia.cs.cg.prdualrank.pattern.extractor.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.resource.AttributeContext;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

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
 * Class used to generate <b>Extraction Patterns</b> as described in <b>Algorithm PatternSearch(To,S,E)</b> in Figure 9 on Section 5 and <b>Definition 2</b> in Section 3.1.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class ExtractionPatternExtractor<T extends Relationship> implements PatternExtractor<Relationship> {

	private int individualPatternSize;
	private RelationshipType rType;
	private int span;
	
	/**
	 * Instantiates a new extraction pattern extractor.
	 *
	 * @param span Maximum distance (in words) in between attributes of a tuple
	 * @param individualPatternSize Maximum size of an extraction pattern, per attribute.
	 * @param rType The relationship type to be extracted.
	 */
	public ExtractionPatternExtractor(int span, int individualPatternSize, RelationshipType rType) {
		this.span = span;
		this.rType = rType;
		this.individualPatternSize = individualPatternSize;
	}

	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor#extractPatterns(edu.columbia.cs.cg.document.TokenizedDocument, edu.columbia.cs.cg.relations.Relationship, java.util.List)
	 */
	@Override
	public Map<Pattern<Relationship,TokenizedDocument>, Integer> extractPatterns(TokenizedDocument document,
			Relationship relationship, List<Relationship> matchingRelationships) {
		// TODO Auto-generated method stub
		
		//I receive only the relationships that are interesting for me.
		//they were also loaded in such
		
		Map<Pattern<Relationship,TokenizedDocument>,Integer> patterns = new HashMap<Pattern<Relationship,TokenizedDocument>, Integer>();
		
		for (Relationship tuple : matchingRelationships) {
			
			AttributeContext context = generateAttributeContexts(tuple,document);			
			
			updateMap(patterns,context.generateExtractionPatterns(individualPatternSize));
			
		}
		
		return patterns;
		
	}

	private void updateMap(Map<Pattern<Relationship,TokenizedDocument>, Integer> patterns,
			List<Pattern<Relationship,TokenizedDocument>> extractedPatterns) {
		
		for (Pattern<Relationship,TokenizedDocument> pattern : extractedPatterns) {
			
			Integer freq = patterns.get(pattern);
			
			if (freq == null){
				
				freq = 0;
				
			}
			
			patterns.put(pattern, freq + 1);
			
		}
		
	}

	private AttributeContext generateAttributeContexts(Relationship tuple,
			TokenizedDocument document) {
		
		AttributeContext ac = new AttributeContext(rType);
		
		for (String role : tuple.getRoles()) {
			
			Entity entity = tuple.getRole(role);
			
			if (Entity.NULL_ENTITY != entity){
				
				Span entitySpan = document.getEntitySpan(entity); //return the indexes whitin the array
				
				String[] before = Arrays.copyOfRange(document.getTokenizedString(), Math.max(0, entitySpan.getStart()-span), entitySpan.getStart());
				
				String[] after = Arrays.copyOfRange(document.getTokenizedString(), Math.min(entitySpan.getEnd()+1, document.getTokenizedString().length-1), Math.min(entitySpan.getEnd()+span, document.getTokenizedString().length));
				
				ac.addContext(entity,role,before,after);
				
			}
			
		}
		
		return ac;
		
	}

}
