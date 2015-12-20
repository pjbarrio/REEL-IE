package edu.columbia.cs.cg.prdualrank.index;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import edu.columbia.cs.cg.prdualrank.index.analyzer.TokenBasedAnalyzer;
import edu.columbia.cs.ref.model.TokenizedDocument;

/**
 * For this Class, <a href="http://lucene.apache.org/">Apache Lucene Engine</a> is required. An instance of {@link IndexWriter} and an instance
 * of {@link IndexSearcher} are used.
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
 * Apache Lucene Indexer and Searcher. Used for optimal matching of the search patterns. <br>
 * Read <b>Algorithm PatternSearch(To,S,E)</b> in Figure 9, Section 5 of the mentioned paper for more detailed information.
 * 
 * <br>
 * @see <a href="http://lucene.apache.org/"> Apache Lucene Engine </a>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */


public class Index {

	private static final String PATH = "path";
	private static final String FILE_NAME = "filename";
	public static final String CONTENT = "content";
	
	private IndexWriter indexWriter;
	private RAMDirectory idx;
	private TokenBasedAnalyzer myAnalyzer;
	private boolean lowercase;
	private Set<String> stopWords;
	private IndexSearcher IndexSearcher;
	private Map<String, TokenizedDocument> documentMap;

	/**
	 * Instantiates a new index.
	 *
	 * @param myAnalyzer the analyzer to be used to index the content.
	 * @param lowercase specifies if the content will be stored in lowercase. No match case will be allowed if true.
	 * @param stopWords the set stop words. Empty set if no stop words are considered.
	 */
	public Index(TokenBasedAnalyzer myAnalyzer,boolean lowercase, Set<String> stopWords) {

		//Index Creation

		this.lowercase = lowercase;
		
		this.stopWords = stopWords;
		
		this.myAnalyzer = myAnalyzer;
		
		idx = new RAMDirectory();

		IndexWriterConfig indexWriterConfiguration = new IndexWriterConfig(Version.LUCENE_34, myAnalyzer);

		indexWriter = null;

		try {

			indexWriter = new IndexWriter(idx,indexWriterConfiguration);

		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Adds the document to the index.
	 *
	 * @param document the document to be indexed.
	 */
	public void addDocument(TokenizedDocument document) {
		
		try {
			indexWriter.addDocument(createDocument(document),myAnalyzer);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private Document createDocument(TokenizedDocument document) {
		
		Document doc = new Document();
		
		doc.add(new Field(PATH,document.getPath(),Store.YES,org.apache.lucene.document.Field.Index.NO));
		
		doc.add(new Field(FILE_NAME,document.getFilename(),Store.YES,org.apache.lucene.document.Field.Index.NO));
		
		doc.add(new Field(CONTENT,myAnalyzer.getReader(document.getTokenizedSpans(),document.getTokenizedString())));
		
		save(createKey(document.getPath(),document.getFilename()),document);
		
		return doc;
		
	}

	private void save(String key, TokenizedDocument document) {
		
		getDocumentTable().put(key,document);
		
	}

	private Map<String,TokenizedDocument> getDocumentTable() {
		
		if (documentMap == null){
			documentMap = new HashMap<String, TokenizedDocument>();
		}
		return documentMap;
	}

	private String createKey(String path, String fileName) {
		return path+"-"+fileName;
	}

	/**
	 * Closes and optimize the index.
	 */
	public void close() {
		
		try {
			indexWriter.optimize();
			
			indexWriter.close();
			
			IndexSearcher = new IndexSearcher(idx);
			
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Search the query in the index.
	 *
	 * @param query the query to be issued
	 * @param n the number of documents to be retrieved.
	 * @return the list of documents that match the query.
	 */
	public List<edu.columbia.cs.ref.model.TokenizedDocument> search(Query query, int n) {

		TopDocs result = null;
		
		try {
			result = IndexSearcher.search(query, n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<TokenizedDocument> searchResults = new ArrayList<TokenizedDocument>();
		
		ScoreDoc[] docs = result.scoreDocs;
		
		for (int i = 0; i < docs.length; i++) {
			
			Document document;
			try {
				document = IndexSearcher.doc(docs[i].doc);
				searchResults.add(getDocumentTable().get(createKey(document.get(PATH), document.get(FILE_NAME))));
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
		return searchResults;
		
	}

}
