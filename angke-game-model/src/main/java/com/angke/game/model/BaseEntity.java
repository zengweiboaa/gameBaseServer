package com.angke.game.model;

import java.io.Serializable;

public class BaseEntity implements Serializable {

	protected Long id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "BaseModel [id=" + id + "]";
	}
	
	private static final long serialVersionUID = 1L;
}
