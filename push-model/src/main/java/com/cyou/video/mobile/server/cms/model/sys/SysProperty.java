package com.cyou.video.mobile.server.cms.model.sys;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 系统参数表
 * @author zs
 */
@XmlRootElement
public class SysProperty{
	
	private int id ; //序列号
	
	private String desc; //参数描述
	
	private String key; //参数键
	
	private String value; //参数值
	
	private String version; //系统版本号
	
	private int type; //参数使用者
	
	private int status; //参数状态
	
	private int plat; //参数使用平台
	
	private String channel = ""; //渠道标识
	
	private int appType;//所属哪种app 1:173app；2:最强攻略app
	
	private String appSx;//app缩写的list字符串
	
	private List<String> appSxList = new ArrayList<String>();//app缩写
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPlat() {
		return plat;
	}

	public void setPlat(int plat) {
		this.plat = plat;
	}
	
	public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

	public int getAppType() {
    return appType;
  }

  public void setAppType(int appType) {
    this.appType = appType;
  }
  
  public List<String> getAppSxList() {
    return appSxList;
  }

  public void setAppSxList(List<String> appSxList) {
    this.appSxList = appSxList;
  }

  public String getAppSx() {
    return appSx;
  }

  public void setAppSx(String appSx) {
    this.appSx = appSx;
    this.appSxList = Arrays.asList(appSx.split(","));
  }

  

	
}
