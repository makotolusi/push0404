package com.cyou.video.mobile.server.cms.service.push.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_ITEM_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_OPERATOR_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.GAME_PLATFORM_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_TAG_STATE;
import com.cyou.video.mobile.server.cms.model.collection.ClientLogCollection;
import com.cyou.video.mobile.server.cms.model.collection.PushTagExcuteStateInfo;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo;
import com.cyou.video.mobile.server.cms.model.collection.UserItemOperatePvMongo2;
import com.cyou.video.mobile.server.cms.model.push.Push;
import com.cyou.video.mobile.server.cms.model.user.UserToken;
import com.cyou.video.mobile.server.cms.model.user.UserTokenBindXinge;
import com.cyou.video.mobile.server.cms.service.push.PushInterface;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.cms.service.utils.HttpRequestProvider;
import com.cyou.video.mobile.server.common.utils.HttpUtil;
import com.mongodb.WriteResult;
import com.tencent.xinge.TagTokenPair;

/**
 * 意见反馈业务实现
 * 
 * @author jyz
 */
@Component("pushTagXinGe173APPApi")
public class PushTagXinGe173APPApi {

  public Logger logger = LoggerFactory.getLogger(PushTagXinGe173APPApi.class);

  private CLIENT_TYPE clientType;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private SystemConfigService systemConfigService;

  @Autowired
  private PushInterface xingePush;

  @Autowired
  HttpRequestProvider httpRequestProvider;

  private int start;

  private int size;

