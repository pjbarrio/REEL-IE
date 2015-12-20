package edu.columbia.cs.ref.tool.segmentator;

import java.util.List;

import edu.columbia.cs.ref.model.Segment;

/**
 * The Interface DocumentSegmentator.
 * 
 * Document segmentators are used to divide the content of a document as sections.
 * A Section correspond to a natural division of the text (e.g., chapters, paragraphs)
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface DocumentSegmentator {

	/**
	 * This method receives as input the content of a document as a String and returns
	 * a division of the document as a List of sections
	 *
	 * @param documentContent the content of a document
	 * @return division of the content into sections
	 */
	public List<Segment> segmentate(String documentContent);

}
