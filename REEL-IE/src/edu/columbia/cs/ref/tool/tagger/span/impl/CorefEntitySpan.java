package edu.columbia.cs.ref.tool.tagger.span.impl;

import java.util.List;

import edu.columbia.cs.ref.model.entity.Entity;

public class CorefEntitySpan extends EntitySpan {

	private Entity rootEntity;

	public CorefEntitySpan(String id, String tag, String value, int offset,
			int length, Entity rootEntity) {
		super(id, tag, value, offset, length);
		this.rootEntity = rootEntity;
	}

	public Entity getRootEntity(){
		return rootEntity;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode()*31;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof CorefEntitySpan){
			
			CorefEntitySpan other = (CorefEntitySpan)o;
			
			if (getOffset() != other.getOffset())
				return false;
			if (getLength() != other.getLength()){
				return false;
			}
			//The same text refers to the same entity. We should not check for others...
//			if (!getRootEntity().equals(other.getRootEntity())){
//				return false;
//			}
		}else{
			return false;
		}
		return true;
		
	}
}
