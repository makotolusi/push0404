package com.cyou.video.mobile.server.cms.service.push.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_OPERATOR_TYPE;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo2;
import com.cyou.video.mobile.server.cms.model.sys.ContentType;
import com.cyou.video.mobile.server.cms.model.sys.ContentTypeActionAndTag;
import com.cyou.video.mobile.server.cms.model.sys.ContentTypeActionAndTag.TAG_OPERATION;
import com.cyou.video.mobile.server.cms.model.user.UserTokenBindXinge;
import com.cyou.video.mobile.server.cms.service.push.PushTagXinGe173APPService;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.cms.service.utils.HttpRequestProvider;
import com.cyou.video.mobile.server.cms.service.utils.SpringEl;
import com.tencent.xinge.TagTokenPair;

/**
 * 意见反馈业务实现
 * 
 * @author jyz
 */
@Service("pushTagXinGe173APPService")
public class PushTagXinGe173APPServiceImpl implements PushTagXinGe173APPService {

  private Logger logger = LoggerFactory.getLogger(PushTagXinGe173APPServiceImpl.class);

  @Autowired
  PushTagXinGe173APPApi pushTagXinGe173APPApi;

  @Autowired
  HttpRequestProvider httpRequestProvider;

  @Autowired
  private SystemConfigService systemConfigService;

  @Override
  public int sendPushTags(Map<String, Object> params, int start, int end) {
    String threadName = String.valueOf(Thread.currentThread().getName());
    logger.info(" cur threadName  " + threadName + " start " + start + " " + end);
    // tag的集合
    // pushTagXinGe173APPApi.newTagResult();
    UserItemOperatePvMongo tagResult = new UserItemOperatePvMongo();
    pushTagXinGe173APPApi.initStartAndSize(start, end);
    logger.info(" cur threadName  " + threadName + " start " + pushTagXinGe173APPApi.getStart() + " size "
        + pushTagXinGe173APPApi.getSize());
    Query query = params.get("query") == null ? null : (Query) params.get("query");
    List<UserItemOperatePvMongo2> userSub = pushTagXinGe173APPApi.getUserItemOperatePvMongo2(query, params.get("name")
        .toString());
    logger
        .info(" cur threadName  " + threadName + " cur userSub size  " + userSub.size() + "  conternt -===" + userSub);
    int total = 0;
    while(!userSub.isEmpty()) {
      Map<String, ContentTypeActionAndTag> action = (Map<String, ContentTypeActionAndTag>) params.get("action");
      int result = sendPushTagCoreOther(userSub, params.get("name").toString(), action, tagResult);
      total += result;
      // multi thread
      if(pushTagXinGe173APPApi.continueStartAndSize(end) == false) break;
      userSub = pushTagXinGe173APPApi.getUserItemOperatePvMongo2(query, params.get("name").toString());
    }
    try {
      pushTagXinGe173APPApi.batchTags(params.get("appId").toString(), tagResult);
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("信鸽批量tag时候出错 " + e.getMessage());
    }// 批量执行tag
    return total;
  }

  @Override
  public int sendPushTagsOld(Map<String, Object> params, int start, int end) {
    String threadName = String.valueOf(Thread.currentThread().getName());
    logger.info(" cur threadName  " + threadName + " start " + start + " " + end);
    // tag的集合
    // pushTagXinGe173APPApi.newTagResult();
    UserItemOperatePvMongo tagResult = new UserItemOperatePvMongo();
    pushTagXinGe173APPApi.initStartAndSize(start, end);
    logger.info(" cur threadName  " + threadName + " start " + pushTagXinGe173APPApi.getStart() + " size "
        + pushTagXinGe173APPApi.getSize());
    Query query = params.get("query") == null ? null : (Query) params.get("query");
    List<UserItemOperatePvMongo2> userSub = pushTagXinGe173APPApi.getUserItemOperatePvMongo2(query, params.get("name")
        .toString());
    logger
        .info(" cur threadName  " + threadName + " cur userSub size  " + userSub.size() + "  conternt -===" + userSub);
    int total = 0;
    while(!userSub.isEmpty()) {
      Map<String, ContentTypeActionAndTag> action = (Map<String, ContentTypeActionAndTag>) params.get("action");
      int result = sendPushTagCoreOtherOld(userSub, params.get("name").toString(), action, tagResult);
      total += result;
      // multi thread
      if(pushTagXinGe173APPApi.continueStartAndSize(end) == false) break;
      userSub = pushTagXinGe173APPApi.getUserItemOperatePvMongo2(query, params.get("name").toString());
    }
    try {
      pushTagXinGe173APPApi.batchTags(params.get("appId").toString(), tagResult);
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("信鸽批量tag时候出错 " + e.getMessage());
    }// 批量执行tag
    return total;
  }

