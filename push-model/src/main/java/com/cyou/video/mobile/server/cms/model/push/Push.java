package com.cyou.video.mobile.server.cms.model.push;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_JOB_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_PLATFORM_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_USER_SCOPE;
import com.cyou.video.mobile.server.cms.model.security.Manager;
import com.cyou.video.mobile.server.cms.model.sys.ContentType;
import com.cyou.video.mobile.server.common.adapter.JaxbDatetimeAdapter;
import com.cyou.video.mobile.server.common.adapter.JaxbEnumAdapter;

@Document(collection = "Push_3.0")
public class Push {

  @Id
  private String id;

  /**
   * 推送消息标题
   */
  private String title;
  private String titleColor;
  /**
   * 推送消息内容
   */
  private String content;
  private String contentColor;

  /**
   * 推送消息内容类型
   */
  private ContentType contentType;

  @Transient
  private String contentTy;

  @Transient
  private String clientTypes;

  private List<PushTagCollection> tags = new ArrayList<PushTagCollection>();

  private String tagRelation;// 标签关系

  private String sentLogs;

  /**
   * 任务状态
   */
  private PUSH_JOB_STATE jobState = PUSH_JOB_STATE.DISABLE;

  /**
   * 任务类型
   */
  private PUSH_TYPE pushType;

  /**
   * 收到通知后打开方式: "key1":"value1", "key2":"value2"
   */
  private Map<String, String> keyValue;

  /**
   * 时段控制
   */
  private Map<String, Integer> interval;

  /**
   * 客户端类型
   */
  private CLIENT_TYPE clientType;

  /**
   * 推送状态
   */
  private PUSH_SEND_STATE sendState;

  /**
   * 推送用户范围
   */
  private PUSH_USER_SCOPE userScope = PUSH_USER_SCOPE.ALL;

  /**
   * 任务创建时间
   */
  private Date sendDate = new Date();

  /**
   * 推送消息点击去向
   */
  @Transient
  private String target;

  /**
   * 定时任务时间规则
   */
  private String cronExp;

  /**
   * 是否删除
   */
  @Transient
  private UseYn useYn;

  /**
   * trigger start
   */
  private String startTime;

  /**
   * trigger name
   */
  @Transient
  private String tirggerName;

  /**
   * trigger next fire time
   */
  private String nextFireTime;

  /**
   * trigger previous time
   */
  private String previousFireTime;

  /**
   * trigger previous time
   */
  @Transient
  private String cronExpression;

  /**
   * user id
   */
  @Transient
  private String userId;

  @Transient
  private String channelId;

  private Integer appId;

  private Integer messageType=1;//1 noti 2 mess
  @Transient
  private String appKey;

  @Transient
  private String secretKey;

  @DBRef
  private Manager manager;

  /**
   * 推送第三方平台
   */
  private PUSH_PLATFORM_TYPE platForm;

  public Map<String, String> getKeyValue() {
    return keyValue;
  }

  /**
   * 是否删除
   * 
   * @author kevin
   * 
   */
  public enum UseYn {
    Y("在使用"), N("已删除");

    private String name;

    // 构造方法
    private UseYn(String name) {
      this.name = name;
    }

    // 覆盖方法
    @Override
    public String toString() {
      return this.name;
    }
  }

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  
  public Integer getMessageType() {
    return messageType;
  }

