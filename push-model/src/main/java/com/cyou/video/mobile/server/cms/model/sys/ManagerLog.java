package com.cyou.video.mobile.server.cms.model.sys;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cyou.video.mobile.server.common.adapter.JaxbDatetimeAdapter;

/**
 * 用户日志表
 * 
 * @author zs
 */
@XmlRootElement
public class ManagerLog {
  private int id;// id

  private int type;// 操作类型：1，增 2，删 3， 改

  private String name="";// 作操名字

  private String desc= "";// 作操描述

  private String params;// 请求参数

  private String url="";// 求请地址

  private String modelName="";// 于属哪个模块

  private Date createDate = new Date();// 入录时间

  private String createUser="";// 作操人名姓

  private String userIp="";// 操作人的ip地址

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  @XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getUserIp() {
    return userIp;
  }

  public void setUserIp(String userIp) {
    this.userIp = userIp;
  }

  

}
