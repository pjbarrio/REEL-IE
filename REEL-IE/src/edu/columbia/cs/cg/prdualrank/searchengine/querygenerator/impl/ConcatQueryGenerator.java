package edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.impl;

import java.util.Collection;

import com.google.gdata.util.NotImplementedException;

import edu.columbia.cs.cg.pattern.prdualrank.SearchPattern;
import edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.QueryGenerator;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;

/**
 * For this Class, <a href="http://lucene.apache.org/">Apache Lucene Engine</a> is required. 
 *  
 * <br>
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * For further information, <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>.
 * 
 * <br><br>
 * 
 * <b>Description</b><br><br>
 * 
 * Query generator based in the simple concatenation of the strings to be searched.
 * 
 * <br>For instance, for a <b>Relationship</b> (Microsoft Corporation, Redmond Seattle), the generated query will be: "Microsoft Corporation Redmond Seattle". 
 * 
 * <br><b>Comment</b>: Then, depending on how the search engine wants to divide
 * it, it is how the query is going to be issued. One possibility is to send it as boolean query where a document will be a hit, if it contains all the words in the query (order does not matter)
 * 
 * <br>For an <b>Entity</b> (Microsoft Corporation), the generated query will be: "Microsoft Corporation". To understand how the query is issued, please read the <b>Comment</b> above.
 * 
 * <br>
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class ConcatQueryGenerator extends QueryGenerator<String> {

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.QueryGenerator#generateQuery(edu.columbia.cs.cg.relations.Relationship)
	 */
	public String generateQuery(Relationship relationship) {
		
		String ret = "";
		
		Collection<String> roles = relationship.getRoles();
		
		if (roles.size() == 0)
			return "";
		
		for (String role : roles) {
			
			ret = ret + " " + relationship.getRole(role).getValue(); 
			
		}
		
		return ret.substring(1);
		
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.QueryGenerator#generateQuery(edu.columbia.cs.cg.relations.Entity)
	 */
	public String generateQuery(Entity role) {
		return role.getValue();
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.QueryGenerator#generateQuery(edu.columbia.cs.cg.pattern.prdualrank.SearchPattern)
	 */
	@Override
	public String generateQuery(
			SearchPattern<Document, TokenizedDocument> pattern) {
		// TODO Implement before trying over the internet
		return null;
	}
	
}
