package com.cyou.video.mobile.server.cms.service.push.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_JOB_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_TAG_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_USER_SCOPE;
import com.cyou.video.mobile.server.cms.model.Pagination;
import com.cyou.video.mobile.server.cms.model.VerifyException;
import com.cyou.video.mobile.server.cms.model.collection.ClientLogCollection;
import com.cyou.video.mobile.server.cms.model.collection.PushTagExcuteStateInfo;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo2;
import com.cyou.video.mobile.server.cms.model.push.Push;
import com.cyou.video.mobile.server.cms.model.push.PushTagCollection;
import com.cyou.video.mobile.server.cms.model.sys.ContentType;
import com.cyou.video.mobile.server.cms.service.push.PushInterface;
import com.cyou.video.mobile.server.cms.service.push.PushService;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.cms.service.utils.DateUtils;
import com.cyou.video.mobile.server.cms.service.utils.HttpUtils;
import com.cyou.video.mobile.server.common.Constants;
import com.cyou.video.mobile.server.common.utils.HttpUtil;
import com.cyou.video.mobile.server.common.utils.JacksonUtil;
import com.tencent.xinge.XingeApp;

/**
 * 意见反馈业务实现
 * 
 * @author jyz
 */
@Service("pushService")
public class PushServiceImpl implements PushService {

  private Logger logger = LoggerFactory.getLogger(PushServiceImpl.class);

  @Autowired
  private SystemConfigService systemConfigService;

  @Autowired
  PushInterface xingePush;

  @Autowired
  private MongoOperations mongoTemplate;

