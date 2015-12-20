package edu.columbia.cs.utils;
import java.io.Serializable;


public class SimpleGraphNode <T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3982299438910566239L;
	private T label;
	
	public SimpleGraphNode(T label) {
		this.setLabel(label);
	}

	public void setLabel(T label) {
		this.label = label;
	}

	public T getLabel() {
		return label;
	}
	
	public String toString(){
		return label.toString();
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof SimpleGraphNode){
			return label.equals(((SimpleGraphNode) o).label);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return label.hashCode();
	}
}
