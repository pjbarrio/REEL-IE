import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.algorithm.CandidatesGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Dataset;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.constraint.role.impl.EntityTypeConstraint;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.document.splitter.impl.OpenNLPMESplitter;
import edu.columbia.cs.ref.tool.io.CandidatesSentenceWriter;
import edu.columbia.cs.ref.tool.loader.document.impl.ace2005.ACE2005Loader;


public class GenerateCandidatesACE2005 {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		RelationshipType relationshipType = new RelationshipType("ORG-AFF","Arg-1","Arg-2");
		relationshipType.setConstraints(new EntityTypeConstraint("ORG"), "Arg-2");
		relationshipType.setConstraints(new EntityTypeConstraint("PER"), "Arg-1");
		
		//relationshipType.setConstraints(new EntitiesOrderNotRelevantConstraint());
		Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
		relationshipTypes.add(relationshipType);
		
		OpenNLPMESplitter splitter = new OpenNLPMESplitter("en-sent.bin");
		CandidatesGenerator generator = new CandidatesGenerator(splitter);
		
		ACE2005Loader l = new ACE2005Loader(relationshipTypes);
		File ACEDir = new File("/home/goncalo/ACEFlat/");
		Dataset<Document> ace2005 = new Dataset<Document>(l,ACEDir,false);
		
		String outputFolder = "/home/goncalo/ACEProcessedFlat/";
		
		for(Document d : ace2005){
			System.out.println("Processing " + d.getPath()+"/"+d.getFilename());
			Set<CandidateSentence> candidates = generator.generateCandidates(d, relationshipTypes);	
			CandidatesSentenceWriter.writeCandidateSentences(candidates, outputFolder+d.getFilename());
		}
	}

}
