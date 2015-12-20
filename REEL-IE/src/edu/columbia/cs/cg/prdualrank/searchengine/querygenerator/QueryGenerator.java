package edu.columbia.cs.cg.prdualrank.searchengine.querygenerator;

import edu.columbia.cs.cg.pattern.prdualrank.SearchPattern;
import edu.columbia.cs.ref.model.Document;
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
 * Generation of queries for specific search Engines. The implementation of the <b>Search Engines</b> and <b>Query Generator</b> used must correspond in their implementation to obtain the
 * expected results. To understand how the queries are generated please refer to <b>Algorithm PatternSearch(To,S,E)</b> in Figure 9 on Section 5 of the paper mentioned here.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public abstract class QueryGenerator<Q> {

	/**
	 * Generate query based on a relationship (tuple)
	 *
	 * @param relationship the relationship
	 * @return the q
	 */
	public abstract Q generateQuery(Relationship relationship);

	/**
	 * Generate query based on a specific entity
	 *
	 * @param role the role
	 * @return the q
	 */
	public abstract Q generateQuery(Entity role);

	/**
	 * Generate query based on a pattern that matches documents (i.e. Search Patterns)
	 *
	 * @param pattern the pattern
	 * @return the q
	 */
	public abstract Q generateQuery(SearchPattern<Document, TokenizedDocument> pattern);

}
