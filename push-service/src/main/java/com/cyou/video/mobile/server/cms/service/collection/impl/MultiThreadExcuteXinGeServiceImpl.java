package com.cyou.video.mobile.server.cms.service.collection.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_PUSH_TAG_JOB_NAME;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_TAG_STATE;
import com.cyou.video.mobile.server.cms.model.collection.PushTagExcuteStateInfo;
import com.cyou.video.mobile.server.cms.model.collection.PushTagLastUpdateTime;
import com.cyou.video.mobile.server.cms.model.sys.ConfigApps;
import com.cyou.video.mobile.server.cms.model.sys.ContentType;
import com.cyou.video.mobile.server.cms.model.sys.ContentTypeActionAndTag;
import com.cyou.video.mobile.server.cms.service.collection.ClientLogCollectionService;
import com.cyou.video.mobile.server.cms.service.collection.MultiThreadExcuteXinGeService;
import com.cyou.video.mobile.server.cms.service.collection.TimeFlagService;
import com.cyou.video.mobile.server.cms.service.push.AppSelectService;
import com.cyou.video.mobile.server.cms.service.push.PushTagXinGe173APPService;
import com.cyou.video.mobile.server.cms.service.sys.ConfigAppsService;
import com.cyou.video.mobile.server.cms.service.sys.ContentTypeService;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.cms.service.utils.DateUtils;
import com.cyou.video.mobile.server.common.Constants;

/**
 * 
 * @author lusi
 * 
 */
@Service("multiThreadExcuteXinGeService")
public class MultiThreadExcuteXinGeServiceImpl implements MultiThreadExcuteXinGeService {

