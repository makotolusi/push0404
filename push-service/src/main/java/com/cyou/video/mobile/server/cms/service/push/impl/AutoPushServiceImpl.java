package com.cyou.video.mobile.server.cms.service.push.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_ITEM_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_JOB_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_PLATFORM_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_USER_SCOPE;
import com.cyou.video.mobile.server.cms.model.push.Push;
import com.cyou.video.mobile.server.cms.model.push.PushApp;
import com.cyou.video.mobile.server.cms.model.push.PushAuto;
import com.cyou.video.mobile.server.cms.model.push.PushTagCollection;
import com.cyou.video.mobile.server.cms.model.sys.ContentType;
import com.cyou.video.mobile.server.cms.model.user.UserTokenBindXinge;
import com.cyou.video.mobile.server.cms.service.collection.ClientLogCollectionService;
import com.cyou.video.mobile.server.cms.service.push.AppSelectService;
import com.cyou.video.mobile.server.cms.service.push.AutoPushService;
import com.cyou.video.mobile.server.cms.service.push.PushInterface;
import com.cyou.video.mobile.server.cms.service.push.PushService;
import com.cyou.video.mobile.server.cms.service.sys.ContentTypeService;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.cms.service.utils.HttpRequestProvider;

/**
 * 自动推送
 * 
 * @author lusi
 */
@Service("autoPushServiceImpl")
public class AutoPushServiceImpl implements AutoPushService {

  private Logger logger = LoggerFactory.getLogger(AutoPushServiceImpl.class);

  @Autowired
  ClientLogCollectionService clientLogCollectionService;

  @Autowired
  PushService pushService;

  @Autowired
  PushInterface baiduPush;

  @Autowired
  XinGePush xingePush;

  @Autowired
  private MongoOperations mongoTemplate;

  @Autowired
  AppSelectService appSelectService;

  @Autowired
  PushTagXinGe173APPApi pushTagXinGe173APPApi;

  @Autowired
  private ContentTypeService contentTypeService;

  @Autowired
  private SystemConfigService systemConfigService;

  @Autowired
  HttpRequestProvider httpRequestProvider;

