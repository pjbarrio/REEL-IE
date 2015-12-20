package edu.columbia.cs.ref.algorithm.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.columbia.cs.ref.algorithm.evaluation.measure.Measure;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.re.Model.PredictionProperties;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class Evaluator is used to evaluate the quality of the results
 * of a given relationship extraction model.
 * 
 * <br>
 * <br>
 * 
 * An Evaluator is composed by a list of measures that are to be computed
 * in order to evaluate models.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Evaluator {
	
	/** The measures. */
	private List<Measure> measures = new ArrayList<Measure>();
	
	/** The results. */
	private Map<Measure,Double> results = new HashMap<Measure,Double>();
	
	/**
	 * Adds a new measure to the evaluator.
	 *
	 * @param measure the new measure
	 */
	public void addMeasure(Measure measure){
		measures.add(measure);
	}
	
	/**
	 * Gets the evaluation result for a given measure
	 *
	 * @param m the measure for which we want to retrieve the result
	 * @return the result of the evaluation of the input measure
	 */
	public double getResult(Measure m){
		return results.get(m);
	}
	
	/**
	 * Prints the evaluation report.
	 *
	 * @param testingFiles the Operable structures to be used during testing. It is assumed that they are tagged with the gold set labels.
	 * @param m the model that will be evaluated.
	 */
	public void printEvaluationReport(List<OperableStructure> testingFiles, Model m){
		Map<OperableStructure, Set<String>> labels = new HashMap<OperableStructure, Set<String>>();
		Map<OperableStructure, Map<PredictionProperties, Object>> properties = new HashMap<OperableStructure, Map<PredictionProperties, Object>>();
		
		int testingFilesSize=testingFiles.size();
		for(int i=0; i<testingFilesSize; i++){
			OperableStructure s = testingFiles.get(i);
			
			Set<String> trueLabel = s.getLabels();
			Set<String> predicted = m.predictLabel(s);
//			System.out.println("i=" + i + ": [" + predicted + "," + trueLabel + "]");
			Map<PredictionProperties, Object> props = m.obtainPredictionPropertyValue(s);
			
//			if (!predicted.isEmpty() && trueLabel.isEmpty())
//				System.err.println("FP: " + Arrays.toString(s.getCandidateSentence().getEntities()) + " - " + s.getCandidateSentence().getSentence().getValue());
//			
//			if (predicted.isEmpty() && !trueLabel.isEmpty())
//				System.err.println("FN: " + Arrays.toString(s.getCandidateSentence().getEntities()) + " - " + s.getCandidateSentence().getSentence().getValue());
			
			labels.put(s, predicted);
			properties.put(s, props);
		}
		
		for(Measure measure : measures){
			double result = measure.getValue(labels, properties);
			results.put(measure, result);
			System.out.println(measure + ": " + result);
		}
	}
}