  /**
   * ---------------------不同类型对应tag----------------------
   */
  /**
   * 或观看30s以上
   * 
   * @param id
   * @param value
   * @param baiduId
   * @param clientType
   * @throws Exception
   */
  // public boolean video(ClientLogCollection id, ClientLogCollection value)
  // throws Exception {
  // if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.VIDEO) {
  // // if (id.getOperatorTypeE() ==
  // // COLLECTION_OPERATOR_TYPE.LEAVE_COMMENTS
  // // || id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.SHARE
  // // || id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.DOWNLOAD) {
  // // setGameTag(value, baiduId, clientType);
  // // }
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.VIEW) {
  // if(value.getKeyWord() != null && !"".equals(value.getKeyWord())) {
  // long sec = Long.parseLong(value.getKeyWord().trim()) / 1000;
  // if(sec > 30) {
  // setGameTag(value, id.getPushToken());
  // return true;
  // }
  // }
  // }
  // }
  // return false;
  // }

  // public boolean live(ClientLogCollection id, ClientLogCollection value)
  // throws Exception {
  // if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.LIVE) {
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.VIEW) {
  // if(value.getKeyWord() != null && !"".equals(value.getKeyWord())) {
  // long sec = Long.parseLong(value.getKeyWord().trim()) / 1000;
  // if(sec > 30) {
  // setGameTag(value, id.getPushToken());
  // setTag(id.getPushToken(), value.getGameCode() + "_" +
  // COLLECTION_ITEM_TYPE.LIVE.index);// XX游戏直播
  // return true;
  // }
  // }
  //
  // }
  // }
  // return false;
  // }

  public void news(ClientLogCollection id, ClientLogCollection value) throws Exception {
    if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.NEWS) {
      // if (id.getOperatorTypeE() ==
      // COLLECTION_OPERATOR_TYPE.LEAVE_COMMENTS
      // || id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.SHARE) {
      // setGameTag(value, baiduId, clientType);
      // }
    }
  }

  // public boolean activity(ClientLogCollection id, ClientLogCollection value)
  // throws Exception {
  // if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.ACT_CENTER) {
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.VIEW) {
  // setTag(id.getPushToken(), "ACT_CENTER");
  // return true;
  // }
  // }
  // return false;
  // }

//  public boolean bySearch(ClientLogCollection id, ClientLogCollection value) throws Exception {
//    if(id.getOtherWay() == 5) {
//      return setGameTag(value, id.getPushToken(), "24_20",tagResult);
//    }
//    return false;
//  }
//
//  public boolean gift(ClientLogCollection id, ClientLogCollection value) throws Exception {
//    if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.GIFT) {
//      if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.RECEIVE
//          || id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.RANDOM) {
//        return setGameTag(value, id.getPushToken(), "24_S13",tagResult);
//      }
//    }
//    return false;
//  }

  // public boolean byRank(ClientLogCollection id, ClientLogCollection value)
  // throws Exception {
  // if(id.getOtherWay() == 15) {
  // setGameTag(value, id.getPushToken());
  // if(!StringUtils.isEmpty(value.getKeyWord())) {
  // String rankTag = value.getKeyWord() + "_" +
  // COLLECTION_ITEM_TYPE.RANK.getIndex();
  // setTag(id.getPushToken(), rankTag);
  // return true;
  // }
  // }
  // return false;
  // }

  // public boolean jiong(ClientLogCollection id, ClientLogCollection value)
  // throws Exception {
  // if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.PIC) {
  // if(!StringUtils.isEmpty(value.getKeyWord()))
  // setTag(value.getPushToken(), value.getKeyWord() + "_" +
  // COLLECTION_ITEM_TYPE.JIONG.index);
  // return true;
  // }
  // return false;
  // }

  /**
   * 访问量 礼包标签
   * 
   * @param id
   * @param value
   * @param baiduId
   * @param clientType
   * @throws Exception
   */
  // public boolean hits(ClientLogCollection id,
  // com.cyou.video.mobile.server.cms.model.collection.Value value,
  // String baiduId) throws Exception {
  // if(value.getPv() >= 3) {
  // return setGameTag(value, baiduId);
  // }
  // return false;
  // }

  // public void subscribe(ClientLogCollection id, ClientLogCollection value)
  // throws Exception {
  // if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.WALKTHROUGH) {
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.SUBSCRIBE) {
  // setGameTag(value, id.getPushToken());
  // }
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.CANCEL_SUBSCRIBE) {
  // delGameTag(value, id.getPushToken());
  // }
  // }
  // }

  // public void top(ClientLogCollection id, ClientLogCollection value) throws
  // Exception {
  // if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.WALKTHROUGH) {
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.TOP) {
  // setGiftTag(value, id.getPushToken());
  // }
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.CANCEL_TOP) {
  // delGiftTag(value, id.getPushToken());
  // }
  // }
  // }

  // public boolean desktop(ClientLogCollection id, ClientLogCollection value)
  // throws Exception {
  // if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.WALKTHROUGH) {
  // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.DESKTOP) {
  // return setGiftTag(value, id.getPushToken());
  // }
  // }
  // return false;
  // }

  /**
   * 批量收集标签发送tag给信鸽
   * 
   * @throws Exception
   */
  public void batchTags(String appId, UserItemOperatePvMongo tagResult) throws Exception {
    logger.info("start batch tag");
    Push p = new Push();
    if(!StringUtils.isEmpty(appId)) {
      p.setAppId(Integer.parseInt(appId));
      p.setClientType(CLIENT_TYPE.IOS);
      loopSendDelTag(p, tagResult.getIosDelTags());
      loopSendSetTag(p, tagResult.getIosTags());
      p.setClientType(CLIENT_TYPE.ANDROID);
      loopSendDelTag(p, tagResult.getAndroidDelTags());
      loopSendSetTag(p, tagResult.getAndroidTags());
    }
  }

  private void loopSendSetTag(Push p, List<TagTokenPair> t) {
    int start = 0, size = 19, end = start + size;
    if(!t.isEmpty()) {
      if(t.size() <= size) {
        end = t.size();
      }
      while(true) {
        List<TagTokenPair> tags = t.subList(start, end);
        xingePush.setTagByXinge(tags, p);
        if(end==t.size())
          break;
        start = end;
        end = start + size;
        if(end>=t.size())
          end=t.size();
      }
    }
  }

  
//  public static void main(String[] args) {
//  PushTagXinGe173APPApi p=new PushTagXinGe173APPApi();
//    
//  int j=19;
//  while(j!=1000){
//    List<TagTokenPair> t=new ArrayList<TagTokenPair>();
//    for(int i = 0; i < j; i++) {
//     t.add(new TagTokenPair("", "")); 
//    
//    }
//    p.loopSendSetTag(new Push(), t);
//    j++;
//  }
//}
  private void loopSendDelTag(Push p, List<TagTokenPair> t) {
    int start = 0, size = 19, end = start + size;
    if(!t.isEmpty()) {
      if(t.size() <= size) {
        end = t.size();
      }
      while(true) {
        List<TagTokenPair> tags = t.subList(start, end);
        xingePush.delTagByXinge(tags, p);
        if(end==t.size())
          break;
        start = end;
        end = start + size;
        if(end>=t.size())
          end=t.size();
      }
    }
  }

  public void batchTags(Map<String, UserItemOperatePvMongo> map) throws Exception {
    for(String appId : map.keySet()) {
      Push p = new Push();
      if(!StringUtils.isEmpty(appId)) {
        p.setAppId(Integer.parseInt(appId));
        UserItemOperatePvMongo u = map.get(appId);
        p.setClientType(CLIENT_TYPE.IOS);
        loopSendDelTag(p, u.getIosDelTags());
        loopSendSetTag(p, u.getIosTags());
        p.setClientType(CLIENT_TYPE.ANDROID);
        loopSendDelTag(p, u.getAndroidDelTags());
        loopSendSetTag(p, u.getAndroidTags());
      }
    }
  }

  public void app(ClientLogCollection id, ClientLogCollection value, UserItemOperatePvMongo tagResult) throws Exception {
    if(value.getItemTypeE() == COLLECTION_ITEM_TYPE.APP) {
      if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.INSTALL) {
        setGameTag(value, id.getPushToken(), "24_10",tagResult);
      }
      // if(id.getOperatorTypeE() == COLLECTION_OPERATOR_TYPE.UNINSTALL) {
      // delGameTag(value, id.getPushToken());
      // }
    }
  }

  /**
   * 访问量 礼包标签
   * 
   * @param id
   * @param value
   * @param baiduId
   * @param clientType
   * @throws Exception
   */
  // public boolean hits(ClientLogCollection id, Value value) throws Exception {
  // if(value.getPv() >= 3) {
  // return setGameTag(value, id.getPushToken());
  // }
  // return false;
  // }

  /**
   * ----------------tool--------------------------------
   */
  public void initStartAndSize(int s, int e) {
    long cha = e - s;
    size = Consts.LIST_MAX_SIZE;
    if(size > e) {
      size = (int) cha;
    }
    start = (int) s;
  }

  public boolean continueStartAndSize(int e) {
    // multi thread
    start = start + size;
    if(size==0)
      return false;
    if(start >= e) return false;
    if(start + size > e) {
      size = (int) e - start;
    }
    return true;
  }

  // public boolean setGiftTag(ClientLogCollection value, String baiduId) throws
  // Exception {
  // setGameTag(value, baiduId);
  // if(!StringUtils.isEmpty(value.getGameCode()))
  // setTag(baiduId, value.getGameCode() + "_" +
  // COLLECTION_ITEM_TYPE.GIFT.index);// gaem
  // return true;
  // }

