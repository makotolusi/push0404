package com.cyou.mbu.server.cms.model.ad;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cyou.video.mobile.server.common.Constants;
import com.cyou.video.mobile.server.common.adapter.JaxbDatetimeAdapter;

/**
 * 广告
 * 
 * @author jyz
 */
@XmlRootElement
public class Advertising  {

	private Integer id; // 广告id

	private String words; // 广告词
	
	private String name; // 广告名称

	private Integer picId; // 广告图

	private int status = Constants.STATUS.OFF.getValue(); // 广告状态

	private Date creDate = new Date();; // 创建日期

	private Date effDate; // 生效日期

	private Date expDate; // 失效日期

	private Integer type; // 广告类型

	private Integer position; // 广告位

	private Integer turn = 1; // 排序

	private Integer action; // 点击动作

	private String urlIos; // ios目标地址

	private String urlAndroid; // android目标地址

	private Integer app; // 应用标识
	
	private Integer showSecond;//全屏显示时间
	
	private String md5;
	
	private String packageName;
	
	private Integer imageTextType;//图文广告类型 1 为列表页图文广告  2 为详情页图文广告
	
	private String strategyIds;//攻略id用逗号分隔, id在此字符串里边的显示
	
	private Integer newsOrVideo;

	public Integer getImageTextType() {
		return imageTextType;
	}

	public void setImageTextType(Integer imageTextType) {
		this.imageTextType = imageTextType;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getTurn() {
		return turn;
	}

	public void setTurn(Integer turn) {
		this.turn = turn;
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public Integer getApp() {
		return app;
	}

	public void setApp(Integer app) {
		this.app = app;
	}

	public Integer getShowSecond() {
		return showSecond;
	}

	public void setShowSecond(Integer showSecond) {
		this.showSecond = showSecond;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}


	public Integer getPicId() {
		return picId;
	}

	public void setPicId(Integer picId) {
		this.picId = picId;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	@XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
	public Date getEffDate() {
		return effDate;
	}

	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

	@XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}



	public String getUrlIos() {
		return urlIos;
	}

	public void setUrlIos(String urlIos) {
		this.urlIos = urlIos;
	}

	public String getUrlAndroid() {
		return urlAndroid;
	}

	public void setUrlAndroid(String urlAndroid) {
		this.urlAndroid = urlAndroid;
	}


	

	public String getStrategyIds() {
		return strategyIds;
	}

	public void setStrategyIds(String strategyIds) {
		this.strategyIds = strategyIds;
	}

	public Integer getNewsOrVideo() {
		return newsOrVideo;
	}

	public void setNewsOrVideo(Integer newsOrVideo) {
		this.newsOrVideo = newsOrVideo;
	}
	
}
