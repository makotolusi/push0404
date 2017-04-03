package com.cyou.video.mobile.server.cms.web.controller.push;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyou.video.mobile.server.cms.model.collection.StatisticJobLastUpdateTime;
import com.cyou.video.mobile.server.cms.model.collection.SystemConfig;
import com.cyou.video.mobile.server.cms.model.push.Push;
import com.cyou.video.mobile.server.cms.model.user.UserTokenBindXinge;
import com.cyou.video.mobile.server.cms.service.collection.ClientLogCollectionService;
import com.cyou.video.mobile.server.cms.service.collection.MultiThreadExcuteXinGeService;
import com.cyou.video.mobile.server.cms.service.push.AppSelectService;
import com.cyou.video.mobile.server.cms.service.push.AutoPushService;
import com.cyou.video.mobile.server.cms.service.push.PushInterface;
import com.cyou.video.mobile.server.cms.service.push.PushService;
import com.cyou.video.mobile.server.cms.service.push.impl.PushTagXinGe173APPApi;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.cms.service.utils.HttpRequestProvider;
import com.cyou.video.mobile.server.common.Constants;
import com.tencent.xinge.TagTokenPair;

/**
 * 
 * 鎺ㄩ�涓�簺鎵嬪姩璁剧疆椤�鍙湁绠＄悊鍛樺彲浠ョ湅鍒扮殑椤甸潰
 * 
 * @author LUSI
 */
@Controller
@RequestMapping("/web/manual")
public class ManualConfigController {

  private Logger logger = LoggerFactory.getLogger(ManualConfigController.class);

  @Autowired
  ClientLogCollectionService clientLogCollectionService;

  @Autowired
  AutoPushService autoPushService;

  @Autowired
  AppSelectService appSelectService;

  @Autowired
  MultiThreadExcuteXinGeService multiThreadExcuteXinGeService;

  @Autowired
  private SystemConfigService systemConfigService;

  @Autowired
  private PushInterface xingePush;

  @Autowired
  HttpRequestProvider httpRequestProvider;

  @Autowired
  PushTagXinGe173APPApi pushTagXinGe173APPApi;

  @Autowired
  PushService pushService;

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView pushListPage() {
    List<StatisticJobLastUpdateTime> list = null;
    ModelAndView m = null;
    try {
      long start = new Date().getTime();
      logger.debug("updateLogInfo start  : " + new Date());
      list = clientLogCollectionService.getStatisticJobLastUpdateTime();
      List l = clientLogCollectionService.getPushTagExcuteStateInfo();
      long time = new Date().getTime() - start;
      logger.debug("excute time : " + (time / 1000));
      m = new ModelAndView("/activity/manual", "lastPvTime", list);
      m.addObject("pushTagExcuteStateInfo", l);
      // m.addObject("threadTotal", pushTagService.getThreadTotal());
      // m.addObject("threadNumList", pushTagService.getThreadNumList());
      m.addObject("appId", systemConfigService.getByKey("sys_173app_id"));
      // thread num
      // PushTagExcuteStateInfo p = pushTagService.getSysThreadNum();
      // if(p != null) {
      // m.addObject("sysThreadN", p.getThreadNum() + "");
      // }
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("manual updateLogInfo exception " + e.getMessage());
      return m;
    }

    return m;

  }

