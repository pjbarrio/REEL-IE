import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.columbia.cs.ref.algorithm.CandidatesGenerator;
import edu.columbia.cs.ref.algorithm.StructureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntityBasedChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPPartOfSpeechFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.SpansToStringsConvertionFG;
import edu.columbia.cs.ref.engine.Engine;
import edu.columbia.cs.ref.engine.impl.JLibSVMBinaryEngine;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Dataset;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.constraint.role.impl.EntityTypeConstraint;
import edu.columbia.cs.ref.model.core.impl.ShortestPathKernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.document.splitter.impl.OpenNLPMESplitter;
import edu.columbia.cs.ref.tool.loader.document.impl.ace2005.ACE2005Loader;


public class TrainREModel {
	public static void main(String[] args) throws IOException{
		//First, we need to define the type of relationship we want to extract
		//as well as the constraints imposed to the entities involved in the
		//relationship. Our objective is to extract a relationship tagged as
		//ORG-AFF in the training data and for which the first argument ("Arg-1")
		//is a person ("PER") and the second argument ("ARG-2") is an organization
		//("ORG"). Note that you may not introduce constraints but if you decide
		//not to do it then the candidate entities will include all combinations
		//of all entity types
		RelationshipType relationshipType = new RelationshipType("ORG-AFF","Arg-1","Arg-2");
		relationshipType.setConstraints(new EntityTypeConstraint("ORG"), "Arg-2");
		relationshipType.setConstraints(new EntityTypeConstraint("PER"), "Arg-1");
		Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
		relationshipTypes.add(relationshipType);
		
		//We need a sentence splitter to create the candidates generator
		OpenNLPMESplitter splitter = new OpenNLPMESplitter("/path/to/sentence/splitter/model.bin");
		CandidatesGenerator generator = new CandidatesGenerator(splitter);
		
		//In order to load the data we need a DocumentLoader. In this case, we
		//use the ACE2005Loader but you may use your own loader for your own data
		ACE2005Loader l = new ACE2005Loader(relationshipTypes);
		File ACEDir = new File("/path/to/training/data/trainingData/");
		Dataset<Document> ace2005 = new Dataset<Document>(l,ACEDir,false);
		Set<CandidateSentence> candidates = new HashSet<CandidateSentence>();
		for(Document d : ace2005){
			candidates.addAll(generator.generateCandidates(d, relationshipTypes));	
		}
		
		//Now that we have the candidate sentences, we need to convert them to
		//the structure used by the core and enrich it with additional features.
		//In this example we will consider only one additional features: the POS
		//tags of each word in the sentence. However, since the POS feature extractor
		//depends on other features, we need to create these dependencies:
		StructureConfiguration conf = new StructureConfiguration(new ShortestPathKernel());
		FeatureGenerator<SequenceFS<Span>> tokenizer = new OpenNLPTokenizationFG("/path/to/tokenizer/model.bin");
		FeatureGenerator<SequenceFS<Span>> fgChunk = new EntityBasedChunkingFG(tokenizer);
		FeatureGenerator<SequenceFS<String>> fgChuckString = new SpansToStringsConvertionFG(fgChunk);
		FeatureGenerator<SequenceFS<String>> fgPOS = new OpenNLPPartOfSpeechFG("/path/to/pos/model.bin",fgChuckString);
		conf.addFeatureGenerator(fgPOS);
		Set<OperableStructure> trainingData = StructureGenerator.generateStructures(candidates, conf);
		
		//The engine is responsible for the training. In this case, we are using
		//an engine based on the JLibSVM library. You may want to create your own
		//engine based on any other library.
		Engine classificationEngine = new JLibSVMBinaryEngine(conf, relationshipTypes);
		Model svmModel = classificationEngine.train(trainingData);
		
		//Finally, we can store the model in order to use it later
		edu.columbia.cs.ref.tool.io.SerializationHelper.write("/path/to/model/ORG-AFFModel.svm", svmModel);
	}
}