  @Override
  public boolean autoPush(String tag, String id, String title, String anchor, String itemType, CLIENT_TYPE ct,
      Integer appId) {
    try {
      Integer appId173 = Integer.parseInt(systemConfigService.getByKey("sys_173app_id"));
      Push pushAuto = this.getAutoPushByType(itemType);
      if(pushAuto == null) return false;
      PUSH_JOB_STATE state = pushAuto.getJobState();
      if(state == PUSH_JOB_STATE.DISABLE) return false;// 停用
      if(isCreatedWalkThrough(id, itemType)) // 该攻略已经启用过
        return false;
      Push push = new Push();
      CLIENT_TYPE[] ctype = null;
      if(ct == null) {
        if(pushAuto.getClientType() == CLIENT_TYPE.ALL)
          ctype = new CLIENT_TYPE[]{CLIENT_TYPE.ANDROID, CLIENT_TYPE.IOS};
        else
          ctype = new CLIENT_TYPE[]{pushAuto.getClientType()};
      }
      else {
        if(ct == CLIENT_TYPE.ALL)
          ctype = new CLIENT_TYPE[]{CLIENT_TYPE.ANDROID, CLIENT_TYPE.IOS};
        else
          ctype = new CLIENT_TYPE[]{ct};
      }
      push.setSendState(PUSH_SEND_STATE.FAIL);
      String gameName = null;
      if(pushAuto.getTitle().indexOf("#gameName#") >= 0 || pushAuto.getContent().indexOf("#gameName#") >= 0) {
        Map<String, String> typeSt = pushTagXinGe173APPApi.getGameCodeTypeAndStatus(tag, null);
        if(typeSt != null && !StringUtils.isEmpty(typeSt.get("name"))) {
          gameName = typeSt.get("name");
          push.setTitle(pushAuto.getTitle().replaceAll("#gameName#", typeSt.get("name")));
          push.setContent(pushAuto.getContent().replaceAll("#gameName#", typeSt.get("name")));
        }
        else {
          push.setTitle(pushAuto.getTitle().replaceAll("#gameName#", ""));
          push.setContent(pushAuto.getContent().replaceAll("#gameName#", ""));
        }
        if(StringUtils.isEmpty(gameName)) {
          gameName = title;
        }
      }
      else {
        push.setTitle(pushAuto.getTitle().replaceAll("#gameName#", ""));
        push.setContent(pushAuto.getContent().replaceAll("#gameName#", ""));
      }
//      if(!httpRequestProvider.isForbidenGameCode(tag)) {// 是否被禁用的标签
        if(Integer.parseInt(itemType) == COLLECTION_ITEM_TYPE.LIVE.index) {// live
          anchorName(anchor, pushAuto, push);// 主播名称
          setTag(tag + "_" + COLLECTION_ITEM_TYPE.LIVE.index, gameName, push, pushAuto);
        }
        else if(Integer.parseInt(itemType) == 13) {
          // setTag(tag + "_" + COLLECTION_ITEM_TYPE.GIFT.index, gameName, push,
          // pushAuto);
          PushTagCollection tag2 = new PushTagCollection();
          tag2.setTagId(tag);
          tag2.setTagName(tag);
          push.getTags().add(tag2);
        }
        else if(Integer.parseInt(itemType) == 28) {
          anchorName(anchor, pushAuto, push);// 主播名称
          setTag(tag + "_" + COLLECTION_ITEM_TYPE.SHOW.index, gameName, push, pushAuto);
//          if(appId != appId173) {
//            showInNight(push);// 夜间防扰
//          }
        }
        else {
          setTag(tag, gameName, push, pushAuto);
        }
//      }else{
//        setTag("tag forbiden", gameName, push, pushAuto);
//      }
      push.setTagRelation("AND");
      push.setUserScope(PUSH_USER_SCOPE.TAG);
      Map<String, String> keyValue = new HashMap<String, String>();
      keyValue.put("i", id);
      keyValue.put("t", title);
      if(Integer.parseInt(itemType) == COLLECTION_ITEM_TYPE.TOOL.index)
        keyValue.put("p", COLLECTION_ITEM_TYPE.WALKTHROUGH.index + "");
      else
        keyValue.put("p", Integer.parseInt(itemType) == 40008 ? "40004" : itemType);
      keyValue.put("s", "999");
      ContentType contentType = contentTypeService.getByIndexAndType(itemType, "SERVICE");
      ContentType ct1 = new ContentType();
      ct1.setDesc(contentType.getDesc());
      ct1.setName(contentType.getName());
      ct1.setIndex(contentType.getIndex());
      pushService.gameSwitch(push, appId173, contentType);// 游戏开关标签
      push.setKeyValue(keyValue);
      push.setContentType(ct1);
      push.setPlatForm(PUSH_PLATFORM_TYPE.XINGE);
      push.setAppId(appId);
      push.setPushType(PUSH_TYPE.AUTO_HISTORY);
      for(int i = 0; i < ctype.length; i++) {
        push.setId(null);
        push.setClientType(ctype[i]);
        String pushId = pushService.createPush(push);
        if(pushId == null) {
          logger.error("insert push object failed!!");
        }
        else {
          boolean flag = pushService.app173(push);
          if(flag==false){
            push = xingePush.pushTag(push);
          }
          if(push.getSendState() == PUSH_SEND_STATE.FAIL)
            logger.error("auto push failed!!");
          else
            pushService.updateSendStateById(push);
        }

      }
    }
    catch(Exception e) {
//      e.printStackTrace();
      logger.error("auto push exception is {}", e.getMessage());
      return false;
    }
    return true;
  }

  private void anchorName(String anchor, Push pushAuto, Push push) {
    if(pushAuto.getTitle().indexOf("#anchor#") >= 0 || pushAuto.getContent().indexOf("#anchor#") >= 0) {
      push.setTitle(pushAuto.getTitle().replaceAll("#anchor#", anchor));
      push.setContent(pushAuto.getContent().replaceAll("#anchor#", anchor));
    }
  }

