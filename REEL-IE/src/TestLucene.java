import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import edu.columbia.cs.cg.prdualrank.index.analyzer.TokenBasedAnalyzer;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;
import edu.columbia.cs.ref.tool.tokenizer.impl.OpenNLPTokenizer;
import edu.columbia.cs.utils.Words;


public class TestLucene {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Tokenizer tokenizer = new OpenNLPTokenizer("en-token.bin");
		
		Words.initialize(new File("data/stopWords.txt"), null);
		
		Set<String> stopW = Words.getStopWords();
		
		Analyzer myAnalyzer = new TokenBasedAnalyzer(stopW);
		
		try {
		
			RAMDirectory idx = new RAMDirectory();
			
			IndexWriterConfig indexWriterConfiguration = new IndexWriterConfig(Version.LUCENE_34, myAnalyzer);

			IndexWriter indexWriter = new IndexWriter(idx,indexWriterConfiguration);
			
			indexWriter.addDocument(createDocument("Theodore Roosevelt",
	                "It behooves every man to remember that the work of the " +
	                "critic, is of altogether secondary importance, and that, " +
	                "in the end, progress is accomplished by the man who does " +
	                "things and achievements."));

			indexWriter.optimize();
            indexWriter.close();

            indexWriter = new IndexWriter(idx, indexWriterConfiguration);
            
            indexWriter.addDocument(createDocument("Friedrich Hayek",
                    "The case for individual freedom rests largely on the " +
                    "recognition of the inevitable and universal ignorance " +
                    "of all of us concerning a great many of the factors on " +
                    "which the achievements of our ends and welfare depend."));

			indexWriter.optimize();
            indexWriter.close();
            
            IndexSearcher searcher = new IndexSearcher(idx);

            // Run some queries
            search(searcher, new String[]{"accomplished"},myAnalyzer);
            
            searcher.close();
			
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void search(IndexSearcher searcher, String[] queryString, Analyzer analyzer)
	        throws ParseException, IOException {

			PhraseQuery pq = new PhraseQuery();
		
	        // Build a Query object
			for (int i = 0; i < queryString.length; i++) {
				pq.add(new Term("content",queryString[i]));
			}
		
	        TopDocs d = searcher.search(pq,5);
	        
	        for (int i = 0; i < d.scoreDocs.length; i++) {
				
	        	System.out.println(i + " - " + d.scoreDocs[i].toString());
	        	System.out.println(searcher.doc(d.scoreDocs[i].doc));
			}
	        
	}

	
	private static Document createDocument(String title, String content) {
		
		Document doc = new Document();

        // Add the title as an unindexed field...
        doc.add(new Field("title",title,Store.YES,Index.NO));

        // ...and the content as an indexed field. Note that indexed
        // Text fields are constructed using a Reader. Lucene can read
        // and index very large chunks of text, without storing the
        // entire content verbatim in the index. In this example we
        // can just wrap the content string in a StringReader.
        doc.add(new Field("content", content,Store.YES,Index.ANALYZED));

        return doc;

		
	}

}
