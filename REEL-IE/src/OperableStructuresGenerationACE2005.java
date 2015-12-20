import java.io.File;
import java.util.Set;

import weka.core.SerializationHelper;
import edu.columbia.cs.ref.algorithm.StructureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntityBasedChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.GenericPartOfSpeechFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPPartOfSpeechFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.SpansToStringsConvertionFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.impl.BagOfNGramsKernel;
import edu.columbia.cs.ref.model.core.impl.OpenInformationExtractionCore;
import edu.columbia.cs.ref.model.core.impl.SubsequencesKernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.ref.tool.io.CandidatesSentenceReader;
import edu.columbia.cs.ref.tool.io.CoreWriter;



public class OperableStructuresGenerationACE2005 {

	public static void main(String[] args) throws Exception {
		StructureConfiguration conf = new StructureConfiguration(new SubsequencesKernel());
				
		/*FeatureGenerator<SequenceFS<Span>> tokenizer = new OpenNLPTokenizationFG("en-token.bin");
		FeatureGenerator<SequenceFS<Span>> fgChunk = new EntityBasedChunkingFG(tokenizer);
		FeatureGenerator<SequenceFS<String>> fgChuckString = new SpansToStringsConvertionFG(fgChunk);
		FeatureGenerator<SequenceFS<String>> fgPOS = new OpenNLPPartOfSpeechFG("en-pos-maxent.bin",fgChuckString);
		FeatureGenerator<SequenceFS<String>> fgGPOS = new GenericPartOfSpeechFG(fgPOS);
		//conf.addFeatureGenerator(fgPOS);
		conf.addFeatureGenerator(fgGPOS);*/
		
		String inputFolder = "/home/goncalo/ACEProcessedFlat/";
		File ACEDir = new File(inputFolder);
		File[] files = ACEDir.listFiles();
		String outputFolder = "/home/goncalo/ACESubseqFlat/";
		for(int i=0; i<files.length; i++){
			File f = files[i];
			
			String fileName = f.getName();
			Set<CandidateSentence> sents = CandidatesSentenceReader.readCandidateSentences(inputFolder+fileName);
			System.out.println("Processing file " + (i+1) + " of " + files.length + " containing " + sents.size() + " sentences");
			Set<OperableStructure> structures = StructureGenerator.generateStructures(sents, conf);
			
			CoreWriter.writeOperableStructures(structures, outputFolder+fileName);
		}
		
		SerializationHelper.write(outputFolder+"Configuration.bin",conf);
	}

}
