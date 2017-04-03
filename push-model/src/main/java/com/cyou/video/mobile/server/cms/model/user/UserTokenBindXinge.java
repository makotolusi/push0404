package com.cyou.video.mobile.server.cms.model.user;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户token绑定百度 id
 * 
 * @author lusi
 */
@Document(collection = "UserTokenBindXinge")
public class UserTokenBindXinge{

  private static final long serialVersionUID = 2063737012499058725L;

  @Id
  private String id; // id

  private int tokenId; // 令牌ID
  
  private String tokenStr;
  
  private int platForm;

  private long accessId; // accessId

  private String deviceId; // deviceId

  private String account; // ticket
  
  private String ticket; // ticket

  private String ticketType; // ticketType

  private String xgToken; // token

  private String appId; // token
  
  private Date bindDate=new Date(); // 创建时间

  


  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getTokenId() {
    return tokenId;
  }

  public void setTokenId(int tokenId) {
    this.tokenId = tokenId;
  }

  public long getAccessId() {
    return accessId;
  }

  public void setAccessId(long accessId) {
    this.accessId = accessId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public String getTicketType() {
    return ticketType;
  }

  public void setTicketType(String ticketType) {
    this.ticketType = ticketType;
  }

  public String getXgToken() {
    return xgToken;
  }

  public void setXgToken(String xgToken) {
    this.xgToken = xgToken;
  }

  public Date getBindDate() {
    return bindDate;
  }

  public void setBindDate(Date bindDate) {
    this.bindDate = bindDate;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getTokenStr() {
    return tokenStr;
  }

  public void setTokenStr(String tokenStr) {
    this.tokenStr = tokenStr;
  }

  public int getPlatForm() {
    return platForm;
  }

  public void setPlatForm(int platForm) {
    this.platForm = platForm;
  }

  

}
