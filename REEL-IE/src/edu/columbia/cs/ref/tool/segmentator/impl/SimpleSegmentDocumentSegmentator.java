package edu.columbia.cs.ref.tool.segmentator.impl;

import java.util.ArrayList;
import java.util.List;

import edu.columbia.cs.ref.model.Segment;
import edu.columbia.cs.ref.tool.segmentator.DocumentSegmentator;

/**
 * The SimpleSegmentDocumentSegmentator is an implementation of the DocumentSegmentator.
 * 
 * This DocumentSegmentator is actually a dummy segmentator since it does not perform
 * any transformation. It simply produces a new segmentation that corresponds to the whole
 * content of the document
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class SimpleSegmentDocumentSegmentator implements DocumentSegmentator {

	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.document.segmentator.DocumentSegmentator#segmentate(java.lang.String)
	 */
	@Override
	public List<Segment> segmentate(String documentContent) {
		
		List<Segment> segments = new ArrayList<Segment>(1);
		
		segments.add(new Segment(documentContent, 0));
		
		return segments;
		
	}

}