  @Override
  public String createPush(Push push) {
    try {
      if(push == null) {
        throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
            Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_push");
      }
      mongoTemplate.insert(push);
      return push.getId();
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void gameSwitch(Push push, int appId, ContentType contentType) throws DataNotFoundException {
    if(appId == Integer.parseInt(systemConfigService.getByKey("tag_switch"))) {
      if(!StringUtils.isEmpty(contentType.getTagConstant())) {
        PushTagCollection tag = new PushTagCollection();
        tag.setTagId(contentType.getTagConstant());
        tag.setTagName(contentType.getName());
        push.getTags().add(tag);
      }
    }
  }

  @Override
  public String savePush(Push push) {
    try {
      if(push == null) {
        throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
            Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_push");
      }
      mongoTemplate.save(push);
      return push.getId();
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public int updatePush(Push push) {
    try {
      if(push == null) {
        throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
            Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_push");
      }
//      mongoTemplate.updateFirst(
//          new Query().addCriteria(new Criteria("id").is(push.getId())),
//          new Update().set("title", push.getTitle()).set("content", push.getContent()).set("tags", push.getTags())
//              .set("clientType", push.getClientType()).set("userScope", push.getUserScope()), Push.class);
      // pushDao.updatePush(push);
      return 1;
    }
    catch(Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  @Override
  public void readPush(int id) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public Push getPushById(String id) throws Exception {
    Push p = mongoTemplate.findOne(new Query().addCriteria(new Criteria("_id").is(id)), Push.class);
    if(p.getPushType() == Consts.PUSH_TYPE.TIMING) {
      try {
        Map<String, String> map = this.postGetTriggerInfo(p.getId());
        if(map != null) {
          p.setTirggerName(map.get("name"));
          p.setStartTime(map.get("startTime"));
          p.setPreviousFireTime(map.get("previousFireTime"));
          p.setNextFireTime(map.get("nextFireTime"));
          p.setCronExpression(map.get("cronExpression"));
        }
      }
      catch(Exception e) {
        e.printStackTrace();
        logger.error("get job info failed :" + e.getMessage());

      }
    }
    return p;
  }

  @Override
  public Push getPushByPreId(int preId) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * 调用cms-job创建新quartz任务
   * 
   * @throws HttpException
   * @throws IOException
   */
  @Override
  public JSONObject postNewJob(String pushId, String cronExpress) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    params.put("expression", cronExpress);
    params.put("pushId", pushId);
    String str = HttpUtil.syncPost(systemConfigService.getByKey("job_url") + "/job/push/newJob", params, null);
    return new JSONObject(str);
  }

  /**
   * 调用cms-job删除quartz任务
   * 
   * @param pushId
   *          push编号
   * @return
   * @throws Exception
   */
  @Override
  public JSONObject deleteJob(String pushId) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    params.put("pushId", pushId + "");
    String str = HttpUtil.syncPost(systemConfigService.getByKey("job_url") + "/job/push/deleteJob", params, null);
    return new JSONObject(str);
  }

  /**
   * 调用cms-job停用quartz任务
   * 
   * @param pushId
   *          push编号
   * @return
   * @throws Exception
   */
  private String pauseJob(String pushId) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    params.put("pushId", pushId + "");
    return HttpUtil.syncPost(systemConfigService.getByKey("job_url") + "/job/push/pauseJob", params, null);
  }

  /**
   * 调用cms-job启用quartz任务
   * 
   * @param pushId
   *          push编号
   * @return
   * @throws Exception
   */
  private String resumeJob(String pushId) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    params.put("pushId", pushId + "");
    return HttpUtil.syncPost(systemConfigService.getByKey("job_url") + "/job/push/resumeJob", params, null);
  }

  /**
   * 调用cms-job修改quartz任务的cron表达式
   * 
   * @param pushId
   *          push编号
   * @param expression
   *          cron表达式
   * @return
   * @throws Exception
   */
  @Override
  public JSONObject modifyJob(String pushId, String expression) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    params.put("pushId", pushId + "");
    params.put("expression", expression + "");
    String str = HttpUtil.syncPost(systemConfigService.getByKey("job_url") + "/job/push/updateTrigger", params, null);
    return new JSONObject(str);
  }

  /**
   * 调用cms-job获得quartz任务信息
   * 
   * @param pushId
   *          push编号
   * @return
   * @throws Exception
   */
  public Map<String, String> postGetTriggerInfo(String pushId) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    params.put("pushId", pushId + "");
    String result = HttpUtil.syncPost(systemConfigService.getByKey("job_url") + "/job/push/getTriggerInfo", params,
        null);
    if(StringUtils.isEmpty(result)) {
      logger.info("job return msg is blank push id is " + pushId);
      return null;
    }
    else {
      @SuppressWarnings("unchecked")
      Map<String, String> map = JacksonUtil.getJsonMapper().readValue(result, Map.class);
      return map;
    }
  }

  @Override
  public Pagination listApp(Map<String, Object> params) throws Exception {
    Pagination pagination = new Pagination();
    int curPage = Integer.parseInt(params.get("curPage").toString());
    int pageSize = Integer.parseInt(params.get("pageSize").toString());
    curPage = (curPage - 1) * pageSize;
    Query query = new Query();
    if(!StringUtils.isEmpty((String) params.get("uid"))) {
      query.addCriteria(new Criteria("uid").is(params.get("uid")));
    }
    if(!StringUtils.isEmpty((String) params.get("itemType"))) {
      query.addCriteria(new Criteria("itemType").is(params.get("itemType")));
    }
    query.limit(pageSize);
    query.skip(curPage);
    query.with(new Sort(Sort.Direction.DESC, "operatorDate"));
    int count1 = (int) mongoTemplate.count(query, ClientLogCollection.class);
    List<ClientLogCollection> result = mongoTemplate.find(query, ClientLogCollection.class);
    pagination.setRowCount(count1);
    pagination.setContent(result);
    return pagination;
  }