  private void showInNight(Push push) throws DataNotFoundException {
    try {
      // 秀场夜间防扰开关
      String[] ampm = systemConfigService.getByKey("show_night").split(",");
      Date now = new Date();
      Calendar nowc = Calendar.getInstance();
      Calendar evening = Calendar.getInstance();
      Calendar morning = Calendar.getInstance();
      nowc.setTime(now);
      evening.setTime(now);
      morning.setTime(now);
      // System.out.println(nowc.getTime());
      evening.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ampm[1]));
      evening.set(Calendar.MINUTE, 0);
      // System.out.println(evening.getTime());
      morning.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ampm[0]));
      morning.set(Calendar.MINUTE, 0);
      // System.out.println(morning.getTime());
      if(morning.after(nowc) || evening.before(nowc)) {
        // 在夜里
        PushTagCollection tag2 = new PushTagCollection();
        tag2.setTagId(COLLECTION_ITEM_TYPE.SHOW_N.name());
        tag2.setTagName(COLLECTION_ITEM_TYPE.SHOW_N.name);
        push.getTags().add(tag2);
      }
    }
    catch(Exception e) {
      // TODO: handle exception
    }
  }

  private boolean isCreatedWalkThrough(String id, String itemType) {
    if(Integer.parseInt(itemType) == COLLECTION_ITEM_TYPE.WALKTHROUGH.index)// 新建攻略只能推送一次
    {
      Query q = new Query();
      q.addCriteria(new Criteria("contentType.index").is(itemType).and("keyValue.i").is(id));
      List<Push> l = mongoTemplate.find(q, Push.class);
      if(l == null || l.isEmpty())
        return false;
      else
        return true;
    }
    return false;
  }

  private void setTag(String gameCode, String gameName, Push push, Push pushAuto) {
    List<PushTagCollection> tags = pushAuto.getTags();
    PushTagCollection tag = new PushTagCollection();
    tag.setTagId(gameCode);
    tag.setTagName(gameCode);
    tags.add(tag);
    push.setTags(tags);
  }

  @Override
  public boolean pushFeedBack(String token, String title, String content, String type, String appId) throws Exception {
    Push push = new Push();
    PushApp app = appSelectService.getAppById(Integer.parseInt(appId));
    Push pushAuto = this.getAutoPushByType(type);
    PUSH_JOB_STATE state = pushAuto.getJobState();
    if(state == PUSH_JOB_STATE.DISABLE) return false;// 停用
    // UserToken n = null;// userTokenService.getToken(token);
    // String pushToke = "";
    // if(n == null) {
    // return false;
    // }
    UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(token);
    if(xingeToken == null) {// 没有xinge id
      return false;
    }
    push.setUserId(xingeToken.getXgToken());
    if(xingeToken.getPlatForm() == 6)
      push.setClientType(CLIENT_TYPE.IOS);
    else
      push.setClientType(CLIENT_TYPE.ANDROID);
    push.setContentType(contentTypeService.getByIndex(type));
    push.setAppId(Integer.parseInt(appId));
    push.setPushType(PUSH_TYPE.AUTO_HISTORY);
    String reply = pushAuto.getContent().replaceAll("#feedback#", content);
    push.setTitle(reply);
    push.setContent(reply);
    push.setPlatForm(pushAuto.getPlatForm());
    Map<String, String> keyValue = new HashMap<String, String>();
    keyValue.put("i", "");
    keyValue.put("t", "title");
    keyValue.put("p", type);
    keyValue.put("s", "999");
    push.setKeyValue(keyValue);
    push.setSendState(PUSH_SEND_STATE.FAIL);
    push.setUserScope(PUSH_USER_SCOPE.SINGLE);
    String pushId = pushService.createPush(push);
    if(pushId == null) {
      logger.error("insert push object failed!!");
    }
    else {
      push = xingePush.pushOne(push);
      if(push.getSendState() == PUSH_SEND_STATE.FAIL)
        logger.error("auto push failed!!");
      else
        pushService.updateSendStateById(push);
    }

    return true;

  }

  @Override
  public List<PushAuto> listAutoPush() {
    return mongoTemplate.findAll(PushAuto.class);
  }

  // @Override
  public Push getAutoPushByType(String itemType) throws Exception {
    Query query = new Query();
    query.addCriteria(new Criteria("pushType").is(PUSH_TYPE.AUTO.name()).and("contentType.index").is(itemType));
    return mongoTemplate.findOne(query, Push.class);
  }

}
