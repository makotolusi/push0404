package com.cyou.video.mobile.server.cms.model.security;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cyou.video.mobile.server.common.IEnumDisplay;

/**
 * 角色类
 * 
 * @author lusi
 */
@Document(collection = "Security_Role")
public class Role {

  @Id
  private String id; // 角色id

  private String name; // 角色名称

  private ROLE_TYPE roleType = ROLE_TYPE.NORMAL;

  private List<Integer> appIds;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ROLE_TYPE getRoleType() {
    return roleType;
  }

  public void setRoleType(ROLE_TYPE roleType) {
    this.roleType = roleType;
  }


  public List<Integer> getAppIds() {
    return appIds;
  }

  public void setAppIds(List<Integer> appIds) {
    this.appIds = appIds;
  }

  public enum ROLE_TYPE implements IEnumDisplay {
    ADMIN("攻略视频", 1), NORMAL("攻略新闻", 2);
    public String name;

    public int index;

    private ROLE_TYPE(String name, int index) {
      this.name = name;
      this.index = index;
    }

    public String getName() {
      return this.name;
    }

    public int getIndex() {
      return this.index;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

}
