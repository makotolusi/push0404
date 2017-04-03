package com.cyou.video.mobile.server.cms.model.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cyou.video.mobile.server.common.Constants;

/**
 * CMS管理员类
 * 
 * @author lusi
 */
@Document(collection = "Security_Manager")
public class Manager {

  private static final long serialVersionUID = -79915748798890256L;

  @Id
  private String id; // 管理员id

  private String username; // 登录用户名

  private String password; // 登录密码

  private String email; // 邮箱地址

  private Constants.STATUS status = Constants.STATUS.ON; // 管理员状态

  @DBRef
  private List<Role> roles = new ArrayList<Role>();

  @Transient
  List<String> roleids;

  @Transient
  Integer appId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Constants.STATUS getStatus() {
    return status;
  }

  public void setStatus(Constants.STATUS status) {
    this.status = status;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public List<String> getRoleids() {
    return roleids;
  }

  public void setRoleids(List<String> roleids) {
    this.roleids = roleids;
  }



  public Integer getAppId() {
    return appId;
  }

  public void setAppId(Integer appId) {
    this.appId = appId;
  }


}