  public void setMessageType(Integer messageType) {
    this.messageType = messageType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<PushTagCollection> getTags() {
    return tags;
  }

  public void setTags(List<PushTagCollection> tags) {
    this.tags = tags;
  }

  public void setKeyValue(Map<String, String> keyValue) {
    this.keyValue = keyValue;
  }

  public String getSentLogs() {
    return sentLogs == null ? "" : sentLogs;
  }

  public void setSentLogs(String sentLogs) {
    this.sentLogs = sentLogs;
  }

  public CLIENT_TYPE getClientType() {
    return clientType;
  }

  public void setClientType(CLIENT_TYPE clientType) {
    this.clientType = clientType;
  }

  @XmlJavaTypeAdapter(JaxbEnumAdapter.class)
  public PUSH_USER_SCOPE getUserScope() {
    return userScope;
  }

  public void setUserScope(PUSH_USER_SCOPE userScope) {
    this.userScope = userScope;
  }

  @XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
  public Date getSendDate() {
    return sendDate;
  }

  public void setSendDate(Date sendDate) {
    this.sendDate = sendDate;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getCronExp() {
    return cronExp;
  }

  public void setCronExp(String cronExp) {
    this.cronExp = cronExp;
  }

  public UseYn getUseYn() {
    return useYn;
  }

  public void setUseYn(UseYn useYn) {
    this.useYn = useYn;
  }

  public PUSH_JOB_STATE getJobState() {
    return jobState;
  }

  public void setJobState(PUSH_JOB_STATE jobState) {
    this.jobState = jobState;
  }

  @XmlJavaTypeAdapter(JaxbEnumAdapter.class)
  public PUSH_SEND_STATE getSendState() {
    return sendState;
  }

  public void setSendState(PUSH_SEND_STATE sendState) {
    this.sendState = sendState;
  }

  // @XmlJavaTypeAdapter(JaxbEnumAdapter.class)
  public PUSH_TYPE getPushType() {
    return pushType;
  }

  // @ (JaxbEnumAdapter.class)
  public void setPushType(PUSH_TYPE pushType) {
    this.pushType = pushType;
  }

  public String getClientTypes() {
    return clientTypes;
  }

  public void setClientTypes(String clientTypes) {
    this.clientTypes = clientTypes;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getTirggerName() {
    return tirggerName;
  }

  public void setTirggerName(String tirggerName) {
    this.tirggerName = tirggerName;
  }

  public String getNextFireTime() {
    return nextFireTime;
  }

  public void setNextFireTime(String nextFireTime) {
    this.nextFireTime = nextFireTime;
  }

  public String getPreviousFireTime() {
    return previousFireTime;
  }

  public void setPreviousFireTime(String previousFireTime) {
    this.previousFireTime = previousFireTime;
  }

  /**
   * @return the cronExpression
   */
  public String getCronExpression() {
    return cronExpression;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getContentTy() {
    return contentTy;
  }

  public void setContentTy(String contentTy) {
    this.contentTy = contentTy;
  }

  public Integer getAppId() {
    return appId;
  }

  public void setAppId(Integer appId) {
    this.appId = appId;
  }

  public PUSH_PLATFORM_TYPE getPlatForm() {
    return platForm;
  }

  public void setPlatForm(PUSH_PLATFORM_TYPE platForm) {
    this.platForm = platForm;
  }

  public Map<String, Integer> getInterval() {
    return interval;
  }

  public void setInterval(Map<String, Integer> interval) {
    this.interval = interval;
  }

  public ContentType getContentType() {
    return contentType;
  }

  public void setContentType(ContentType contentType) {
    this.contentType = contentType;
  }

  public String getTagRelation() {
    if(tags.size() <= 1)
      return "OR";
    else
      return tagRelation;
  }

  public void setTagRelation(String tagRelation) {
    this.tagRelation = tagRelation;
  }

  /**
   * @param cronExpression
   *          the cronExpression to set
   */
  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public Manager getManager() {
    return manager;
  }

  public void setManager(Manager manager) {
    this.manager = manager;
  }

  
  public String getTitleColor() {
    return titleColor;
  }

  public void setTitleColor(String titleColor) {
    this.titleColor = titleColor;
  }

  public String getContentColor() {
    return contentColor;
  }

  public void setContentColor(String contentColor) {
    this.contentColor = contentColor;
  }

  @Override
  public String toString() {
    return "Push [id=" + id + ", title=" + title + ", content=" + content + ", contentType=" + contentType
        + ", contentTy=" + contentTy + ", clientTypes=" + clientTypes + ", tags=" + tags + ", tagRelation="
        + tagRelation + ", sentLogs=" + sentLogs + ", jobState=" + jobState + ", pushType=" + pushType + ", keyValue="
        + keyValue + ", interval=" + interval + ", clientType=" + clientType + ", sendState=" + sendState
        + ", userScope=" + userScope + ", sendDate=" + sendDate + ", target=" + target + ", cronExp=" + cronExp
        + ", useYn=" + useYn + ", startTime=" + startTime + ", tirggerName=" + tirggerName + ", nextFireTime="
        + nextFireTime + ", previousFireTime=" + previousFireTime + ", cronExpression=" + cronExpression + ", userId="
        + userId + ", channelId=" + channelId + ", appId=" + appId + ", appKey=" + appKey + ", secretKey=" + secretKey
        + ", platForm=" + platForm + "]";
  }

  public Push clone() {
    Push push = null;
    try {
      push = (Push) super.clone();
    }
    catch(CloneNotSupportedException e) {
      e.printStackTrace();
    }

    return push;
  }

}
