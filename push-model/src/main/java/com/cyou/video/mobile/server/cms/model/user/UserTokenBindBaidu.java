package com.cyou.video.mobile.server.cms.model.user;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 用户token绑定百度 id
 * 
 * @author lusi
 */
@XmlRootElement
public class UserTokenBindBaidu  {

	private static final long serialVersionUID = 2063737012499058725L;

	private int id; // id

	private int tokenId; // 令牌ID

	private String baiduUserId; // baidu user id

	private String baiduChannelId; // baidu channel id

	private String baiduAppId; // baidu channel id

	private Date bindDate; // 创建时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTokenId() {
    return tokenId;
  }

  public void setTokenId(int tokenId) {
    this.tokenId = tokenId;
  }

  public String getBaiduUserId() {
    return baiduUserId;
  }

  public void setBaiduUserId(String baiduUserId) {
    this.baiduUserId = baiduUserId;
  }

  public String getBaiduChannelId() {
    return baiduChannelId;
  }

  public void setBaiduChannelId(String baiduChannelId) {
    this.baiduChannelId = baiduChannelId;
  }

  public String getBaiduAppId() {
    return baiduAppId;
  }

  public void setBaiduAppId(String baiduAppId) {
    this.baiduAppId = baiduAppId;
  }

  public Date getBindDate() {
		return bindDate;
	}

	public void setBindDate(Date bindDate) {
		this.bindDate = bindDate;
	}

	

}