  private int sendPushTagCoreOther(List<UserItemOperatePvMongo2> userTag, String name,
      Map<String, ContentTypeActionAndTag> action, UserItemOperatePvMongo tagResult) {
    String threadName = String.valueOf(Thread.currentThread().getName());
    int setNum = 0;
    for(Iterator iterator = userTag.iterator(); iterator.hasNext();) {
      pushTagXinGe173APPApi.inc(threadName, setNum);
      setNum++;
      try {
        UserItemOperatePvMongo2 uTag = (UserItemOperatePvMongo2) iterator.next();
        if(uTag.getId().getUid() == null) {// 没有token
          continue;
        }
        else {
          // set 百度 id
          UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(uTag.getId().getUid());
          if(xingeToken == null) {// 没有xinge id
            continue;
          }
          else {
            uTag.getId().setPushToken(xingeToken.getXgToken());
            uTag.getValue().setPushToken(xingeToken.getXgToken());
//            if("df6a428a98c68b590af2daea46a9ebfc5afe89499be10aab8cd9e96dbcb37e0c".equals(xingeToken.getXgToken()))
//            logger.info("uid:"+xingeToken.getXgToken());
          }
          pushTagXinGe173APPApi.initClientType(xingeToken.getPlatForm());
//          if("df6a428a98c68b590af2daea46a9ebfc5afe89499be10aab8cd9e96dbcb37e0c".equals(xingeToken.getXgToken()))
//          logger.info("clientType:"+pushTagXinGe173APPApi.getClientType());
          // client type
          String key = uTag.getValue().getItemType() + "_";
          if(!StringUtils.isEmpty(uTag.getId().getOtherWay()) && uTag.getId().getOtherWay() != -1) {
            key += uTag.getId().getOtherWay() + "_";
          }
          key += uTag.getId().getOperatorType() + "_";
          // pushTagXinGe173APPApi.activity(uTag.getId(),
          // uTag.getValue());// 独特的活动标签方式
          ContentTypeActionAndTag actionAndTag = action.get(key);
          // 表达式的需求暂时没有
          // if(!StringUtils.isEmpty(actionAndTag.getCondition())) {
          // Boolean f = (Boolean)
          // SpringEl.condition(actionAndTag.getCondition(),
          // uTag.getValue());
          // if(!f) {
          // continue;// 表达式未通过
          // }
          // }
          if(actionAndTag != null) {
//            if("df6a428a98c68b590af2daea46a9ebfc5afe89499be10aab8cd9e96dbcb37e0c".equals(xingeToken.getXgToken()))
//            logger.info(actionAndTag.getName());
            List<ContentType> tag = actionAndTag.getTags();
            for(Iterator iterator2 = tag.iterator(); iterator2.hasNext();) {
              String realTag = "";
              ContentType contentType = (ContentType) iterator2.next();
              if(StringUtils.isEmpty(contentType.getTag())) {// 没有配置标签表达式，直接打常亮
                realTag = contentType.getTagConstant();
              }
              else if(contentType.getTag().indexOf("_") > 0) {
                String[] tagCombination = contentType.getTag().split("_");
                Object v = SpringEl.getFieldValue(tagCombination[0], uTag.getValue());
                realTag += (v == null ? tagCombination[0] : v) + "_";
                Object index = SpringEl.getFieldValue(tagCombination[1], contentType);
                realTag += (index == null ? tagCombination[1] : index);
              }
              else {// single
                Object obj = SpringEl.getFieldValue(contentType.getTag(), uTag.getValue());
                String v = (String) obj;
                if(v.indexOf(",") >= 0) {
                  String[] s = v.split(",");
                  for(int i = 0; i < s.length; i++) {// game
                    realTag = s[i];
                  }
                }
                else {
                  realTag = v;
                }
              }
              if(!StringUtils.isEmpty(realTag)) {
//                if("df6a428a98c68b590af2daea46a9ebfc5afe89499be10aab8cd9e96dbcb37e0c".equals(xingeToken.getXgToken()))
//                logger.info("realTag is " + realTag);
                if(xingeToken.getXgToken() != null)
                  tag(xingeToken.getXgToken(), actionAndTag, realTag, tagResult);
              }
            }
//            pushTagXinGe173APPApi.updateTagStateInMongoBatch(uTag.getId(),
//                Consts.PUSH_TAG_SETTING_STATE.SUC.getIndex(), name);
          }
          // suc.add(pushTagXinGe173APPApi.jiong(uTag.getId(),
          // uTag.getValue()));
          //
          // pushTagXinGe173APPApi.news(uTag.getId(), uTag.getValue());
          //
          // suc.add(pushTagXinGe173APPApi.live(uTag.getId(),
          // uTag.getValue()));
          //
          // suc.add(pushTagXinGe173APPApi.video(uTag.getId(),
          // uTag.getValue()));
          //
          // suc.add(pushTagXinGe173APPApi.activity(uTag.getId(),
          // uTag.getValue()));
          // //
          // suc.add(pushTagXinGe173APPApi.bySearch(uTag.getId(),
          // uTag.getValue()));
          //
          // suc.add(pushTagXinGe173APPApi.byRank(uTag.getId(),
          // uTag.getValue()));
          // // 订阅
          // pushTagXinGe173APPApi.subscribe(uTag.getId(), uTag.getValue());
          // // 订阅
          // pushTagXinGe173APPApi.top(uTag.getId(), uTag.getValue());
          //
          // suc.add(pushTagXinGe173APPApi.desktop(uTag.getId(),
          // uTag.getValue()));
          // // app
          // pushTagXinGe173APPApi.app(uTag.getId(), uTag.getValue());
        }
        // if(suc.contains(true))
      }
      catch(Exception e) {
        logger.error("set tag exception is " + e.getMessage());
      }
    }
    return setNum;
  }

