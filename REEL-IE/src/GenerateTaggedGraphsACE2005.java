import java.io.File;
import java.io.IOException;
import java.util.Set;

import edu.columbia.cs.ref.algorithm.StructureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.impl.ShortestPathKernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.tool.io.CandidatesSentenceReader;
import edu.columbia.cs.ref.tool.io.CoreWriter;


public class GenerateTaggedGraphsACE2005 {
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		StructureConfiguration conf = new StructureConfiguration(new ShortestPathKernel());
		
		
		//FeatureGenerator tokenizer = new OpenNLPTokenizationFG("en-token.bin");
		//FeatureGenerator fgChunk = new DependentFeatureGenerator(new EntityBasedChunkingFG(),tokenizer);
		//FeatureGenerator fgPOS = new DependentFeatureGenerator(new OpenNLPPartOfSpeechFG("en-pos-maxent.bin"),fgChunk);
		//FeatureGenerator fgGPOS = new DependentFeatureGenerator(new GenericPartOfSpeechFG(),fgPOS);
		//conf.addFeatureGenerator(fgPOS);
		//conf.addFeatureGenerator(fgGPOS);
		
		String inputFolder = args[1] + args[0] + "/";
		File ACEDir = new File(inputFolder);
		File[] files = ACEDir.listFiles();
		String outputFolder = args[2] + args[0] + "/";
		for(int i=0; i<files.length; i++){
			System.out.println("Processing file " + (i+1) + " of " + files.length);
			File f = files[i];
			
			String fileName = f.getName();
			Set<CandidateSentence> sents = CandidatesSentenceReader.readCandidateSentences(inputFolder+fileName);
			Set<OperableStructure> structures = StructureGenerator.generateStructures(sents, conf);
			
			CoreWriter.writeOperableStructures(structures, outputFolder+fileName);
		}
	}
}
