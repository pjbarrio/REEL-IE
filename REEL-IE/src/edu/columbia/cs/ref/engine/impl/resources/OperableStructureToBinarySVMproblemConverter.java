package edu.columbia.cs.ref.engine.impl.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.scaler.NoopScalingModel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * This class converts the training data as represented in our framework to the format
 * needed by <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>. This
 * class is used by the binary engine which means that it returns the data as
 * a <code>BinaryClassificationProblemImpl<String, OperableStructure></code>
 *
 * @see <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OperableStructureToBinarySVMproblemConverter {

	/**
	 * Converts the training data into a <code>BinaryClassificationProblemImpl<String, OperableStructure></code>
	 *
	 * @param train the training data
	 * @param relLabel the relationship type that the binary classifier must extract
	 * @return the representation of a binary SVM problem of <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>
	 */
	public static BinaryClassificationProblemImpl<String, OperableStructure> convert(
			List<OperableStructure> train, RelationshipType relLabel) {
		Map<OperableStructure,String> examples = new HashMap<OperableStructure,String>();
		Map<OperableStructure,Integer> exampleIds = new HashMap<OperableStructure,Integer>();
		
		Map<String,Integer> numDocs = new HashMap<String,Integer>();
		
		int i=0;
		for(OperableStructure sent : train){
			String label;
			Set<String> trueLabels = sent.getLabels();
			if(trueLabels.contains(relLabel.getType())){
				label=relLabel.getType();
			}else{
				label=RelationshipType.NOT_A_RELATIONSHIP;
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
		
		BinaryClassificationProblemImpl<String,OperableStructure> result = 
			new BinaryClassificationProblemImpl<String,OperableStructure>(String.class, 
					examples, exampleIds, new NoopScalingModel<OperableStructure>(),
					relLabel.getType(),RelationshipType.NOT_A_RELATIONSHIP);
		
		return result;
	}

}
