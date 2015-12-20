package edu.columbia.cs.cg.prdualrank.searchengine.impl;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.code.bing.search.client.BingSearchClient;
import com.google.code.bing.search.client.BingSearchServiceClientFactory;
import com.google.code.bing.search.client.BingSearchClient.SearchRequestBuilder;
import com.google.code.bing.search.schema.AdultOption;
import com.google.code.bing.search.schema.SearchOption;
import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.SourceType;
import com.google.code.bing.search.schema.web.WebResult;
import com.google.code.bing.search.schema.web.WebSearchOption;

import edu.columbia.cs.cg.prdualrank.searchengine.WebBasedSearchEngine;
import edu.columbia.cs.ref.tool.loader.document.impl.RawDocumentLoader;

/**
 * <br>
 * For this Class, <a href="http://msdn.microsoft.com/en-us/library/dd900818.aspx">Bing API</a> is required. 
 *  
 * <br>
 * 
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * For further information, <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>.
 * 
 * <br><br>
 * 
 * <b>Description</b><br><br>
 * 
 * Search Engine used to issue queries to Bing Search Engine. The user has to write its own API key.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd900818.aspx"> Bing API </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class BingSearchEngine extends WebBasedSearchEngine {

	/**
	 * Instantiates a new bing search engine.
	 *
	 * @param loader the loader
	 */
	public BingSearchEngine(RawDocumentLoader loader) {
		super(loader);
	}

	private final String API_KEY = "B47E474EC2280B209E026F084E6D8F3E520968FC";
	
	private class BingQuerier implements Runnable{

		private URL[] documentResults;
		private int start;
		private int count;
		private String query;

		public BingQuerier(String query, int start, int count,
				URL[] documentResults) {
			this.documentResults = documentResults;
			this.start = start;
			this.count = count;
			this.query = query;
		}

		@Override
		public void run() {
			
			BingSearchServiceClientFactory factory = BingSearchServiceClientFactory.newInstance();
			BingSearchClient client = factory.createBingSearchClient();
			
			SearchRequestBuilder builder = client.newSearchRequestBuilder();
			builder.withAppId(API_KEY);
			builder.withQuery(query);
			builder.withSourceType(SourceType.WEB);
			builder.withVersion("2.0");
			builder.withMarket("en-us");
			builder.withAdultOption(AdultOption.OFF);
			builder.withSearchOption(SearchOption.DISABLE_LOCATION_DETECTION);
			builder.withWebRequestCount((long)count);
			builder.withWebRequestOffset((long)start);
			
			builder.withWebRequestSearchOption(WebSearchOption.DISABLE_HOST_COLLAPSING);
			builder.withWebRequestSearchOption(WebSearchOption.DISABLE_QUERY_ALTERATIONS);

			SearchResponse response = client.search(builder.getResult());
			
			long total = response.getWeb().getTotal();
			
			if (total == 0 || start > total - 1) 
				return;
			
			int resultIndex = start;
			
			for (WebResult res : response.getWeb().getResults()) {
		        
				try {
					documentResults[resultIndex] = new URL(res.getUrl());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				resultIndex++;
				
			}
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.searchengine.WebBasedSearchEngine#getRunnableQueryExecutor(java.lang.String, int, int, java.net.URL[])
	 */
	@Override
	protected Runnable getRunnableQueryExecutor(String query, int start,
			int count, URL[] documentResults) {
		return new BingQuerier(query,start,count,documentResults);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.searchengine.WebBasedSearchEngine#getNumberOfResultsPerThread()
	 */
	@Override
	protected int getNumberOfResultsPerThread() {
		return 50;
	}

	

}