  private int sendPushTagCoreOtherOld(List<UserItemOperatePvMongo2> userTag, String name,
      Map<String, ContentTypeActionAndTag> action, UserItemOperatePvMongo tagResult) {
    String threadName = String.valueOf(Thread.currentThread().getName());
    logger.info("cur thread is " + threadName);
    int setNum = 0;
    for(Iterator iterator = userTag.iterator(); iterator.hasNext();) {
      pushTagXinGe173APPApi.inc(threadName, setNum);
      setNum++;
      try {
        UserItemOperatePvMongo2 uTag = (UserItemOperatePvMongo2) iterator.next();
        // logger.info("game code ==========="+uTag.getValue().getGameCode() );
        // if("4033126".equals(uTag.getValue().getGameCode()))
        // System.out.println("==");
        if(uTag.getId().getUid() == null) {// 没有token
          continue;
        }
        else {
          // set 百度 id
          UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(uTag.getId().getUid());// "9451f39ac0bffa213d51f71662a9dd2443272685_gl@qmyx");
          if(xingeToken == null) {// 没有xinge id
          // logger.info("xinge token is null  173 token is "+uTag.getId().getUid());
            continue;
          }
          else {
            uTag.getId().setPushToken(xingeToken.getXgToken());
            uTag.getValue().setPushToken(xingeToken.getXgToken());
            // logger.info("xinge token is  "+xingeToken.getXgToken());
          }
          pushTagXinGe173APPApi.initClientType(xingeToken.getPlatForm());
          // 成功数
          // suc.add(pushTagXinGe173APPApi.bySearch(uTag.getId(),
          // uTag.getValue()));
          // suc.add(pushTagXinGe173APPApi.gift(uTag.getId(), uTag.getValue()));
          // app
          pushTagXinGe173APPApi.app(uTag.getId(), uTag.getValue(), tagResult);
          // logger.info(threadName+" setNum " + setNum );
          // if(suc.contains(true))
          // pushTagXinGe173APPApi.updateTagStateInMongoBatch(uTag.getId(),
          // Consts.PUSH_TAG_SETTING_STATE.SUC.getIndex(), name);
        }

      }
      catch(Exception e) {
        logger.error("set tag exception is " + e.getMessage());
      }
    }
    return setNum;
  }