//  public void delGiftTag(ClientLogCollection value, String baiduId) throws Exception {
//    delGameTag(value, baiduId);
//    deleteTag(baiduId, value.getGameCode() + "_" + COLLECTION_ITEM_TYPE.GIFT.index);// gaem
//                                                                                    // platform
//  }

//  public void delGameTag(ClientLogCollection value, String baiduId) throws Exception {
//    try {
//      if(!StringUtils.isEmpty(value.getGameCode())) {
//        deleteTag(baiduId, value.getGameCode());// game
//        deleteTag(baiduId, value.getGameType() + "");// game
//        deleteTag(baiduId, value.getGameStatus() + "");// game
//        deleteTag(baiduId, value.getGamePlatForm() + "");// gaem
//        // platform
//      }
//    }
//    catch(Exception e) {
//      // TODO: handle exception
//    }
//  }

  public boolean setGameTag(ClientLogCollection value, String baiduId, String tag,UserItemOperatePvMongo tagResult) throws Exception {
    setTag(baiduId, value.getGameCode(),tagResult);// game
//    logger.info("tag 1 gamecode "+value.getGameCode());
    setTag(baiduId, tag,tagResult);
//    logger.info("tag 2 tag "+tag);
    sendMultiStr(value.getGameType(), baiduId,tagResult); // code
//    logger.info("tag 3 value.getGameType() "+value.getGameType());
    sendMultiStr(value.getGameStatus(), baiduId,tagResult);
//    logger.info("tag 4 value.getGameStatus() "+value.getGameStatus());
    if(value.getGamePlatForm() != -1) {
      setTag(baiduId, value.getGamePlatForm() + "_P",tagResult);// game
//      logger.info("tag 5 value.getGamePlatForm() "+value.getGamePlatForm());
    }
    return true;
  }

  public void sendMultiStr(String str, String baiduId,UserItemOperatePvMongo tagResult) throws Exception {
    if(str.indexOf(",") >= 0) {
      String[] s = str.split(",");
      for(int i = 0; i < s.length; i++) {
        setTag(baiduId, s[i], tagResult);// game
      }
    }
    else {
      setTag(baiduId, str, tagResult);// game
    }
  }

  public void sendMultiStr(String str, String baiduId, List<TagTokenPair> pairs,UserItemOperatePvMongo tagResult) throws Exception {
    if(str.indexOf(",") >= 0) {
      String[] s = str.split(",");
      for(int i = 0; i < s.length; i++) {
        setTag(baiduId, s[i],tagResult);// game
        pairs.add(new TagTokenPair(s[i], baiduId));
      }
    }
    else {
      pairs.add(new TagTokenPair(str, baiduId));
    }
  }

  public void setTag(String uid, String tag,UserItemOperatePvMongo tagResult) {
    TagTokenPair t = new TagTokenPair(tag, uid);
    if(clientType == CLIENT_TYPE.IOS)
      tagResult.addIosTag(t);
    else
      tagResult.addAndroidTag(t);
  }

  public void sendSetTag(String uid, List<TagTokenPair> pairs, String appId) throws DataNotFoundException {
    Push p = new Push();
    p.setAppId(Integer.parseInt(appId));
    xingePush.setTagByXinge(pairs, p);
  }

  public void sendDeleteTag(String uid, List<TagTokenPair> pairs, String appId) throws DataNotFoundException {
    Push p = new Push();
    p.setAppId(Integer.parseInt(appId));
    xingePush.delTagByXinge(pairs, p);
  }

  public void deleteTag(String uid, String tag,UserItemOperatePvMongo tagResult) {
    TagTokenPair t = new TagTokenPair(tag, uid);
    if(clientType == CLIENT_TYPE.IOS)
      tagResult.addIosDelTag(t);
    else
      tagResult.addAndroidDelTag(t);
  }

  public void inc(String threadName, int setNum) {
    int incc = 50;
    if(setNum != 0 && setNum % incc == 0) {
      mongoTemplate.findAndModify(new Query(new Criteria("threadName").is(threadName)),
          new Update().inc("threadNum", setNum), FindAndModifyOptions.options().upsert(true),
          PushTagExcuteStateInfo.class, "PushTagThreadInfo");
    }
  }

  public void successLogEnd(String c, String appId, int total) {
    List<PushTagExcuteStateInfo> o1 = (List) mongoTemplate.find(new Query().addCriteria(new Criteria("name").is(c+"_"+appId)),
        PushTagExcuteStateInfo.class);
    PushTagExcuteStateInfo excuteStateInfo = o1.get(0);
    excuteStateInfo.setFinishThreadNum(excuteStateInfo.getFinishThreadNum() + 1);
    excuteStateInfo.setSize(excuteStateInfo.getSize() + total);
    excuteStateInfo.setLastUpdate(new Date());
    if(excuteStateInfo.getThreadNum() == excuteStateInfo.getFinishThreadNum())
      excuteStateInfo.setState(PUSH_SEND_TAG_STATE.WAITING);
    mongoTemplate.save(excuteStateInfo);
  }

  /**
   * ----------------mongotemplate------------------
   */

  public List<UserItemOperatePvMongo2> getUserItemOperatePvMongo2(Query query, String name) {
    query.limit(size);
    query.skip(start);
    return mongoTemplate.find(query, UserItemOperatePvMongo2.class, name);
  }

