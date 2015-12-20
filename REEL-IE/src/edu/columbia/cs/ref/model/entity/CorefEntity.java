package edu.columbia.cs.ref.model.entity;

import edu.columbia.cs.ref.model.Document;

public class CorefEntity extends Entity {

	private Entity rootEntity;

	public CorefEntity(String id, String entityType, int offset, int length,
			String value, Document doc, Entity rootEntity) {
		super(id, entityType, offset, length, value, doc);
		this.rootEntity = rootEntity;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6173892634138734677L;

	@Override
	public String getValue() {
		return rootEntity.getValue();
	}
	
	public Entity getRootEntity(){
		return rootEntity;
	}
	
}
