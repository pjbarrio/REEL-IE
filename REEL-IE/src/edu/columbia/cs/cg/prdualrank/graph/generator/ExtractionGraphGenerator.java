/**
 * Generates a graph, based on the extraction patterns and based on the assumption that extraction patterns only match the tuples whose
 * context match the pattern.
 * <br>
 * Different type of extraction patterns can be defined. For instance, an <b>Extraction Pattern</b> "COMPANY was acquired by BUYER", matches the tuple context: "You Tube was acquired by Google"
 *
 * <br>
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * <br>
 * For further information, 
 * 
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */
package edu.columbia.cs.cg.prdualrank.graph.generator;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.pattern.Pattern;
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
 * Generates a graph, based on the extraction patterns and based on the assumption that extraction patterns only match the tuples whose
 * context match the pattern.
 * <br>
 * Different type of extraction patterns can be defined. For instance, an <b>Extraction Pattern</b> "COMPANY was acquired by BUYER", matches the tuple context: "You Tube was acquired by Google"
 * 
 * <br>
 * For more information about Graphs, see <b>Section 4</b> of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class ExtractionGraphGenerator<T extends Relationship, D extends TokenizedDocument> extends GraphGenerator<Relationship,TokenizedDocument>{

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.graph.generator.GraphGenerator#findTuples(java.util.Set, edu.columbia.cs.cg.pattern.Pattern)
	 */
	@Override
	protected Map<Relationship, Integer> findTuples(Set<TokenizedDocument> documents,
			Pattern<Relationship,TokenizedDocument> pattern) {
		
		Map<Relationship, Integer> relationshipMap = new HashMap<Relationship, Integer>();
				
		for (TokenizedDocument document : documents) {
			
			List<Relationship> tuples = pattern.findMatch(document);
			
			for (Relationship relationship : tuples) {
				
				Integer freq = relationshipMap.get(relationship);
				
				if (freq == null){
					freq = 0;
				}
				
				relationshipMap.put(relationship, freq + 1);
				
			}

		}
			
		return relationshipMap;
	
	}

}
