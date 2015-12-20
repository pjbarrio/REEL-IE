package edu.columbia.cs.ref.model.core.impl.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.tools.ant.filters.StringInputStream;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

public class OperableStructureSetInputStream extends InputStream {

	private StringInputStream inputStream;

	public OperableStructureSetInputStream(Set<OperableStructure> trainingData, boolean training, Set<RelationshipType> relationshipTypes) {
		StringBuilder sb = new StringBuilder();
		
		int size = trainingData.size();
		
		for (OperableStructure operableStructure : trainingData) {
			
			if (size > 0)
				sb.append(operableStructure.toString() + "\n" + getLabel(operableStructure.getLabels(),relationshipTypes) + "\n");
			else //last One
				sb.append(operableStructure.toString() + "\n" + getLabel(operableStructure.getLabels(),relationshipTypes));
			size--;
			
		}
		
		inputStream = new StringInputStream(sb.toString());
		
	}



	private int getLabel(Set<String> labels,
			Set<RelationshipType> relationshipTypes) {
		
		for (RelationshipType relationshipType : relationshipTypes) {
			
			if (labels.contains(relationshipType.getType()))
				return 1;
			
		}
		
		return 0;
	}



	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

}
