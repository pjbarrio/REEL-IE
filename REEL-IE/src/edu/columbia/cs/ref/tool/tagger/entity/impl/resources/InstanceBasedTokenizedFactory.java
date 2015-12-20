package edu.columbia.cs.ref.tool.tagger.entity.impl.resources;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.columbia.cs.ref.model.Span;

public class InstanceBasedTokenizedFactory implements TokenizerFactory{

	private class SpanBasedTokenizer extends Tokenizer{

		private String str;
		private Span[] spans;
		private int currentIndex;
		private int lastokenstart;
		private int lasttokenend;

		public SpanBasedTokenizer(String str, Span[] spans) {
			
			this.str = str;
			this.spans = spans;
			this.currentIndex = 0;
			this.lastokenstart = -1;
			this.lasttokenend = -1;
		}

		@Override
		public String nextToken() {
			
			if (currentIndex < spans.length){
			
				lastokenstart = spans[currentIndex].getStart();
				lasttokenend = spans[currentIndex].getEnd();
				
				String ret = str.substring(lastokenstart, lasttokenend);
				currentIndex++;
				return ret;
				
			}
			
			return null;
			
		}
		
	    public int lastTokenStartPosition() {
	        return lastokenstart;
	    }

	    public int lastTokenEndPosition() {
	        return lasttokenend;
	    }
		
	}
	
    public static InstanceBasedTokenizedFactory INSTANCE = null;
	private edu.columbia.cs.ref.tool.tokenizer.Tokenizer tokenizer;
	
	public InstanceBasedTokenizedFactory(edu.columbia.cs.ref.tool.tokenizer.Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	@Override
	public Tokenizer tokenizer(char[] ch, int start, int length) {
		
		String str = new String(ch, start, length);
		
		Span[] spans = null;
		
		synchronized (tokenizer) {
			
			spans = tokenizer.tokenize(str);
			
		}
		
		return new SpanBasedTokenizer(str,spans);
		
	}

	public static TokenizerFactory getInstance(
			edu.columbia.cs.ref.tool.tokenizer.Tokenizer tokenizer) {
		
		if (INSTANCE == null){
			INSTANCE = new InstanceBasedTokenizedFactory(tokenizer);
		}
		return INSTANCE;
	}

}
