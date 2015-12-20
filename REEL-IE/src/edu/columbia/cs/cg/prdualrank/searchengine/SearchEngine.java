package edu.columbia.cs.cg.prdualrank.searchengine;

import java.util.List;

import edu.columbia.cs.ref.model.Document;

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
 * This interface gives the expected behavior of a Search Engine. <br><br>
 * 
 * The search engine is used in the <b>Algorithm PatternSearch(To,S,E)</b> in Figure 9 on Section 5 of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public interface SearchEngine {

	/**
	 * Issues query into the search engine and retrieves either k_seed or all the documents in the result set, whichever number is the lower.  
	 *
	 * @param query the query to be issued
	 * @param k_seed the number of documents to be retrieved if lower than the total number of hits. All the results are retrieved otherwise.
	 * @return the list of documents contained in the result set.
	 */
	public List<Document> search(String query, int k_seed);

}
