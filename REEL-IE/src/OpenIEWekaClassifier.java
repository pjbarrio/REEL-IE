import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import edu.washington.cs.knowitall.extractor.conf.LabeledBinaryExtractionReader;


public class OpenIEWekaClassifier {

	/**
	 * Trains a classifier using the examples in the given file,
	 * and saves the model to disk. The examples must be in the format described in
	 * <code>LabeledBinaryExtractionReader</code>.
	 * 
	 * An optional third parameter can be passed that writes the training data in 
	 * Weka's ARFF file format to disk.
	 * 
	 * @param args
	 * @throws Exception
	 */

	
	public static void main(String[] args) throws Exception {
		
		//In the class Reverb Features, I can add more ...
		
		Classifier classifier = new J48();
		
		InputStream OperableStructurereader = new FileInputStream(new File("/home/pjbarrio/Dataset/SGML-ACE-COREF/AFFILIATE-PARTNER/0/AFFILIATE-PARTNER_PERSON_GPE_0.reverb.train")); //Reads from wherever we need (Operable Structures or arff)
		
		InputStream in = OperableStructurereader;//new FileInputStream(args[0]);
		
		LabeledBinaryExtractionReader reader = new LabeledBinaryExtractionReader(in);
		
		ReVerbClassifierTrainer trainer = new ReVerbClassifierTrainer(reader.readExtractions(),classifier);
		
		
		//Writes the binary model to a file
		SerializationHelper.write("j48.model", classifier);
		
		//These are the operable Structures as in arff that are to be saved...
		//We can save them as operable structures or as arff ...
//		if (args.length > 2) {
			ArffSaver saver = new ArffSaver();
			saver.setInstances(trainer.getDataSet().getWekaInstances());
			saver.setFile(new File("j48.arff"));
			saver.writeBatch();
//		}

		
	}
	
}
