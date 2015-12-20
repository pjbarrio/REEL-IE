package edu.columbia.cs.cg.prdualrank.searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.davidsoergel.conja.Function;
import com.davidsoergel.conja.Parallel;

import edu.columbia.cs.cg.prdualrank.searchengine.downloader.Downloader;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.tool.loader.document.DocumentLoader;
import edu.columbia.cs.ref.tool.loader.document.impl.RawDocumentLoader;
import edu.columbia.cs.ref.tool.preprocessor.Preprocessor;

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
 * Abstract class that provides the behavior of any Web search engine used in PRdualRank.
 * 
 * <br> 
 * This class is abstract given that many implementation of WebBasedSearchEngines are available:
 * 1.<a href="http://developer.yahoo.com/search/boss/">Yahoo Boss API</a>. Please read usage terms.
 * 2.<a href="http://msdn.microsoft.com/en-us/library/dd900818.aspx">Bing API</a>. Please read usage terms.
 * 
 * <br><br>
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

public abstract class WebBasedSearchEngine implements SearchEngine {

	private RawDocumentLoader loader;

	/**
	 * Instantiates a new web based search engine using the instance of the loader to be used to process the documents 
	 * from the web.
	 *
	 * @param loader the loader
	 */
	public WebBasedSearchEngine(RawDocumentLoader loader){
		this.loader = loader;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.prdualrank.searchengine.SearchEngine#search(java.lang.String, int)
	 */
	@Override
	public List<Document> search(String query, int k_seed) {
		
		System.out.println("searching: " + query);
		
		URL[] documentResults = new URL[k_seed];
		
		List<Thread> threadPool = generateQueryThreadPool(k_seed, query,documentResults);
		
		join(threadPool);
		
		List<URL> urls = asList(documentResults);
		
		System.out.println(urls.size());
		
		Map<URL,String> urlContentMap = new HashMap<URL, String>();
		
		List<Thread> downloaderPool = generateDownloaderPool(urls,urlContentMap);
		
		join(downloaderPool);
		
		return loadDocuments(urls,urlContentMap);

	}

	private List<Document> loadDocuments(List<URL> urls, Map<URL,String> urlContentMap) {
		
		List<Document> ret = new ArrayList<Document>();
		
		System.out.print("\nLoading");
		
		for (URL url : urls) {
			
			String document = urlContentMap.get(url);
			
			if (document != null && !document.isEmpty()){
				System.out.print(".");
				
				Document d = loader.load(document);
				
				d.setPath(url.getHost());
				
				d.setFilename(url.getFile());
				
				ret.add(d);
			} else {
				System.out.println("\nEMPTY: " + url);
			}
			
		}
		
		System.out.println("");

		return ret;
		
	}

	private List<Thread> generateDownloaderPool(List<URL> urls, Map<URL, String> urlContentMap) {
		
		List<Thread> threadPool = new ArrayList<Thread>();
		
		for (URL url : urls) {
			
			Thread t = new Thread(new Downloader(url, urlContentMap));
			
			threadPool.add(t);
			
			t.start();
			
		}
		
		return threadPool;
	}

	private List<URL> asList(URL[] documentResults) {
		
		List<URL> urls = new ArrayList<URL>();
		
		for (int i = 0; i < documentResults.length; i++) {
			
			if (documentResults[i] != null)
				urls.add(documentResults[i]);
			
		}
		
		return urls;
	}

	private void join(List<Thread> threadPool) {
		
		for (Thread thread : threadPool) {
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	private List<Thread> generateQueryThreadPool(int k_seed, String query, URL[] documentResults) {

		int start = 0;
		
		ArrayList<Thread> threadPool = new ArrayList<Thread>();

		while (start < k_seed){

			int count = getNumberOfResultsPerThread();

			executeQuery(threadPool,getRunnableQueryExecutor(query,start,Math.min(count, k_seed),documentResults));

			start += count;

		}

		return threadPool;
	}

	private void executeQuery(List<Thread> threadPool,Runnable runnableQueryExecutor) {
		
		Thread t = new Thread(runnableQueryExecutor);
		
		t.start();
		
		threadPool.add(t);
		
	}

	/**
	 * Gets the query executor runnable object that interacts with the web api.
	 *
	 * @param query the query
	 * @param start the index of the first result to be retrieved
	 * @param count the number of documents to be retrieved.
	 * @param documentResults the array where the url of the results are going to be stored. First used index in the array is start.
	 * @return the query executor runnable object.
	 */
	protected abstract Runnable getRunnableQueryExecutor(String query, int start, int count, URL[] documentResults);

	/**
	 * Gets the maximum number of results allowed per thread. Some web apis do not allow to retrieve more than a certain
	 * number of documents after submitting a query.
	 *
	 * @return the number of documents to be retrieved per thread.
	 */
	protected abstract int getNumberOfResultsPerThread();
	
}
