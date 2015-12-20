package edu.columbia.cs.ref.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;

/**
 * Particular type of Document that went through a tokenization process.
 * 
 * <br>
 * <br>
 * 
 * Like a Document, a TokenizedDocument is defined by its path, the name of the file,
 * a list of Segments that represent the content of the document and annotations of
 * entities and relationships in the document. Additionally, a TokenizedDocument is
 * composed by the information that results from the tokenization.
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class TokenizedDocument extends Document {

	private Span[] detectedSpans;
	
	private String[] detectedString;

	private Map<Entity, Span> entitySpanTable;
	
	/**
	 * Constructor of the Document
	 * 
	 * @param d document without tokenization
	 * @param tokenizer tokenizer used to tokenize the document
	 */
	public TokenizedDocument(Document d, Tokenizer tokenizer) {
		
		super(d);
		
		String text = getString(d.getPlainText());
				
		detectedSpans = tokenizer.tokenize(text);
		
		detectedString = generateTokenStrings(detectedSpans,text);
		
		entitySpanTable = new HashMap<Entity, Span>();
		
		//TODO I don't like this...
		
		for (Entity entity : d.getEntities()) {
			
			entity.setDocument(this);
			
		}
	}

	private String getString(List<Segment> plainText) {
		
		StringBuilder sb = new StringBuilder();
		
		for (Segment segment : plainText) {
			
			sb.insert(segment.getOffset(), segment.getValue());
			
		}
		
		return sb.toString();
		
	}

	private String[] generateTokenStrings(Span[] spans, String text) {
		
		String[] tokenStrings = new String[spans.length];
		
		for (int i = 0; i < spans.length; i++) {
			
			tokenStrings[i] = text.substring(spans[i].getStart(), spans[i].getEnd());
			
		}
		
		return tokenStrings;
	}

	/**
	 * Returns the indexes in the tokenization.
	 * 
	 * @param entity Entity that we are trying to find the indexes for
	 * @return start and end indexes of the input entity
	 */
	public Span getEntitySpan(Entity entity) {
		
		Span ret = entitySpanTable.get(entity);
		
		if (ret!=null)
			return ret;
		
		int firstIndex = binarySearch(detectedSpans,entity.getOffset(),0,detectedSpans.length-1);
		
		int endIndex = binarySearch(detectedSpans,entity.getOffset() + entity.getLength()-1, firstIndex,detectedSpans.length-1);
		
		ret = new Span(firstIndex,endIndex);
		
		entitySpanTable.put(entity,ret);
		
		return ret;
		
	}

	
	private int binarySearch(Span[] spans,int offset,int low, int high){
		
		//Though this never happens, it's good to have it (real binary search)
		if (high < low)
			return -1; // not found
		int mid = low + (high - low) / 2;
		if (spans[mid].getStart() > offset)
			return binarySearch(spans, offset, low, mid-1);
		else if (spans[mid].getEnd() <= offset)
			return binarySearch(spans, offset, mid+1, high);
		else
			return mid; // found
	}

	/**
	 * Returns an array of Strings where each entry is the value of each token of the text
	 * 
	 * @return tokens of the text
	 */
	public String[] getTokenizedString() {
		
		return detectedString;
	
	}
	
	/**
	 * Returns an array of spans where each entry corresponds to the start and ending
	 * indexes of the tokens in the text
	 * 
	 * @return indexes of the tokens of the text
	 */
	public Span[] getTokenizedSpans() {
		
		return detectedSpans;
	
	}

}
