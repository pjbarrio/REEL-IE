package edu.columbia.cs.ref.tool.tagger.entity;

import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tagger.Tagger;
import edu.columbia.cs.ref.tool.tagger.span.TaggedSpan;
import edu.columbia.cs.ref.tool.tagger.span.impl.EntitySpan;

/**
 * The EntityTagger is an abstract class that extends from Tagger.
 * 
 * <br>
 * <br>
 * 
 * Instances of this class are able to annotate entities that are present in the text.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class EntityTagger<S extends EntitySpan,E extends Entity> extends Tagger<S,Entity> {
	
	/**
	 * Instantiates a new entity tagger.
	 *
	 * @param tag the tag
	 */
	public EntityTagger(String tag) {
		super(tag);
	}
	
	public EntityTagger(Set<String> tag) {
		super(tag);
	}

	/**
	 * This method defines the behavior of the EntityTagger.
	 * 
	 * <br>
	 * <br>
	 * 
	 * The objective of this method is to find references to entities in document
	 * d that must be annotated.
	 *
	 * @param d the document where the entities are present
	 * @return a list containing all the entities of the input document that this tagger
	 * will annotate
	 */
	protected abstract List<S> findSpans(Document d);

	@Override
	protected Entity generateInstance(S entitySpan, Document d) {
		return new Entity(entitySpan.getId(), entitySpan.getTag(), entitySpan.getOffset(), entitySpan.getLength(), entitySpan.getValue(), d);
	}
	
	@Override
	protected void updateDocument(Document d, Entity entity) {
		d.addEntity(entity);
	}
}