  // private void setResult(List<UserItemOperatePvMongo2> u1,
  // List<ClientLogCollection> result) {
  // for(Iterator iterator = u1.iterator(); iterator.hasNext();) {
  // UserItemOperatePvMongo2 uio = (UserItemOperatePvMongo2) iterator.next();
  // ClientLogCollection c=new ClientLogCollection();
  // c.setServiceId(uio.getId().getServiceId());
  // c.setServiceName(uio.getValue().getServiceName());
  // c.setGameCode(uio.getValue().getGameCode());
  // c.setGameStatus(uio.getValue().getGameStatus());
  // c.setGameType(uio.getValue().getGameType());
  // c.setGamePlatForm(uio.getValue().getGamePlatForm());
  // c.setItemType(uio.getValue().getItemType());
  // c.setOperatorType(uio.getId().getOperatorType());
  // c.setUploadDate(uio.getValue().getUploadDate());
  // c.setOperatorDate(uio.getValue().getOperatorDate());
  // result.add(c);
  // }
  // }

  @Override
  public Pagination listPush(Map<String, Object> params) throws Exception {
    Pagination pagination = new Pagination();
    if(!org.springframework.util.StringUtils.isEmpty(params.get("curPage"))) {
      pagination.setCurPage(Integer.parseInt(params.get("curPage").toString()));
      pagination.setPageSize(Integer.parseInt(params.get("pageSize").toString()));
    }
    Query q = this.getQuery(params, pagination);
    pagination.setRowCount((int) mongoTemplate.count(q, Push.class));
    if(!org.springframework.util.StringUtils.isEmpty(params.get("curPage"))) {
      q.limit(pagination.getPageSize());
      q.skip(pagination.getStartRow() - 1);
    }
    List<Push> pushs = mongoTemplate.find(q, Push.class);
    pagination.setContent(pushs);
    return pagination;
  }

  public Query getQuery(Map<String, Object> params, Pagination pagination) throws Exception {
    Query query = new Query();
    if(!StringUtils.isEmpty((String) params.get("platForm"))) {
      query.addCriteria(new Criteria("platForm").is(params.get("platForm")));
    }
    if(params.get("appId") != null) {
      query.addCriteria(new Criteria("appId").is(Integer.parseInt(params.get("appId").toString())));
    }
    if(!StringUtils.isEmpty((String) params.get("title"))) {
      Pattern pattern = Pattern.compile("^.*" + params.get("title").toString() + ".*$");
      query.addCriteria(Criteria.where("title").regex(pattern));
    }
    if(!StringUtils.isEmpty((String) params.get("clientType"))) {
      query.addCriteria(new Criteria("clientType").is(params.get("clientType")));
    }
    if(!StringUtils.isEmpty((String) params.get("jobState"))) {
      query.addCriteria(new Criteria("jobState").is(params.get("jobState")));
    }
    if(!StringUtils.isEmpty((String) params.get("pushType"))) {
      query.addCriteria(new Criteria("pushType").is(params.get("pushType")));
    }
    else {
      // query.addCriteria(new Criteria("pushType").ne("AUTO"));
    }
    if(!StringUtils.isEmpty((String) params.get("contentTypeName"))) {
      query.addCriteria(new Criteria("contentType.name").is(params.get("contentTypeName")));
    }
    if(!StringUtils.isEmpty((String) params.get("contentTypeEn"))) {
      query.addCriteria(new Criteria("contentType.desc").is(params.get("contentTypeEn")));
    }
    if(!StringUtils.isEmpty((String) params.get("sendState"))) {
      query.addCriteria(new Criteria("sendState").is(params.get("sendState")));
    }
    if(!StringUtils.isEmpty((String) params.get("userScope"))) {
      query.addCriteria(new Criteria("userScope").is(params.get("userScope")));
    }
    if(params.get("tags") != null) {
      List l = (List) params.get("tags");
      query.addCriteria(new Criteria("tags.tagId").in(l));
    }
    if(!org.springframework.util.StringUtils.isEmpty(params.get("start"))) {
      query.limit(Integer.parseInt(params.get("limit").toString()));
      query.skip(Integer.parseInt(params.get("start").toString()));
    }
    query.with(new Sort(Sort.Direction.DESC, "sendDate"));
    return query;
  }

