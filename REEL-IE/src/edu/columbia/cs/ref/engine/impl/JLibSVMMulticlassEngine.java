package edu.columbia.cs.ref.engine.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationSVM;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.berkeley.compbio.jlibsvm.multi.MultiClassProblemImpl;
import edu.berkeley.compbio.jlibsvm.multi.MultiClassificationSVM;
import edu.columbia.cs.ref.engine.Engine;
import edu.columbia.cs.ref.engine.impl.resources.OperableStructureToBinarySVMproblemConverter;
import edu.columbia.cs.ref.engine.impl.resources.OperableStructureToMulticlassSVMproblemConverter;
import edu.columbia.cs.ref.model.StructureConfiguration;
import edu.columbia.cs.ref.model.core.Kernel;
import edu.columbia.cs.ref.model.core.impl.BagOfNGramsKernel;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.re.impl.JLibsvmBinaryModel;
import edu.columbia.cs.ref.model.re.impl.JLibsvmCompositeBinaryModel;
import edu.columbia.cs.ref.model.re.impl.JLibsvmMulticlassModel;
import edu.columbia.cs.ref.model.relationship.RelationshipType;

/**
 * The Class JLibSVMMulticlassEngine is an implementation of an Engine that produces models
 * by using the SVM engine from <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>.
 * 
 * <br>
 * <br>
 * 
 * It receives as input a structure configuration and a set of relationship extractions to
 * be extracted.
 * 
 * <br>
 * <br>
 * 
 * The model produced by this engine will use multi-label classifiers, which means that only one
 * label can be assigned to a given object.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class JLibSVMMulticlassEngine implements Engine {

	/** The conf. */
	private StructureConfiguration conf;
	
	/** The relationship types. */
	private Set<RelationshipType> relationshipTypes;
	
	/**
	 * Instantiates a new multiclass engine that uses JLibSVM to train.
	 *
	 * @param conf the structure configuration
	 * @param relationshipTypes the relationship types to extract
	 */
	public JLibSVMMulticlassEngine(StructureConfiguration conf, Set<RelationshipType> relationshipTypes){
		this.conf=conf;
		this.relationshipTypes=relationshipTypes;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.engine.Engine#train(java.util.List)
	 */
	@Override
	public Model train(Collection<OperableStructure> train) {
		KernelFunction<OperableStructure> kernel = (Kernel)conf.getCore();
		ImmutableSvmParameterPoint.Builder<String, OperableStructure> builder = new ImmutableSvmParameterPoint.Builder<String, OperableStructure>();
		builder.C=50;
		builder.kernel=kernel;
		builder.eps=1;
		
		ImmutableSvmParameterPoint<String, OperableStructure> params = builder.build();
		
		MultiClassProblemImpl<String,OperableStructure> problem = OperableStructureToMulticlassSVMproblemConverter.convert(new ArrayList<OperableStructure>(train));
	
		BinaryClassificationSVM<String,OperableStructure> binary = new C_SVC<String,OperableStructure>();
		
		MultiClassificationSVM<String,OperableStructure> multi = new MultiClassificationSVM<String,OperableStructure>(binary);
		
		return new JLibsvmMulticlassModel(multi.train(problem, params),conf,this.relationshipTypes);
	}

}
