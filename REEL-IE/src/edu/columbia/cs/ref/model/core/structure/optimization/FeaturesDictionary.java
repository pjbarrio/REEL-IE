package edu.columbia.cs.ref.model.core.structure.optimization;

import java.util.HashMap;

/**
 * The Class FeaturesDictionary represents an object that stores String-based
 * features and, for a given value, always returns the same object. By returning
 * always the same object for a given value, the comparisons between string-based
 * features can be done by using the '==' operator instead of equals.
 * 
 * <br>
 * <br>
 * 
 * This class is used only for optimization purposes.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class FeaturesDictionary {
	
	/** The features. */
	private HashMap<String,String> features = new HashMap<String,String>();
	
	/**
	 * Given a string as an input value, it returns a string with that
	 * same value. However, it is guaranteed that if this method is called
	 * for different string objects with the same value, it will return
	 * the very same object.
	 *
	 * @param result the value of the string-based feature to be retrieved
	 * @return an object with the same value as result but that is guaranteed to be the same
	 * for every call of this method with the same value
	 */
	public synchronized String getFeature(String result){
		String r = features.get(result);
		if(r==null){
			features.put(result, result);
			r=result;
		}
		return r;
	}
}
