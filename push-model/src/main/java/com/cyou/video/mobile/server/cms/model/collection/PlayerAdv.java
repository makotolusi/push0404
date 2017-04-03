package com.cyou.video.mobile.server.cms.model.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PlayerAdv")
public class PlayerAdv {
	
	@Id
	private String id; // i d

	private String type; //目前只有'trdparty'

	private String state; //status:0表示停用，1表示启用
	
	private String advcode;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAdvcode() {
		return advcode;
	}

	public void setAdvcode(String advcode) {
		this.advcode = advcode;
	}



}
