package edu.columbia.cs.ref.engine.impl.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.labelinverter.StringLabelInverter;
import edu.berkeley.compbio.jlibsvm.multi.MultiClassProblemImpl;
import edu.berkeley.compbio.jlibsvm.scaler.NoopScalingModel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * This class converts the training data as represented in our framework to the format
 * needed by <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>. This
 * class is used by the multiclass engine which means that it returns the data as
 * a <code>MultiClassProblemImpl<String, OperableStructure></code>
 *
 * @see <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OperableStructureToMulticlassSVMproblemConverter {

	/**
	 * Converts the training data into a <code>MultiClassProblemImpl<String, OperableStructure></code>
	 *
	 * @param train the training data
	 * @return the representation of a multiclass SVM problem of <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>
	 */
	public static MultiClassProblemImpl<String, OperableStructure> convert(
			List<OperableStructure> train) {
		Map<OperableStructure,String> examples = new HashMap<OperableStructure,String>();
		Map<OperableStructure,Integer> exampleIds = new HashMap<OperableStructure,Integer>();
		
		Map<String,Integer> numDocs = new HashMap<String,Integer>();
		
		int i=0;
		for(OperableStructure sent : train){
			String label=RelationshipType.NOT_A_RELATIONSHIP;
			Set<String> trueLabels = sent.getLabels();
			if(trueLabels.size()>1){
				System.out.println("ERROR: can't deal with overlaping relationships... try binary classification");
				System.exit(1);
			}
			
			for(String str : trueLabels){
				label=str;
			}
			
			examples.put(sent, label);
			exampleIds.put(sent, i);
			
			Integer res = numDocs.get(label);
			if(res==null){
				res=0;
			}
			numDocs.put(label, res+1);
			
			i++;
		}
		
		System.out.println(numDocs);
		
		MultiClassProblemImpl<String,OperableStructure> result = 
			new MultiClassProblemImpl<String,OperableStructure>(String.class, new StringLabelInverter(), 
					examples, exampleIds, new NoopScalingModel<OperableStructure>());
		
		return result;
	}

}