  @Override
  public void modifyStateById(String id, PUSH_JOB_STATE state) throws Exception {
//    if(state == PUSH_JOB_STATE.DISABLE) {
//      if(pauseJob(id).indexOf("true") >= 0)
////        mongoTemplate.updateFirst(new Query().addCriteria(new Criteria("id").is(id)),
////            new Update().set("jobState", state), Push.class);
//    }
//    else {
//      if(resumeJob(id).indexOf("true") >= 0)
////        mongoTemplate.updateFirst(new Query().addCriteria(new Criteria("id").is(id)),
////            new Update().set("jobState", state), Push.class);
//    }
  }

  @Override
  public void modifyAutoPushStateById(int id, PUSH_JOB_STATE state) throws Exception {
    // pushDao.updateStateById(id, state);
  }

  @Override
  public void modifyAutoPushStateById(String id, PUSH_JOB_STATE state) throws Exception {
//    mongoTemplate.updateFirst(new Query().addCriteria(new Criteria("_id").is(id)), new Update().set("jobState", state),
//        Push.class);
  }

  @Override
  public void updateSendStateById(Push push) {
    try {
//      mongoTemplate.updateFirst(new Query().addCriteria(new Criteria("id").is(push.getId())),
//          new Update().set("sendState", push.getSendState()).set("sentLogs", push.getSentLogs()), Push.class);
    }
    catch(Exception e) {
      logger.error("update send state erro" + e.getMessage());
    }
  }

  @Override
  public void saveJob(Push push) {
    try {
      mongoTemplate.save(push);
    }
    catch(Exception e) {
      logger.error("update send state erro" + e.getMessage());
    }
  }

  @Override
  public void deletePush(String id) throws Exception {
    Push push = new Push();
    push.setId(id);
    mongoTemplate.remove(push);
  }

  @Override
  public void autoPush(Push push) throws Exception {
    push.setPushType(PUSH_TYPE.AUTO);
    xingePush.pushTag(push);
  }

  public void autoPush(String title, String content, CLIENT_TYPE clientType, String gameCode, String serviceId)
      throws Exception {
    Push push = new Push();
    push.setTitle(title);
    push.setClientType(clientType);
    push.setContent(content);
    push.setPushType(PUSH_TYPE.AUTO);
    this.autoPush(push);
  }

  @Override
  public Push pushInfo(Push push) throws Exception {
    boolean flag = app173(push);
    if(flag == false) switch(push.getUserScope()) {
      case ALL :
        return xingePush.pushAll(push);
      case TAG :
        return xingePush.pushTag(push);
      default :
        break;
    }
    return push;
  }

