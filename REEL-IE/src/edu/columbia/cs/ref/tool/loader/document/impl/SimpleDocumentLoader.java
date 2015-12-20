package edu.columbia.cs.ref.tool.loader.document.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Segment;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.loader.document.DocumentLoader;

public class SimpleDocumentLoader extends DocumentLoader {

	public SimpleDocumentLoader(Set<RelationshipType> relationshipTypes) {
		super(relationshipTypes);
	}

	@Override
	public Set<Document> load(File file) {
		HashSet<Document> ret = new HashSet<Document>(1);
		ret.add(new Document(file.getAbsolutePath(), file.getName(), new ArrayList<Segment>(0)));
		return ret;
	}

}
