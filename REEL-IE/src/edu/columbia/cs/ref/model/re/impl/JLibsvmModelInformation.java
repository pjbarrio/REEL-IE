package edu.columbia.cs.ref.model.re.impl;

import java.io.Serializable;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;

/**
 * The Class JLibsvmModelInformation is a container for the serialization of
 * models based on <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>.
 * 
 * <br>
 * <br>
 * 
 * It is an internal representation that should not concern the user of this framework.
 * It is used only to deal with the fact that the models from <a href="http://dev.davidsoergel.com/trac/jlibsvm/">JLibSVM</a>
 * are not serializable
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class JLibsvmModelInformation implements Serializable {
	
	/** The num sv. */
	private int numSV;
	
	/** The support vectors. */
	private OperableStructure[] supportVectors;
	
	/** The alpha. */
	private double[] alpha;
	
	/** The rho. */
	private float rho;
	
	/** The kernel. */
	private KernelFunction<OperableStructure> kernel;
	
	/** The true label. */
	private String trueLabel;
	
	/** The false label. */
	private String falseLabel;
	
	/**
	 * Instantiates a new JLibsvmModelInformation.
	 *
	 * @param numSV the number of support vectors
	 * @param supportVectors the support vectors
	 * @param alpha the alpha values for the support vectors
	 * @param rho the rho value
	 * @param kernel the kernel used by the model
	 * @param trueLabel the positive label
	 * @param falseLabel the negative label
	 */
	public JLibsvmModelInformation(int numSV, OperableStructure[] supportVectors,
			double[] alpha, float rho, KernelFunction<OperableStructure> kernel, String trueLabel, String falseLabel){
		this.numSV=numSV;
		this.supportVectors=supportVectors;
		this.alpha=alpha;
		this.rho=rho;
		this.setKernel(kernel);
		this.setFalseLabel(falseLabel);
		this.setTrueLabel(trueLabel);
	}

	/**
	 * Returns the number of support vectors
	 *
	 * @return the number of support vectors
	 */
	public int getNumSV() {
		return numSV;
	}

	/**
	 * Sets the number of support vectors
	 *
	 * @param numSV number of support vectors
	 */
	public void setNumSV(int numSV) {
		this.numSV = numSV;
	}

	/**
	 * Returns the support vectors.
	 *
	 * @return the support vectors
	 */
	public OperableStructure[] getSupportVectors() {
		return supportVectors;
	}

	/**
	 * Sets the support vectors.
	 *
	 * @param supportVectors the new support vectors
	 */
	public void setSupportVectors(OperableStructure[] supportVectors) {
		this.supportVectors = supportVectors;
	}

	/**
	 * Returns the alpha values for the support vectors
	 *
	 * @return the alpha values for the support vectors
	 */
	public double[] getAlpha() {
		return alpha;
	}

	/**
	 * Sets the alpha values for the support vectors
	 *
	 * @param alpha the alpha values for the support vectors
	 */
	public void setAlpha(double[] alpha) {
		this.alpha = alpha;
	}

	/**
	 * Returns the rho value
	 *
	 * @return the rho value
	 */
	public float getRho() {
		return rho;
	}

	/**
	 * Sets the rho value
	 *
	 * @param rho the rho value
	 */
	public void setRho(float rho) {
		this.rho = rho;
	}

	/**
	 * Sets the kernel.
	 *
	 * @param kernel the new kernel
	 */
	public void setKernel(KernelFunction<OperableStructure> kernel) {
		this.kernel = kernel;
	}

	/**
	 * Returns the kernel used by the model
	 *
	 * @return the kernel used by the model
	 */
	public KernelFunction<OperableStructure> getKernel() {
		return kernel;
	}

	/**
	 * Sets the negative label.
	 *
	 * @param falseLabel the new negative label
	 */
	public void setFalseLabel(String falseLabel) {
		this.falseLabel = falseLabel;
	}

	/**
	 * Gets the negative label.
	 *
	 * @return the negative label
	 */
	public String getFalseLabel() {
		return falseLabel;
	}

	/**
	 * Sets the positive label.
	 *
	 * @param trueLabel the new positive label
	 */
	public void setTrueLabel(String trueLabel) {
		this.trueLabel = trueLabel;
	}

	/**
	 * Gets the positive label.
	 *
	 * @return the positive label
	 */
	public String getTrueLabel() {
		return trueLabel;
	}
}
