package com.cyou.video.mobile.server.cms.model.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PlayerAvList")
public class PlayerAvList  {
	
	@Id
	private String id; // i d

	private String appid;

	private int pv;
	
	private int uv;
	
	private int vv;
	
	private int time;
	
	private String statdate;
	
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

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getUv() {
		return uv;
	}

	public void setUv(int uv) {
		this.uv = uv;
	}
	
	public int getVv() {
		return vv;
	}

	public void setVv(int vv) {
		this.vv = vv;
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getStatdate() {
		return statdate;
	}

	public void setStatdate(String statdate) {
		this.statdate = statdate;
	}
	
	

}