  private void tag(String xingeToken, ContentTypeActionAndTag actionAndTag, String tag, UserItemOperatePvMongo tagResult) {
    if(actionAndTag.getOperation() == TAG_OPERATION.SET_TAG) {
      pushTagXinGe173APPApi.setTag(xingeToken, tag, tagResult);
    }
    else {
      pushTagXinGe173APPApi.deleteTag(xingeToken, tag, tagResult);
    }
  }

//  @Override
//  public int sendBestWalkThroughInstalledGameTags(Map<String, Object> params, int start, int end) throws Exception {
//    // String threadName = String.valueOf(Thread.currentThread().getName());
//    Map<String, UserItemOperatePvMongo> map = new HashMap<String, UserItemOperatePvMongo>();
//    pushTagXinGe173APPApi.initStartAndSize(start, end);
//    Query query = params.get("query") == null ? null : (Query) params.get("query");
//    List<ClientLogBestWalkthroughCollection> userSub = pushTagXinGe173APPApi.getBestWalkthroughInstalledGames(query,
//        Consts.COLLECTION_CLIENT_LOG_WALKTHROUGH_NAME);
//    int total = 0;
//    while(!userSub.isEmpty()) {
//      int result = sendPushWalkThroughTagCoreOther(userSub, map);
//      total += result;
//      if(pushTagXinGe173APPApi.continueStartAndSize(end) == false) break;
//      userSub = pushTagXinGe173APPApi.getBestWalkthroughInstalledGames(query,
//          Consts.COLLECTION_CLIENT_LOG_WALKTHROUGH_NAME);
//    }
//    try {
//      pushTagXinGe173APPApi.batchTags(map);
//    }
//    catch(Exception e) {
//      e.printStackTrace();
//      logger.debug("xinge set tag exception may be could not find appKey by id");
//    }
//    return total;
//  }
//
//  private int sendPushWalkThroughTagCoreOther(List<ClientLogBestWalkthroughCollection> userTag,
//      Map<String, UserItemOperatePvMongo> map) {
//    String threadName = String.valueOf(Thread.currentThread().getName());
//    int setNum = 0;
//    for(Iterator iterator = userTag.iterator(); iterator.hasNext();) {
//      pushTagXinGe173APPApi.inc(threadName, setNum);
//      setNum++;
//      try {
//        ClientLogBestWalkthroughCollection uTag = (ClientLogBestWalkthroughCollection) iterator.next();
//        if(uTag.getUid() == null) {// 没有token
//          continue;
//        }
//        else {
//          // set xinge id
//          UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(uTag.getUid());
//          if(xingeToken == null) {// 没有baidu id
//            continue;
//          }
//          else {
//            app(uTag, xingeToken.getXgToken(), xingeToken.getPlatForm(), map);
//          }
//        }
//      }
//      catch(Exception e) {
//        logger.error("set tag exception is " + e.getMessage());
//      }
//    }
//    return setNum;
//  }
//
//  private void app(ClientLogBestWalkthroughCollection data, String xingeToken, int plat,
//      Map<String, UserItemOperatePvMongo> map) throws Exception {
//    TagTokenPair tag = new TagTokenPair(data.getServiceId(), xingeToken);
//    UserItemOperatePvMongo u = null;
//    if(map.containsKey(data.getAppId())) {
//      u = map.get(data.getAppId());
//    }
//    else {
//      u = new UserItemOperatePvMongo();
//    }
//    if(Integer.parseInt(data.getOperatorType()) == COLLECTION_OPERATOR_TYPE.INSTALL.index) {
//      u.addAndroidTag(tag);
//    }
//    if(Integer.parseInt(data.getOperatorType()) == COLLECTION_OPERATOR_TYPE.UNINSTALL.index) {
//      u.addAndroidDelTag(tag);
//    }
//    map.put(data.getAppId(), u);
//  }

  @Override
  public void successLogEnd(String c, String appId,int total) {
    pushTagXinGe173APPApi.successLogEnd(c, appId,total);

  }
}
