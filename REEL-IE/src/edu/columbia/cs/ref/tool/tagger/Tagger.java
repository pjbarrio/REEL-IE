/**
 * 
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */
package edu.columbia.cs.ref.tool.tagger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tagger.span.TaggedSpan;

/**
 * Tagger is an abstract class that represents objects that can be used to annotate
 * documents.
 * 
 * <br>
 * <br>
 * 
 * In general, defining property of a Tagger is the tag that is assigned to the annotations
 * produced in the document. The method that must be used for a Tagger to annotate a
 * document is the method enrich.
 * 
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class Tagger<C extends TaggedSpan,O> {

	private Set<String> tags;

	/**
	 * Instantiates a new tagger.
	 *
	 * @param tags The tags to be assigned to every annotation made by this tagger
	 */
	public Tagger(Set<String> tags){
		this.tags = tags;
	}
	
	/**
	 * Instantiates a new tagger.
	 *
	 * @param tag The tag to be assigned to every annotation made by this tagger
	 */
	public Tagger(String tag){
		tags = new HashSet<String>();
		tags.add(tag);
	}
	
	/**
	 * This is the method that defines the behavior of the Tagger. It receives a document
	 * that has to be annotated and produces all the annotations in that document
	 *
	 * @param d the document to be annotated
	 */
	public void enrich(Document d){
		
		List<C> spans = findSpans(d);

		for (C span : spans) {
			
			O object = createInstance(span,d);
				
			if (object != null)
				updateDocument(d,object);
				
		}

	}
	
	
	
	/**
	 * Updates the document by adding the tagged object.
	 *
	 * @param d the document
	 * @param object the tagged object
	 */
	protected abstract void updateDocument(Document d, O object);

	/**
	 * Find spans of the objects to be tagged.
	 *
	 * @param d the document
	 * @return the list of spans
	 */
	protected abstract List<C> findSpans(Document d);

	/**
	 * Creates the instance of a tagged object using the object spans found by a specific instantiation of Tagger.
	 *
	 * @param container the objct spans
	 * @param d the document
	 * @return the tagged object.
	 */
	private O createInstance(C container,Document d){
		
		if (tags.contains(container.getTag())){
			return generateInstance(container,d);
		}
		return null;
	}

	protected abstract O generateInstance(C container, Document d);
	
	public boolean accepts(String tag){
		return tags.contains(tag);
	}
}