//  public List<ClientLogBestWalkthroughCollection> getBestWalkthroughInstalledGames(Query query, String name) {
//    query.limit(size);
//    query.skip(start);
//    query.with(new Sort(Sort.Direction.DESC, "operatorDate"));
//    return mongoTemplate.find(query, ClientLogBestWalkthroughCollection.class, name);
//
//  }

  public int updateTagStateInMongoBatch(ClientLogCollection id, int state, String collecitonName) {
    try {
      // if(Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME.equals(collecitonName))
      // {
      WriteResult wr = mongoTemplate.updateFirst(
          new Query(Criteria.where("_id.uid").is(id.getUid()).and("_id.serviceId").is(id.getServiceId())
              .and("_id.operatorType").is(id.getOperatorType())), Update.update("value.state", state), collecitonName);
      return wr.getN();
      // }
      // return 0;
    }
    catch(Exception e) {

      return 0;
    }
  }

  /**
   * 信鸽token
   * 
   * @param n
   * @return
   * @throws DataNotFoundException
   */
  public UserTokenBindXinge getXGToken(String t) {
    try {
      List<UserTokenBindXinge> xgToken = mongoTemplate.find(new Query(new Criteria("tokenStr").is(t)),
          UserTokenBindXinge.class);
      if(xgToken == null || xgToken.isEmpty()) {
        UserToken token = httpRequestProvider.getToken(t);
        Query query = new Query();
        query.addCriteria(new Criteria("tokenId").is(token.getId()));
        Update update = Update.update("tokenStr", token.getToken()).set("platForm", token.getPlat());
        UserTokenBindXinge xgt = mongoTemplate.findAndModify(query, update, UserTokenBindXinge.class);
        if(xgt==null)
          xgt=new UserTokenBindXinge();
        xgt.setPlatForm(token.getPlat());
        xgt.setTokenStr(token.getToken());
        return xgt;
      }
      else {
        return xgToken.get(0);
      }
    }
    catch(Exception e) {
      return null;
    }

  }

  public void initClientType(int n) {
    if(n == 6)
      setClientType(CLIENT_TYPE.IOS);
    else
      setClientType(CLIENT_TYPE.ANDROID);
  }

  public CLIENT_TYPE getClientType() {
    return clientType;
  }

  public void setClientType(CLIENT_TYPE clientType) {
    this.clientType = clientType;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Cacheable(value = "videoMobileCMSCache", key = "'gameInfo_X_' + #gameCode ")
  public Map<String, String> getGameCodeTypeAndStatus(String gameCode, GAME_PLATFORM_TYPE type) {
    Map<String, String> typeStatus = null;
    if(type == null) {
      typeStatus = mobile(gameCode);
      if(typeStatus != null) {
        typeStatus.put("platForm", "1");
        return typeStatus;
      }
      typeStatus = pc(gameCode);
      if(typeStatus != null) {
        typeStatus.put("platForm", "2");
        return typeStatus;
      }
    }
    else {
      if(type == GAME_PLATFORM_TYPE.MOBILE) {
        return mobile(gameCode);
      }
      else if(type == GAME_PLATFORM_TYPE.PC) {
        return pc(gameCode);
      }
    }
    return typeStatus;
  }

  private Map<String, String> pc(String gameCode) {
    try {
      Map<String, String> p = new HashMap<String, String>();
      Map<String, String> typeStatus;
      typeStatus = new HashMap<String, String>();
      String url = systemConfigService.getByKey("game_cate_pc") + "/game/info?game_code=" + gameCode;
      String str = HttpUtil.syncPost(url, p, null);
      if(StringUtils.isEmpty(str)) return null;
      JSONObject obj = new JSONObject(str).getJSONObject("data");
      JSONArray info_status = obj.getJSONArray("game_feature");
      typeStatus.put("name", obj.getString("game_name"));
      JSONArray info_type = obj.getJSONArray("game_type");
      typeStatus.put("status", getStrs(info_status).toString());
      typeStatus.put("type", getStrs(info_type).toString());
      typeStatus.put("gameCode", gameCode);
      return typeStatus;
    }
    catch(Exception e) {
      // e.printStackTrace();
      return null;
    }
  }

  private Map<String, String> mobile(String gameCode) {
    try {
      Map<String, String> p = new HashMap<String, String>();
      Map<String, String> typeStatus;
      typeStatus = new HashMap<String, String>();
      String url = systemConfigService.getByKey("game_cate_mobile") + "/apis/game/info?game_code=" + gameCode;
      String str = HttpUtil.syncPost(url, p, null);
      if(StringUtils.isEmpty(str)) return null;
      JSONObject obj = new JSONObject(str).getJSONObject("data").getJSONObject(gameCode);
      JSONArray info_status = obj.getJSONArray("info_status");
      StringBuffer status = getStrs(info_status);
      JSONObject info_type = obj.getJSONObject("info_type");
      typeStatus.put("type", info_type.getString("name"));
      typeStatus.put("status", status.toString());
      typeStatus.put("pkg", obj.getString("info_package"));
      typeStatus.put("name", obj.getString("info_chname"));
      typeStatus.put("gameCode", gameCode);
      return typeStatus;
    }
    catch(Exception e) {
      // e.printStackTrace();
      return null;
    }
  }

  private StringBuffer getStrs(JSONArray info_status) throws JSONException {
    Map<String, String> p = new HashMap<String, String>();
    StringBuffer status = new StringBuffer();
    for(int i = 0; i < info_status.length(); i++) {
      status.append(info_status.getJSONObject(i).getString("name")).append(",");
    }
    if(status.length() != 0) status = status.deleteCharAt(status.length() - 1);
    return status;
  }


}
