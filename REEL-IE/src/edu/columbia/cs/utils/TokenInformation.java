package edu.columbia.cs.utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The Class TokenInformation is a container for the information stored about a token
 * in a sentence used by the Dependency Graph. That information consists of: a list of
 * string-based features, an indicator of whether the token corresponds to entity 1 or
 * entity 2 and information of wheter the token is in the shortest path between the two
 * entities in the dependency graph.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class TokenInformation implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2805314937535969201L;
	
	/** The features. */
	private List<String> features;
	
	/** The is in shortest path. */
	private boolean isInShortestPath = false;
	
	/** The is entity1. */
	private boolean isEntity1=false;
	
	/** The is entity2. */
	private boolean isEntity2=false;

	/**
	 * Instantiates a new token information.
	 */
	public TokenInformation(){
		features=new ArrayList<String>();
	}
	
	/**
	 * Adds the a string-based feature to the token
	 *
	 * @param feature the new string-based feature
	 */
	public void add(String feature){
		features.add(feature);
	}
	
	/**
	 * Gets the similarity the similarity with the input token.
	 * 
	 * <br>
	 * <br>
	 * 
	 * The similarity is given by:
	 * 
	 * <br>
	 * <br>
	 * 
	 * (i) if one token is in the shortest path ant the other isn't then the similarity is 0
	 * 
	 * <br>
	 * 
	 * (ii) if one token is the first entity of the sentence and the other is not then the similarity is 0
	 * 
	 * <br>
	 * 
	 * (iii) if one token is the second entity of the sentence and the other is not then the similarity is 0
	 *
	 * <br>
	 * 
	 * (iv) otherwise the similarity is given by the common number of string-based features divided by the total number of string-based features
	 * 
	 * @param other the other
	 * @return the similarity
	 */
	public double getSimilarity(TokenInformation other){
		if(isInShortestPath!=other.isInShortestPath){
			return 0;
		}
		
		if(isEntity1()!=other.isEntity1()){
			return 0;
		}
		
		if(isEntity2()!=other.isEntity2()){
			return 0;
		}
		
		if(!isEntity1() && !isEntity2()){
			int sizeFeats=features.size();
			int inner=0;
			for (int i = 0; i < sizeFeats; i++){
				if(features.get(i).equals(other.features.get(i))){
					inner++;
				}
			}
			
			return inner/Math.sqrt(sizeFeats*sizeFeats);
		}
		
		int numFeats1=0;
		int numFeats2=0;
		double inner=0;
		int sizeFeats=features.size();		
		for(int i=0; i<sizeFeats; i++){
			if(features.get(i).equals(other.features.get(i))){
				inner++;
			}
		}
			
		
		numFeats1+=features.size();
		numFeats2+=other.features.size();
		
		return inner/Math.sqrt(numFeats1*numFeats2);
	}
	
	/**
	 * Returns the list of string-based features
	 *
	 * @return the list of string-based features
	 */
	public List<String> getOwnFeatures(){
		return features;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return features + "|" + isEntity1 + "|" + isEntity2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof TokenInformation){
			TokenInformation other =(TokenInformation) o;

			if(isInShortestPath!=other.isInShortestPath){
				return false;
			}
			
			if(isEntity1()!=other.isEntity1()){
				return false;
			}
			
			if(isEntity2()!=other.isEntity2()){
				return false;
			}
			
			for(int i=0; i<features.size(); i++){
				if(!features.get(i).equals(other.features.get(i))){
					return false;
				}
			}

			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return features.get(0).hashCode();
	}

	/**
	 * Sets the indication that the token is in the shortest path.
	 *
	 * @param isInShortestPath true if the token is in the shortest path and false if it isn't
	 */
	public void setInShortestPath(boolean isInShortestPath) {
		this.isInShortestPath = isInShortestPath;
	}

	/**
	 * Checks if the token is in the shortest path.
	 *
	 * @return true if the token is in the shortest path and false if it isn't
	 */
	public boolean isInShortestPath() {
		return isInShortestPath;
	}
	
	/**
	 * Sets the indication that the token is the first entity
	 *
	 * @param v true if it is and false if it isn't
	 * @param type the type
	 */
	public void setIsEntity1(boolean v, String type){
		isEntity1=v;
	}
	
	/**
	 * Sets the indication that the token is the second entity
	 *
	 * @param v true if it is and false if it isn't
	 * @param type the type
	 */
	public void setIsEntity2(boolean v, String type){
		isEntity2=v;
		
	}
	
	/**
	 * Checks if the token is the first entity.
	 *
	 * @return true, if is the first entity.
	 */
	public boolean isEntity1(){
		return isEntity1;
	}
	
	/**
	 * Checks if the token is the second entity.
	 *
	 * @return true, if is the second entity.
	 */
	public boolean isEntity2(){
		return isEntity2;
	}
	
	/**
	 * Returns a copy of this token information without the information of being in the shortest path
	 *
	 * @return a copy of this token information without the information of being in the shortest path
	 */
	public TokenInformation copyNoShortestPath(){
		TokenInformation result = new TokenInformation();
		result.features=features;
		result.isEntity1=isEntity1;
		result.isEntity2=isEntity2;
		return result;
	}
}
