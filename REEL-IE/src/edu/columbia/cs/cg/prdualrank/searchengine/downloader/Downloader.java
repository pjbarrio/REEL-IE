package edu.columbia.cs.cg.prdualrank.searchengine.downloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

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
 * Runnable class used to download documents from the web. It stores the specified URL in a Map together with its content.
 * <br>
 * It will try <b>ATTEMPTS</b> times to retrieve in case previous searches are unsuccessful, waiting <b>TIME_INTERVAL*ATTEMPTS</b> everytime before trying again.
 * <br>
 * There are two time outs set for the connection: Read and Connection. This value is given in milliseconds in: <b>TIME_OUT</b>.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class Downloader implements Runnable {

	private final int ATTEMPTS = 3;
	private final int TIME_INTERVAL = 1000;
	private final int TIME_OUT = 10000;
	
	private URL url;
	private Map<URL,String> documents;

	/**
	 * Instantiates a new downloader using the url to be downloaded and the map where to store the results or null in case connection is not established.
	 *
	 * @param url the url
	 * @param documentsContent the documents content
	 */
	public Downloader(URL url, Map<URL,String> documentsContent) {
		this.url = url;
		this.documents = documentsContent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		int attempt = 0;
		
		while (attempt < ATTEMPTS){
	
			try {
				
				Thread.sleep(attempt*TIME_INTERVAL);
				
				URLConnection conn = url.openConnection();
				 
				conn.setConnectTimeout(TIME_OUT); 

				conn.setReadTimeout(TIME_OUT);  
				
				conn.setRequestProperty( "User-agent", "spider" );
				
				conn.connect();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						
				StringBuilder sb = new StringBuilder();
					
				String fileLine = br.readLine();
				
				if (fileLine != null)
					sb.append(fileLine);
				
				while ((fileLine = br.readLine())!=null){
					
					sb.append("\n" + fileLine);
					
				}

				synchronized (documents) {
					
					documents.put(url, sb.toString());
					
				}
				
				System.out.print(".");
				
				return;
				
			} catch (MalformedURLException e) {
				
				//do not do anything
				
			} catch (IOException e) {
	
				//do not do anything
				
			} catch (InterruptedException e) {
				//don't do anything
			}
			
			attempt++;
		}

		documents.put(url,null);

	}

}