  @RequestMapping(value = "/joblog/lastMapReduceTime", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap lastMapReduceTime(ModelMap model) {
    try {
      model.put("data", clientLogCollectionService.getStatisticJobLastUpdateTime());
    }
    catch(Exception e) {
      e.printStackTrace();
      model.put("message", "lastMapReduceTime  exception " + e.getMessage());
      logger.error("manual lastMapReduceTime exception " + e.getMessage());
    }
    return model;
  }

  @RequestMapping(value = "/joblog/tagLog", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap getPushTagExcuteStateInfo(ModelMap model) {
    try {
      model.put("data", clientLogCollectionService.getPushTagExcuteStateInfo());
    }
    catch(Exception e) {
      e.printStackTrace();
      model.put("message", "tagLog  exception " + e.getMessage());
      logger.error("manual tagLog exception " + e.getMessage());
    }

    model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    return model;
  }

  @RequestMapping(value = "/syncApp", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap syncApp(@RequestBody
  Map<String, String> params, ModelMap model) {
    try {
      appSelectService.syncApp();
    }
    catch(Exception e) {
      e.printStackTrace();
      model.put("message", "syncApp  exception " + e.getMessage());
      logger.error("manual syncApp exception " + e.getMessage());
    }

    model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    return model;
  }

  /**
   * 鍙戦�鐧惧害鏍囩
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/sendPushTags", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap sendPushTags(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    if(params.get("psw") != null && "lusizuiqiang".equals(params.get("psw"))) {
      if(multiThreadExcuteXinGeService.existRunningThread()) {
        model.put("message", "other thread is running!");
        return model;
      }
      return multiThreadExcuteXinGeService.sendPushTags(params, model);
    }
    else {
      // model.put("message", "璇疯緭鍏ュ瘑鐮�);
      return model;
    }
  }

  @RequestMapping(value = "/sendOld", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap sendOld(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    if(params.get("psw") != null) {
      if(multiThreadExcuteXinGeService.existRunningThread()) {
        model.put("message", "other thread is running!");
        return model;
      }
      multiThreadExcuteXinGeService.oldData(params, model);
      return model;
    }
    else {
      // model.put("message", "璇疯緭鍏ュ瘑鐮�);
      return model;
    }
  }

  @RequestMapping(value = "/sendOld2", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap sendOld2(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    if(params.get("psw") != null && "lusizuiqiang".equals(params.get("psw"))) {
      if(multiThreadExcuteXinGeService.existRunningThread()) {
        model.put("message", "other thread is running!");
        return model;
      }
      multiThreadExcuteXinGeService.oldData2(params, model);
      return model;
    }
    else {
      // model.put("message", "璇疯緭鍏ュ瘑鐮�);
      return model;
    }
  }

  /**
   * 鍙戦�鐧惧害鏍囩
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/oldPushData", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap oldPushData(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    return multiThreadExcuteXinGeService.pushHistoryToMongo(params, model);
  }

  /**
   * 鍙戦�鏀荤暐2.0app鏍囩
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/queryPushByID", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap queryPushByID(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      Push push = pushService.getPushById(params.get("pushId").toString());
      if(push == null) {
        model.put("message", "token 未找到请重新激活 ");
      }
      else {
        model.put("message", push.getSentLogs());
      }
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  /**
   * 鍙戦�鏀荤暐2.0app鏍囩
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/sendBestWalkThrough", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap sendWalkThroughAppTags(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    if(params.get("psw") != null && "lusizuiqiang".equals(params.get("psw"))) {
      if(multiThreadExcuteXinGeService.existRunningThread()) {
        model.put("message", "other thread is running!");
        return model;
      }
      return multiThreadExcuteXinGeService.sendBestWalkThrough(params, model);
    }
    else {
      // model.put("message", "璇疯緭鍏ュ瘑鐮�);
      return model;
    }
  }

  /**
   * 鏌ョ湅鐧惧害鏍囩
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/queryUserTag", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap queryUserTag(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      // "9451f39ac0bffa213d51f71662a9dd2443272685_gl@qmyx"
      String ps=params.get("token").toString();
      UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(ps.split(",")[1]);
      if(xingeToken == null) {
        model.put("message", "token 未找到请重新激活 ");
      }
      else {
        Push p = new Push();
        pushTagXinGe173APPApi.initClientType(xingeToken.getPlatForm());
        p.setClientType(pushTagXinGe173APPApi.getClientType());
        p.setAppId(Integer.parseInt(ps.split(",")[0]));
        JSONObject obj = xingePush.queryUserTag(xingeToken.getXgToken(), p);
        // XingeApp xinge = new XingeApp(2200034567L,
        // "dcaa7cf252fd969e13d152bf2b1f0d3a");
        // JSONObject ret =
        // xinge.queryTokenTags("de50e20f7d6387a345ed7d9f9cc0f6ba96aca50e415a1a3cce9505a6484dddb9");
        // System.out.println(ret);
        model.put("message", obj.toString());
      }
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  /**
   * 鏌ョ湅鐧惧害鏍囩
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/deleteUserTag", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap deleteUserTag(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(params.get("token").toString());
      Push p = new Push();
      pushTagXinGe173APPApi.initClientType(xingeToken.getPlatForm());
      p.setClientType(pushTagXinGe173APPApi.getClientType());
      p.setAppId(Integer.parseInt(systemConfigService.getByKey("sys_173app_id")));
      JSONObject obj = xingePush.queryUserTag(xingeToken.getXgToken(), p);
      JSONArray arr = obj.getJSONObject("result").getJSONArray("tags");
      List<TagTokenPair> pairs = new ArrayList<TagTokenPair>();
      for(int i = 0; i < arr.length(); i++) {
        String o = arr.get(i).toString();
        pairs.add(new TagTokenPair(o, xingeToken.getXgToken()));
      }
      xingePush.delTagByXinge(pairs, p);
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      // e.printStackTrace();
    }
    return model;
  }

  /**
   * 鍒犻櫎log鏃ュ織
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/delLog/{name}", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap delLog(@PathVariable("name")
  String name, @RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      // pushTagService.removePushTagLogByName(name);
      model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
      // e.printStackTrace();
    }
    return model;
  }

  /**
   * 璁剧疆绯荤粺绾跨▼鍙傛暟
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/sysThread/{sysThreadN}", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap sysThread(@PathVariable("sysThreadN")
  int sysThreadN, @RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      // pushTagService.setSysThreadNum(sysThreadN);
      model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
    }
    return model;
  }

  /**
   * 璁剧疆173appID
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/set173APPId", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap set173APPId(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      SystemConfig sc = new SystemConfig();
      sc.setKey("appId");
      sc.setValue(params.get("appId").toString());
      systemConfigService.save(sc);
      model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
    }
    return model;
  }

  /**
   * 璁剧疆绯荤粺绾跨▼鍙傛暟
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/updateWaiting", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateWaiting(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      pushService.updateWaiting();
      model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
    }
    return model;
  }

  @RequestMapping(value = "/delPushHistory", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap delPushHistory(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      pushService.delPushHistory();
      model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
    }
    return model;
  }
}
