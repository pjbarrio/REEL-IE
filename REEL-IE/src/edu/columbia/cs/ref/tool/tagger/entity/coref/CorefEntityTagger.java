package edu.columbia.cs.ref.tool.tagger.entity.coref;

import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.entity.CorefEntity;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tagger.entity.EntityTagger;
import edu.columbia.cs.ref.tool.tagger.span.impl.CorefEntitySpan;

public abstract class CorefEntityTagger extends EntityTagger<CorefEntitySpan, CorefEntity> {

	public CorefEntityTagger(Set<String> tag) {
		super(tag);
	}

	@Override
	protected Entity generateInstance(CorefEntitySpan entitySpan, Document d) {
		return new CorefEntity(entitySpan.getId(), entitySpan.getTag(), entitySpan.getOffset(), entitySpan.getLength(), entitySpan.getValue(), d,entitySpan.getRootEntity());
	}

	public List<CorefEntitySpan> getCorefSpans(Document doc) {
		
		return findSpans(doc);
		
	}

}
