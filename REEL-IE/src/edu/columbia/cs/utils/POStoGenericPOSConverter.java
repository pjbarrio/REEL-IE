package edu.columbia.cs.utils;
import java.util.HashMap;
import java.util.Map;


/**
 * The Class POStoGenericPOSConverter is a simple converter from POS tags to
 * Generic POS tags.
 * 
 * <br>
 * <br>
 * 
 * The conversion is directly made from a table:
 * 
 * <br>
 * <br>
 * 
 * <table border="1">
 * 	<tr>
 * 		<th>POS</th>
 * 		<th>GPOS</th>
 * 	</tr>
 * 	<tr>
 * 		<td>JJ, JJR, JJS</td>
 * 		<td>Adjective</td>
 * 	</tr>
 * 	<tr>
 * 		<td>NN, NNS, NNP, NNPS</td>
 * 		<td>Noun</td>
 * 	</tr>
 *  <tr>
 * 		<td>RB, RBR, RBS</td>
 * 		<td>Adverb</td>
 * 	</tr>
 *	<tr>
 * 		<td>VB, VBD, VBG, VBN, VB, VBZ</td>
 * 		<td>Verb</td>
 * 	</tr>
 * </table>
 * 
 * <br>
 * <br>
 * 
 * All other POS tags are tagged <i>Other</i>
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class POStoGenericPOSConverter {
	
	/** The mapping. */
	private static Map<String,String> mapping;
	
	static{
		mapping = new HashMap<String,String>();
		mapping.put("JJ", "Adjective");
		mapping.put("JJR", "Adjective");
		mapping.put("JJS", "Adjective");
		mapping.put("NN", "Noun");
		mapping.put("NNS", "Noun");
		mapping.put("NNP", "Noun");
		mapping.put("NNPS", "Noun");
		mapping.put("RB","Adverb");
		mapping.put("RBR","Adverb");
		mapping.put("RBS","Adverb");
		mapping.put("VB", "Verb");
		mapping.put("VBD", "Verb");
		mapping.put("VBG", "Verb");
		mapping.put("VBN","Verb");
		mapping.put("VBP","Verb");
		mapping.put("VBZ","Verb");
	}
	
	/**
	 * Converts the inputs POS tag to GPOS tag
	 *
	 * @param tag the input POS tag
	 * @return the the corresponding GPOS tag
	 */
	public static String convertPOS(String tag){
		String t = mapping.get(tag);
		if(t==null){
			return "Other";
		}else{
			return t;
		}
	}
}