  private Logger logger = LoggerFactory.getLogger(MultiThreadExcuteXinGeServiceImpl.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  ClientLogCollectionService clientLogCollectionService;

  @Autowired
  PushTagXinGe173APPService pushTagXinGe173APPService;

  @Autowired
  ThreadPoolTaskExecutor taskExecutor;

  @Autowired
  TimeFlagService timeFlagService;

  @Autowired
  ConfigAppsService configAppsService;

  @Autowired
  AppSelectService appSelectService;

  @Autowired
  private SystemConfigService systemConfigService;

  @Autowired
  ContentTypeService contentTypeService;

  @Override
  public void syncApp() {
    try {
      appSelectService.syncApp();
    }
    catch(Exception e) {
      logger.error("manual syncApp exception " + e.getMessage());
    }
  }

  /**
   * 发送tag
   */
  @Override
  public ModelMap sendPushTags(Map<String, Object> params, ModelMap model) {
    // 等其它线程执行完
    waiting();
    // 删除发送log
    delThreadNumList();
    smallTag(params, model);
    // 等其它线程执行完
    // waiting();
    // delThreadNumList();
    // gameCode(params, model);
    // waiting();
    model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    return model;
  }

  @Override
  public ModelMap sendBestWalkThrough(Map<String, Object> params, ModelMap model) {
    // 等其它线程执行完
    waiting();
    // 删除发送log
    delThreadNumList();
    bestWalkThrough(params, model);
    model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    return model;
  }

  private void bestWalkThrough(Map<String, Object> params, ModelMap model) {
    try {
      Query queryOther = new Query();
      // 清空之前的记录
      removePushTagLogByName(Consts.COLLECTION_CLIENT_LOG_WALKTHROUGH_NAME);
      // 其它
      PushTagLastUpdateTime oldLastUpdateTime = timeFlagService.setTimestamp(queryOther,
          Consts.COLLECTION_USER_GAME_PV, "value.uploadDate");
      // queryOther.addCriteria(Criteria.where("value.state").is(0));
      long total = clientLogCollectionService.getCount(queryOther, Consts.COLLECTION_CLIENT_LOG_WALKTHROUGH_NAME);
      // 线程总量
      setThreadTotal((int) total);
      params.put("query", queryOther);
      int threadNum = Integer.parseInt(systemConfigService.getByKey("sys_thread_num"));
      successLogStart(threadNum, Consts.COLLECTION_CLIENT_LOG_WALKTHROUGH_NAME, 0);
//      multiTreadCore(threadNum, total, Consts.COLLECTION_CLIENT_LOG_WALKTHROUGH_NAME,
//          COLLECTION_PUSH_TAG_JOB_NAME.WALKTHROUGH_APP_GAME_TAG, params);
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void smallTag(Map<String, Object> params, ModelMap model) {
    try {
      String collection = Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME;
      // 清空之前的记录
     
      String[] pvappIds = systemConfigService.getByKey("pv_analyseis_appid").split(",");
      for(int i = 0; i < pvappIds.length; i++) {
        String appId = pvappIds[i];// systemConfigService.getByKey("sys_173app_id");
        removePushTagLogByName(Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME+"_"+appId);
        params.put("appId", appId);
        ConfigApps ca = configAppsService.findConfigIdByAppid(Integer.parseInt(appId));
        ConfigApps configApps = configAppsService.findById(ca.getId());
        List<ContentType> tag = configApps.getTag();
        // Map<String,Map<String, ContentTypeActionAndTag>> tagConfig=new
        // HashMap<String,Map<String, ContentTypeActionAndTag>>();
        Map<String, ContentTypeActionAndTag> action = new HashMap<String, ContentTypeActionAndTag>();
        List in = new ArrayList();// 打标签范围
        for(Iterator iterator = tag.iterator(); iterator.hasNext();) {
          ContentType ct = (ContentType) iterator.next();
          String service = ct.getIndex();
          in.add(ct.getIndex());
          List<ContentTypeActionAndTag> actionAndTag = ct.getAction();
          for(Iterator iterator2 = actionAndTag.iterator(); iterator2.hasNext();) {
            String key = service + "_";
            ContentTypeActionAndTag contentTypeActionAndTag = (ContentTypeActionAndTag) iterator2.next();
            List<ContentType> act = contentTypeActionAndTag.getAction();
            contentTypeService.fullActionAndTag(act);
            for(Iterator iterator3 = act.iterator(); iterator3.hasNext();) {
              ContentType contentType = (ContentType) iterator3.next();
              key += contentType.getIndex() + "_";
            }
            List<ContentType> tt = contentTypeActionAndTag.getTags();
            contentTypeService.fullActionAndTag(tt);
            action.put(key, contentTypeActionAndTag);
          }
        }
        params.put("action", action);
        Query queryOther = new Query();
        queryOther.addCriteria(Criteria.where("value.itemType").in(in));
        if(appId.equals(systemConfigService.getByKey("sys_173app_id"))) {
          queryOther.addCriteria(Criteria.where("_id.appId").is(-1));
        }
        else {
          queryOther.addCriteria(Criteria.where("_id.appId").is(Integer.parseInt(appId)));
        }
        PushTagLastUpdateTime oldLastUpdateTime = timeFlagService.setTimestamp(queryOther, collection+"_"+appId,
            "value.uploadDate");
        queryOther.with(new Sort(Direction.ASC, "value.uploadDate"));
        long total = clientLogCollectionService.getCount(queryOther, collection);
        // 线程总量
        setThreadTotal((int) total);
        params.put("query", queryOther);
        params.put("appId", appId);
        int threadNum = Integer.parseInt(systemConfigService.getByKey("sys_thread_num"));
        final CountDownLatch latch = new CountDownLatch(threadNum);
        successLogStart(threadNum, collection+"_"+appId, 0);
        multiTreadCore(threadNum, total, collection, COLLECTION_PUSH_TAG_JOB_NAME.USER_REDUCE_TAG, params,latch);
        latch. await();
      }

    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void oldData(Map<String, Object> params, ModelMap model) {
    try {
      String psw = params.get("psw").toString();
      String collection = psw.split(":")[0];// systemConfigService.getByKey("user_item_operation_pv_sub_collection");
      // String appId = systemConfigService.getByKey("sys_173app_id");
      removePushTagLogByName(collection);// Consts.COLLECTION_ITEM_TYPE.APP.index
                                         // + "",
      // Object[] in = new Object[]{
      // Consts.COLLECTION_ITEM_TYPE.WALKTHROUGH.index + "",
      // Consts.COLLECTION_ITEM_TYPE.GIFT.index + "",
      // Consts.COLLECTION_ITEM_TYPE.NEWS.index + "",
      // Consts.COLLECTION_ITEM_TYPE.APP.index + "",
      // Consts.COLLECTION_ITEM_TYPE.VIDEO.index + ""};
      String appId = systemConfigService.getByKey("sys_173app_id");
      params.put("appId", appId);
      Query queryOther = new Query();
      Date d = DateUtils.toDate(psw.split(":")[1]);
      Calendar cal = Calendar.getInstance();
      cal.setTime(d);
      cal.add(Calendar.DAY_OF_MONTH, 1);
      Date d2 = DateUtils.toDate(psw.split(":")[2]);
      Criteria cri = new Criteria().where("value.uploadDate").gt(d).lt(d2);
      cri.and("value.itemType").is("10");
      // if("otherWay".equals(psw.split(":")[3])){
      // cri.and("value.itemType").in(in).and("_id.otherWay").is(5);
      // }
      // if("operatorType".equals(psw.split(":")[3])){
      // cri.and("value.itemType").in(in2).and("_id.operatorType").is(in3);
      // }
      queryOther.addCriteria(cri);// 搜索
      queryOther.with(new Sort(Direction.ASC, "value.uploadDate"));
      // PushTagLastUpdateTime oldLastUpdateTime = timeFlagService
      // .setTimestamp(queryOther, collection, "value.uploadDate");
      long total = clientLogCollectionService.getCount(queryOther, collection);
      ;
      // 线程总量
      setThreadTotal((int) total);
      params.put("query", queryOther);
      int threadNum = Integer.parseInt(systemConfigService.getByKey("sys_thread_num"));
      successLogStart(threadNum, collection, 0);
//      multiTreadCore(threadNum, total, collection, COLLECTION_PUSH_TAG_JOB_NAME.TEMP, params);
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void oldData2(Map<String, Object> params, ModelMap model) {
    try {
      String collection = Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME;
      String collection_log = Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME + "_LOG";
      // String appId = systemConfigService.getByKey("sys_173app_id");
      removePushTagLogByName(collection_log);// Consts.COLLECTION_ITEM_TYPE.APP.index
                                             // + "",
      Object[] in = new Object[]{Consts.COLLECTION_ITEM_TYPE.WALKTHROUGH.index + "",
          Consts.COLLECTION_ITEM_TYPE.GIFT.index + "", Consts.COLLECTION_ITEM_TYPE.NEWS.index + "",
          Consts.COLLECTION_ITEM_TYPE.APP.index + "", Consts.COLLECTION_ITEM_TYPE.VIDEO.index + ""};
      Query queryOther = new Query();
      Criteria cri = new Criteria();
      cri.where("value.itemType").in(Arrays.asList(in)).and("_id.otherWay").is("5");// search
      cri.orOperator(new Criteria("value.itemType").is(Consts.COLLECTION_ITEM_TYPE.APP.index + "")
          .and("_id.operatorType").is("10"));// app
      cri.orOperator(new Criteria("value.itemType").is(Consts.COLLECTION_ITEM_TYPE.GIFT.index + "")
          .and("_id.operatorType").in("14", "15"));// app
      queryOther.addCriteria(cri);// 搜索
      PushTagLastUpdateTime oldLastUpdateTime = timeFlagService.setTimestamp(queryOther, collection_log,
          "value.uploadDate");
      Date d = DateUtils.toDate(params.get("date").toString());
      queryOther.addCriteria(Criteria.where("value.uploadDate").lt(d));
      long total = clientLogCollectionService.getCount(queryOther, collection);
      // 线程总量
      setThreadTotal((int) total);
      params.put("query", queryOther);
      int threadNum = Integer.parseInt(systemConfigService.getByKey("sys_thread_num"));
      successLogStart(threadNum, collection_log, 0);
//      multiTreadCore(threadNum, total, collection, COLLECTION_PUSH_TAG_JOB_NAME.TEMP, params);
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public ModelMap pushHistoryToMongo(Map<String, Object> params, ModelMap model) {
    try {
      // List<PushOld> li = pushDao.listPush(params);
      // List<Push> pushs=new ArrayList<Push>();
      // for(Iterator iterator = li.iterator(); iterator.hasNext();) {
      // PushOld pushOld = (PushOld) iterator.next();
      // Push push = new Push();
      // push.setPlatForm(PUSH_PLATFORM_TYPE.BAIDU);
      // push.setTitle(pushOld.getTitle());
      // push.setContent(pushOld.getContent());
      // push.setClientTypes(pushOld.getClientTypes());
      // push.setClientType(pushOld.getClientType());
      // push.setCronExp(pushOld.getCronExp());
      // push.setCronExpression(pushOld.getCronExpression());
      // push.setUserScope(pushOld.getUserScope());
      // push.setSendState(pushOld.getSendState());
      // push.setSendDate(pushOld.getSendDate());
      // push.setSentLogs(pushOld.getSentLogs());
      // push.setPushType(pushOld.getPushType());
      // push.setJobState(pushOld.getJobState());
      // push.setSentLogs(pushOld.getSentLogs());
      // push.setAppId(Integer.parseInt(systemConfigService.getSystemConfigByConfigKey("sys_173app_id")));
      // String kv = "{" + pushOld.getKeyValue() + "}";
      // JSONObject obj = new JSONObject(kv);
      // Iterator keys = obj.keys();
      // Map map = new HashMap();
      // while(keys.hasNext()) {
      // String key = (String) keys.next();
      // String value = obj.get(key).toString();
      // if("URL".equals(key)){
      // push.setContentType(COLLECTION_ITEM_TYPE.URL);
      // }else if("p".equals(key)){
      // push.setContentType(COLLECTION_ITEM_TYPE.values()[Integer.parseInt(value)]);
      // }
      // map.put(key, value);
      // }
      // push.setKeyValue(map);
      // String tagstr=pushOld.getTags();
      // if(!StringUtils.isEmpty(tagstr)){
      // PushTagCollection p=new PushTagCollection();
      // p.setTagName(tagstr.split(":")[0]);
      // p.setTagId(tagstr.split(":")[1]);
      // List<PushTagCollection> tags=new ArrayList<PushTagCollection>();
      // tags.add(p);
      // push.setTags(tags);
      // }
      // pushs.add(push);
      // }
      // mongoTemplate.insertAll(pushs);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return model;
  }

  /**
   * ---------------------------------tool--------------------------------------
   * 
   **/

  public void waiting() {
    while(existRunningThread()) {
      logger.info("other thread is running ---------------------------------------");
      try {
        Thread.sleep(5000);
      }
      catch(InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void successLogStart(int threadNum, String c, long size) {
    PushTagExcuteStateInfo o = new PushTagExcuteStateInfo(c, new Date(), threadNum);
    o.setSize(size);
    o.setState(PUSH_SEND_TAG_STATE.RUNNING);
    this.savePushTagLog(o);
  }

  // public PushTagExcuteStateInfo getSysThreadNum() {
  // return mongoTemplate.findOne(new Query().addCriteria(new
  // Criteria("name").is("sysThreadNum")),
  // PushTagExcuteStateInfo.class, "PushTagThreadInfo");
  // }

  public void setThreadTotal(int total) {
    mongoTemplate.save(new PushTagExcuteStateInfo("threadTotal", null, total), "PushTagThreadInfo");
  }

  public void removePushTagLogByName(String name) {
    mongoTemplate.remove(new Query().addCriteria(new Criteria("name").is(name)), PushTagExcuteStateInfo.class);
  }

  public void savePushTagLog(Object o) {
    mongoTemplate.save(o);
  }

  public void delThreadNumList() {
    try {
      mongoTemplate.remove(new Query().addCriteria(new Criteria("name").ne("sysThreadNum")), "PushTagThreadInfo");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean existRunningThread() {
    Query query = new Query();
    query.addCriteria(new Criteria().where("state").is(PUSH_SEND_TAG_STATE.RUNNING.name()));
    List l = mongoTemplate.find(query, PushTagExcuteStateInfo.class);
    if(l == null || l.isEmpty())
      return false;
    else
      return true;
  }

  /**
   * 多线程
   * 
   * @param model
   * @param query
   * @param oldLastUpdateTime
   * @param cname
   * @param pthread
   */
  private void multiTreadCore(int thr, long total, String collectionName, COLLECTION_PUSH_TAG_JOB_NAME type,
      Map<String, Object> params,CountDownLatch latch) throws Exception {
    // 多线程开始
    int threadN = thr;
    long size = total / threadN;
    long yu = total % threadN;
    long start = 0;
    long end = size;
    for(int i = 1; i <= threadN; i++) {
      if(i != 1) {
        start = end;
        if(i == threadN)
          end += size + yu;
        else
          end += size;
      }
      params.put("start", start);
      params.put("end", end);
      params.put("name", collectionName);
      PThread pThread = getThreadService(type, params,latch);
      pThread.setName(collectionName);
      pThread.setStart(start);
      pThread.setEnd(end);
      taskExecutor.execute(pThread);
    }
  }

  private PThread getThreadService(COLLECTION_PUSH_TAG_JOB_NAME type, Map<String, Object> params,CountDownLatch latch) {
    params.put("jobType", type);
    PThread pThread = new PushTagXinGeThread(pushTagXinGe173APPService, params,latch);
    return pThread;
  }

}
