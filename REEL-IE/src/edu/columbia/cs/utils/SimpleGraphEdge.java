package edu.columbia.cs.utils;

public class SimpleGraphEdge <T> {
	private int origin;
	private int destiny;
	private T label;
	
	public SimpleGraphEdge(int origin, int destiny, T label){
		this.setOrigin(origin);
		this.setDestiny(destiny);
		this.setLabel(label);
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}

	public int getOrigin() {
		return origin;
	}

	public void setDestiny(int destiny) {
		this.destiny = destiny;
	}

	public int getDestiny() {
		return destiny;
	}
	
	public void setLabel(T lab){
		label=lab;
	}
	
	public T getLabel(){
		return label;
	}
}