  @Override
  public boolean app173(Push push) throws DataNotFoundException, Exception {
    boolean flag = false;
    String n = systemConfigService.getByKey("version_old_notification");
    if(push.getAppId() == Integer.parseInt(systemConfigService.getByKey("sys_173app_id"))) {
      if(push.getClientType() == CLIENT_TYPE.ANDROID) {
        if("-1".equals(n)) {// mess
          push.setMessageType(2);// noti
        }
        else {
          if(Integer.parseInt(push.getContentType().getIndex()) == 28) {
            Map<String, String> keyValue = push.getKeyValue();
            String s = systemConfigService.getByKey("show_download_android");
            JSONObject jsonObject = new JSONObject(s);
            Iterator it = jsonObject.keys();
            while(it.hasNext()) {
              String key = String.valueOf(it.next());
              String value = (String) jsonObject.get(key);
              keyValue.put(key, value);
            }
            push.setKeyValue(keyValue);
          }
          List<PushTagCollection> oldTag = new ArrayList<PushTagCollection>();
          oldTag.addAll(push.getTags());
          String tagR = push.getTagRelation();
          push.setUserScope(PUSH_USER_SCOPE.TAG);
          push.setMessageType(1);
          flag = tags(push, oldTag, "version_old_notification");
          push.setMessageType(2);// mess
          push.getTags().clear();
          flag = tags(push, oldTag, "version_new_message");
          if(oldTag.isEmpty()) {
            push.getTags().clear();
            push.setUserScope(PUSH_USER_SCOPE.ALL);
          }
          else {
            push.setTags(oldTag);
            push.setTagRelation(tagR);
          }
          if(flag == false) {
            push.setMessageType(2);// mess
          }

        }
      }
      else {
        if(Integer.parseInt(push.getContentType().getIndex()) == 28) {
          Map<String, String> keyValue = push.getKeyValue();
          try {
            String s = systemConfigService.getByKey("show_download_ios");
            JSONObject a = new JSONObject(s);
            for(Iterator iter = a.keys(); iter.hasNext();) {
              String k = (String) iter.next();
              keyValue.put(k, a.getString(k));
            }
            push.setKeyValue(keyValue);
          }
          catch(DataNotFoundException e) {
            return false;
          }
        }
      }
    }
    return flag;
  }

  private boolean tags(Push push, List<PushTagCollection> oldTag, String configN) throws Exception {
    List<PushTagCollection> ps = new ArrayList<PushTagCollection>();
    String[] newVs = null;
    try {
      String n = systemConfigService.getByKey(configN);
      newVs = n.split(",");
    }
    catch(DataNotFoundException e) {
      return false;
    }

    if(oldTag.isEmpty()) {
      push.setTagRelation("OR");
      int l = 0;
      if(newVs.length > 15)
        l = 15;
      else
        l = newVs.length;
      for(int i = 0; i < l; i++) {
        PushTagCollection p = new PushTagCollection();
        p.setTagId("V_" + newVs[i]);
        p.setTagName(newVs[i]);
        push.getTags().add(p);
      }
      xingePush.pushTag(push);
    }
    else {
      push.setTagRelation("AND");
      int l = 0;
      if(newVs.length > 15)
        l = 15;
      else
        l = newVs.length;
      for(int i = 0; i < l; i++) {
        push.getTags().clear();
        PushTagCollection p = new PushTagCollection();
        p.setTagId("V_" + newVs[i]);
        p.setTagName(newVs[i]);
        push.getTags().add(p);
        push.getTags().addAll(oldTag);
        xingePush.pushTag(push);
      }
    }

    return true;
  }

  /**
   * 173分版本
   * 
   * @param push
   * @return
   * @throws Exception
   */
  // @Override
  // public Push pushInfo2(Push push) throws Exception {
  // switch(push.getUserScope()) {
  // case ALL :
  // return xingePush.pushAll(push);
  // case SINGLE :
  // break;
  // case TAG :
  // return xingePush.pushTag(push);
  // default :
  // break;
  // }
  // return push;
  // }
  @Override
  public void updateWaiting() throws Exception {
    List<PushTagExcuteStateInfo> l = mongoTemplate.find(new Query().addCriteria(new Criteria("state").is("RUNNING")),
        PushTagExcuteStateInfo.class);
    if(l != null && !l.isEmpty()) {
      for(Iterator iterator = l.iterator(); iterator.hasNext();) {
        PushTagExcuteStateInfo pushTagExcuteStateInfo = (PushTagExcuteStateInfo) iterator.next();
        pushTagExcuteStateInfo.setState(PUSH_SEND_TAG_STATE.WAITING);
        mongoTemplate.save(pushTagExcuteStateInfo);
      }
    }
  }

  @Override
  public void delPushHistory() throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MONTH, -1);
    mongoTemplate.remove(
        new Query().addCriteria(new Criteria("pushType").nin("AUTO_LOCAL", "AUTO")).addCriteria(
            new Criteria("sendDate").lt(cal.getTime())), Push.class);
  }
}
