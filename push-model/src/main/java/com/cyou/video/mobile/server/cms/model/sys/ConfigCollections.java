package com.cyou.video.mobile.server.cms.model.sys;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cyou.video.mobile.server.common.IEnumDisplay;

@Document(collection = "Config_Collections")
public class ConfigCollections implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -200305217748433136L;

  @Id
  private String id;

  private String name;

  private String chName;

  private String code;

  private COLLECTION_TYPE type;

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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getChName() {
    return chName;
  }

  public void setChName(String chName) {
    this.chName = chName;
  }

  public COLLECTION_TYPE getType() {
    return type;
  }

  public void setType(COLLECTION_TYPE type) {
    this.type = type;
  }

  public enum COLLECTION_TYPE implements IEnumDisplay {
    DATA("数据收集", 0), STATICES("数据统计", 1);
    public String name;

    public int index;

    private COLLECTION_TYPE(String name, int index) {
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
