package com.cyou.video.mobile.server.cms.model.collection;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PlayerApps")
public class PlayerApps  {

	@Id
	private String id; // i d
	
	private String appid;

	private String appName;
	
	private String appNameNew;
	
	private String localState; //0:非官方    1：官方
	
	private String advState;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getAppNameNew() {
		return appNameNew;
	}

	public void setAppNameNew(String appNameNew) {
		this.appNameNew = appNameNew;
	}

	public String getLocalState() {
		return localState;
	}

	public void setLocalState(String localState) {
		this.localState = localState;
	}
	
	public String getAdvState() {
		return advState;
	}

	public void setAdvState(String advState) {
		this.advState = advState;
	}



}
