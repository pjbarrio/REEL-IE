package edu.columbia.cs.ref.tool.tagger.relationship;

import java.util.List;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.tool.tagger.Tagger;
import edu.columbia.cs.ref.tool.tagger.span.impl.RelationshipSpan;

public abstract class RelationshipTagger extends Tagger<RelationshipSpan,Relationship> {

	public RelationshipTagger(String tag) {
		super(tag);
	}

	@Override
	protected void updateDocument(Document d, Relationship tuple) {
		d.addRelationship(tuple);		
	}

	@Override
	protected abstract List<RelationshipSpan> findSpans(Document d);

	@Override
	protected Relationship generateInstance(RelationshipSpan container,
			Document d) {
		// TODO Auto-generated method stub
		return null;
	}

}
