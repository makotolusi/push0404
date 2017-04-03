package com.cyou.video.mobile.server.cms.model.user;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cyou.video.mobile.server.common.Constants;
import com.cyou.video.mobile.server.common.adapter.JaxbDatetimeAdapter;

/**
 * CMS用户令牌
 * 
 * @author jyz
 */
@XmlRootElement
public class UserToken  {

  private static final long serialVersionUID = 2063737012499058725L;

  private int id; // 令牌id

  private String token; // 令牌值

  private Date createDate = new Date(); // 创建时间

  private int plat; // 令牌来源

  private Date lastReqDate = new Date(); // 最近请求时间

  private int reqCount; // 请求次数

  private int status = Constants.STATUS.ON.getValue(); // 令牌状态

  private String os; // 操作系统

  private String ua = ""; // 设备信息

  private String res = ""; // 设备分辨率

  private String channel = ""; // 所属渠道

  private String mid = ""; // 设备标识

  private String baiduUserID; // baidu user id

  private String curUsedVersion; // 当前使用版本

  private Date lastVcTime; // 当version channel 更新时的最新时间戳
  
  private String email = "";
  
  private String mobileNumber = "";

  public String getBaiduUserID() {
    return baiduUserID;
  }

  public void setBaiduUserID(String baiduUserID) {
    this.baiduUserID = baiduUserID;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public int getPlat() {
    return plat;
  }

  public void setPlat(int plat) {
    this.plat = plat;
  }

  @XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
  public Date getLastReqDate() {
    return lastReqDate;
  }

  public void setLastReqDate(Date lastReqDate) {
    this.lastReqDate = lastReqDate;
  }

  public int getReqCount() {
    return reqCount;
  }

  public void setReqCount(int reqCount) {
    this.reqCount = reqCount;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getUa() {
    return ua;
  }

  public void setUa(String ua) {
    this.ua = ua;
  }

  public String getRes() {
    return res;
  }

  public void setRes(String res) {
    this.res = res;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getMid() {
    return mid;
  }

  public void setMid(String mid) {
    this.mid = mid;
  }

  public String getCurUsedVersion() {
    return curUsedVersion;
  }

  public void setCurUsedVersion(String curUsedVersion) {
    this.curUsedVersion = curUsedVersion;
  }

  public Date getLastVcTime() {
    return lastVcTime;
  }

  public void setLastVcTime(Date lastVcTime) {
    this.lastVcTime = lastVcTime;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  

}
