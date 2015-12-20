package edu.columbia.cs.ref.model.core;

import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;

/**
 * The Class Kernel represents an implementation of a kernel-based technique. It is simply
 * an implementation of the interface <code>KernelFunction</code> from the JLibSVM library.
 * 
 * <br>
 * <br>
 * 
 * The interface <code>KernelFunction</code> forces the implementation of the method <code>evaluate</code>. This
 * method is responsible for the computation of the kernel function.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class Kernel extends Core implements KernelFunction<OperableStructure> {

}
