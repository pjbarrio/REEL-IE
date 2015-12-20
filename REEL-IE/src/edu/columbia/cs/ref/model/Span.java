package edu.columbia.cs.ref.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class Span references a piece of a text document.
 * 
 * <br>
 * <br>
 * 
 * A span is composed by two attributes: the starting index of the span
 * and the ending index of the span.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Span implements Serializable,Comparable<Span> {
	
	/** The start. */
	private int start;
	
	/** The end. */
	private int end;
	
	/**
	 * Instantiates a new span given the starting index of the span
	 * and the ending index of the span
	 *
	 * @param start the starting index of the span
	 * @param end the ending index of the span
	 */
	public Span(int start, int end){
		this.start=start;
		this.end=end;
	}
	
	/**
	 * Instantiates a new span given a span in the format of <a href="http://incubator.apache.org/opennlp/">OpenNLP</a>:
	 * <code>opennlp.tools.util.Span</code>
	 *
	 * @param openNLPSpan the span in the OpenNLP format
	 */
	public Span(opennlp.tools.util.Span openNLPSpan){
		this.start=openNLPSpan.getStart();
		this.end=openNLPSpan.getEnd();
	}

	/**
	 * Returns the starting index of the span
	 *
	 * @return the starting index of the span
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns the ending index of the span
	 *
	 * @return the ending index of the span
	 */
	public int getEnd() {
		return end;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Span s) {
		
		if (getStart() < s.getStart()){
			return -1;
		} else if (getStart() == s.getStart()){
			
			if (getEnd() > s.getEnd()){
				return -1;
			} else if (getEnd() < s.getEnd()){
				return 1;
			} else {
				return 0;
			}
			
		} else {
			return 1;
		}
		
	}
	
	/**
	 * Determines if the input span intersects with this span
	 *
	 * @param s a different span
	 * @return true, if the input span intersects with this span
	 */
	public boolean intersects(Span s){
		int sstart = s.getStart();
		
		return this.contains(s) || s.contains(this)
				|| getStart() <= sstart && sstart<getEnd() 
				|| sstart <= getStart() && getStart() < s.getEnd();
	}

	/**
	 * Determines if the input span is contained within this span
	 *
	 * @param s a different span
	 * @return true, if the input span is contained in this span
	 */
	public boolean contains(Span s) {
		return start <= s.getStart() && s.getEnd() <= end;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return start + "-" + end;
	}
	
	/**
	 * Given a list of spans, returns list of spans that do not overlap.
	 *
	 * @param spans the original list of spans
	 * @return the list of spans that do not overlap
	 */
	public static List<Span> calculateNotOverlappingSpans(List<Span> spans){
		
		// TODO returns the not overlapping spans ... use instersects.
		
			if (spans.size()<=1)
				return spans;

			List<Span> notIntersecting = new ArrayList<Span>(spans);
			
			List<Span> intersecting = new ArrayList<Span>();
			
			for (int i=0;i<spans.size();i++) {
				
				Span span = spans.get(i);
				
				for (int j=0;j<spans.size();j++) {

					if (i==j)
						continue;
					
					Span span2 = spans.get(j);

					if (span2.intersects(span) || span.getEnd()==span2.getStart() || span2.getEnd()==span.getStart()){
						notIntersecting.remove(span2);
						if (!intersecting.contains(span2))
							intersecting.add(span2);
					}
					
				}
				
			}
			
			if (intersecting.isEmpty()){
				Collections.sort(spans);
				return spans;
			}
		
			Collections.sort(intersecting);
		
			List<Span> unifiedList = new ArrayList<Span>();

			Span unified = intersecting.get(0);

			int last = unified.getEnd();

			
			for (int i = 0; i < intersecting.size()-1; i++) {

				Span s2 = intersecting.get(i+1);
				
				if (unified.intersects(s2) || unified.getEnd()==s2.getStart() || s2.getEnd()==unified.getStart()){
					
					last = Math.max(unified.getEnd(), s2.getEnd());
					
					unified = new Span(unified.getStart(),last);
					
				} else {
					
					unifiedList.add(unified);
					
					unified = new Span(s2.getStart(),s2.getEnd());
					
					last = s2.getEnd();
					
				}
				
			}
			
			unifiedList.add(unified);
			
			notIntersecting.addAll(unifiedList);
			
			Collections.sort(notIntersecting);
			
			return notIntersecting;
		
	}
}
