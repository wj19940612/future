package com.lecloud.skin.entity;

import java.io.Serializable;
/**
 * 码率
 * @author pys
 *
 */
public class RateTypeItem implements Serializable{
	
	public RateTypeItem(){
	}
	public RateTypeItem(String name,int typeId){
		this.setName(name);
		this.setTypeId(typeId);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	private String name;
	private  int typeId;
}
