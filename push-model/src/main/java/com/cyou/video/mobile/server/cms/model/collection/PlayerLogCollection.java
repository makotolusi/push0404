package com.cyou.video.mobile.server.cms.model.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 播放器信息收集
 * 
 * @author duxiaona
 */
@Document(collection = "PlayerLog")
public class PlayerLogCollection  {
	/**
	 *  "appid": "appID", //可用bundle identifier（必填）
      "appname": "17173最强攻略", // app名称
      "type": "pv", //结点提交类型 pv:访问统计类型 av：广告统计类型（必填）
      "uid": "12332", // 用户唯一标志
      "ua": "v2.0(iphone)", //用户代理,版本号+机型
      "playtime": "1000", // 播放时长 单位 毫秒 （必填）
      "duration": "2000", //总时长 单位 毫秒
      "src": "http://17173.tv.sohu.com/967037.m3u8 ", // 视频播放url
      "br": "60", //码率
      "res": "400*600", // 分辨率
      "fmt": "m3u8", //总格式
      "afmt": "MPEG4 AAC", //音频编码格式
      "vfmt": "Apple MPEG-4(apple series)", //视频编码格式
      "vpixfmt": "MEDIASUBTYPE_RGB1", //视频颜色空间
      "vrefs": "2000", //参考帧数（仅AVC）
      "vprofile": "Baseline Profile", //profile等级（仅AVC）

	 */

	@Id
	private String id; // i d

	private String appid; 
	
	private String appname; 
	
	private String type; 
	
	private String uid; 
	
	private String ua; 
	
	private String playtime; 
	
	private String duration; 
	
	private String src; 
	
	private String br; 
	
	private String res; 
	
	private String fmt; 
	
	private String afmt; 
	
	private String vfmt; 
	
	private String vpixfmt; 
	
	private String vrefs; 
	
	private String vprofile; 
	
	private String lastUpdate;
	
	

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
	
	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}
	
	public String getPlaytime() {
		return playtime;
	}

	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}
	
	public String getBr() {
		return br;
	}

	public void setBr(String br) {
		this.br = br;
	}
	
	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}
	
	public String getFmt() {
		return fmt;
	}

	public void setFmt(String fmt) {
		this.fmt = fmt;
	}
	
	public String getAfmt() {
		return afmt;
	}

	public void setAfmt(String afmt) {
		this.afmt = afmt;
	}
	
	public String getVfmt() {
		return vfmt;
	}

	public void setVfmt(String vfmt) {
		this.vfmt = vfmt;
	}
	
	public String getVpixfmt() {
		return vpixfmt;
	}

	public void setVpixfmt(String vpixfmt) {
		this.vpixfmt = vpixfmt;
	}

	public String getVrefs() {
		return vrefs;
	}

	public void setVrefs(String vrefs) {
		this.vrefs = vrefs;
	}
	
	public String getVprofile() {
		return vprofile;
	}

	public void setVprofile(String vprofile) {
		this.vprofile = vprofile;
	}
	
	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}


	

}
