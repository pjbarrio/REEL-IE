package edu.columbia.cs.ref.tool.preprocessor;


/**
 * This class is an implementation of the Preprocessor interface.
 * 
 * This Preprocessor is used to compose several preprocessors. The result of this processor
 * is the consecutive application of several transformations to the input content
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class CompositeSequentialPreprocessor implements Preprocessor {

	/** The simple preprocessors. */
	private Preprocessor[] preprocessors;

	/**
	 * Instantiates a new composite sequential preprocessor. Note that the order
	 * of the inputs is very important because the transformations may not be commutative.
	 *
	 * @param preprocessors the preprocessors
	 */
	public CompositeSequentialPreprocessor (Preprocessor...preprocessors){
		this.preprocessors = preprocessors;
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.cg.document.preprocessing.Preprocessor#process(java.lang.String)
	 */
	@Override
	public String process(String content) {
		
		for (int i = 0; i < preprocessors.length; i++) {
			
			content = preprocessors[i].process(content);
			
		}
		
		return content;
	}

}
