package com.cyou.video.mobile.server.cms.rest.controller.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_ITEM_TYPE;
import com.cyou.video.mobile.server.cms.model.Pagination;
import com.cyou.video.mobile.server.cms.model.push.Push;
import com.cyou.video.mobile.server.cms.model.user.UserTokenBindXinge;
import com.cyou.video.mobile.server.cms.service.push.AutoPushService;
import com.cyou.video.mobile.server.cms.service.push.PushInterface;
import com.cyou.video.mobile.server.cms.service.push.PushService;
import com.cyou.video.mobile.server.cms.service.push.impl.PushTagXinGe173APPApi;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.common.Constants;

/**
 * 数据收集 and pv
 * 
 * @author lusi
 * 
 */
@Controller
@RequestMapping("/rest/push")
public class PushAutoController {

  private Logger LOGGER = LoggerFactory.getLogger(PushAutoController.class);

  @Autowired
  AutoPushService autoPushServiceImpl;

  @Autowired
  PushTagXinGe173APPApi pushTagXinGe173APPApi;

  @Autowired
  private PushInterface xingePush;
  
  @Autowired
  PushService pushService;
  

  @Autowired
  private SystemConfigService systemConfigService;
  
  /**
   * 新建直播推送
   * 
   * @param id
   *          推送任务编号
   * @param model
   * @return
   */
  @RequestMapping(value = "/auto/live", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap autoLive(@RequestBody
  Map<String, Object> params, ModelMap model) {
    try {
      LOGGER.info("自动推送直播被调用");
      autoPushServiceImpl.autoPush(params.get("gameCode").toString(), params.get("roomId").toString(),
          params.get("title").toString(), "",COLLECTION_ITEM_TYPE.LIVE.index+"", null,Integer.parseInt(systemConfigService.getByKey("sys_173app_id")));
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      LOGGER.debug("delete job failed " + e.getMessage());
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  /**
   * 秀场
   * 
   * @param id
   *          推送任务编号
   * @param model
   * @return
   */
  @RequestMapping(value = "/auto", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap auto(@RequestBody
  Map<String, Object> params, ModelMap model) {
    try {
      CLIENT_TYPE ct=null;
      if(params.get("clientType")!=null)
      {
       ct= CLIENT_TYPE.valueOf(params.get("clientType").toString());
      }
      //秀场临时独特性,支持所有应用
      if(Integer.parseInt( params.get("itemType").toString()) == 28) {
        if(params.get("liantongId")!=null){//liantong
          if(systemConfigService.getByKey("liantong_id").equals(params.get("liantongId").toString()))
          {
            
            LOGGER.info("联通秀场调用 应用id "+params.get("liantongId").toString());
            autoPushServiceImpl.autoPush(params.get("tag").toString(), params.get("id").toString(),
                params.get("title").toString(),params.get("anchor")==null?"":params.get("anchor").toString(), params.get("itemType").toString(), ct,Integer.parseInt(systemConfigService.getByKey("liantong_id").toString()));//联通秀场单独
          }
        }else{//普通秀场
          String[] appIds=systemConfigService.getByKey("show_auto_appIds").split(",");
          for(int i = 0; i < appIds.length; i++) {
            autoPushServiceImpl.autoPush(params.get("tag").toString(), params.get("id").toString(),
                params.get("title").toString(),params.get("anchor")==null?"":params.get("anchor").toString(), params.get("itemType").toString(), ct,Integer.parseInt(appIds[i]));
          }
        }
      }else{//其它
        String[] appIds=systemConfigService.getByKey("pv_analyseis_appid").split(",");
        for(int i = 0; i < appIds.length; i++) {
          autoPushServiceImpl.autoPush(params.get("tag").toString(), params.get("id").toString(),
              params.get("title").toString(),params.get("anchor")==null?"":params.get("anchor").toString(), params.get("itemType").toString(), ct,Integer.parseInt(appIds[i]));
        }
      }
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
//      e.printStackTrace();
      model.addAttribute("message", e.getMessage());
    }
    return model;
  }
  
  
  /**
   * 新建礼包推送
   * 
   * @param id
   *          推送任务编号
   * @param model
   * @return
   */
  @RequestMapping(value = "/auto/gift", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap autoGift(@RequestBody
  Map<String, Object> params, ModelMap model) {
    try {
      LOGGER.info("自动推送礼包被调用");
      CLIENT_TYPE ct=null;
      if(params.get("clientType")!=null)
      {
       ct= CLIENT_TYPE.valueOf(params.get("clientType").toString());
      }
      String[] appIds=systemConfigService.getByKey("pv_analyseis_appid").split(",");
      for(int i = 0; i < appIds.length; i++) {
        autoPushServiceImpl.autoPush(params.get("gameCode").toString(), params.get("id").toString(), params.get("title")
            .toString(),"", COLLECTION_ITEM_TYPE.GIFT.index+"", ct,Integer.parseInt(appIds[i]));
      }
  
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      LOGGER.debug("delete job failed " + e.getMessage());
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  @RequestMapping(value = "/cmsauto", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap autoShow(
      @RequestParam(required = false, defaultValue = "") String tag,
      @RequestParam(required = false, defaultValue = "") String id,
      @RequestParam(required = false, defaultValue = "") String title,
      @RequestParam(required = false, defaultValue = "") String itemType,
      @RequestParam(required = false, defaultValue = "") String clientType,ModelMap model) {
    try {
      CLIENT_TYPE ct=null;
      if(!StringUtils.isEmpty(clientType))
      {
       ct= CLIENT_TYPE.valueOf(clientType);
      }
      autoPushServiceImpl.autoPush(tag, id,title,"", itemType, ct,Integer.parseInt(systemConfigService.getByKey("sys_173app_id")));
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      model.addAttribute("message", e.getMessage());
    }
    return model;
  }
  
  /**
   * 单播推送接口
   * 
   * @param id
   *          推送任务编号
   * @param model
   * @return
   */
  @RequestMapping(value = "/auto/pushOne", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap pushOne(@RequestBody
  Map<String, String> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      String token = params.get("token");
      if(StringUtils.isEmpty(token)) {
        LOGGER.info("token is null bind baidu id failed!!!!! " + token);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return model;
      }
      else {
        LOGGER.info("单播推送调用");
        autoPushServiceImpl.pushFeedBack(token, params.get("title"), params.get("content"),
            params.get("itemType"), params.get("appId"));
        model.addAttribute("message", "SUCCESS");
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      LOGGER.debug("delete job failed " + e.getMessage());
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  /**
   * 游戏中心需要的推送列表
   * @param params
   * @param model
   * @return
   */
  @RequestMapping(value = "/listPush", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap list(@RequestBody
  Map<String, Object> params, ModelMap model,HttpServletResponse response,HttpServletRequest request) {
    try {
      String ua = request.getHeader("i");
      if(StringUtils.isEmpty(ua)){
        model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
        return model;
      }
      if(ua.indexOf("iPhone")>=0){
        params.put("clientType", "IOS");
      }else{
        params.put("clientType", "ANDROID");
      }
      Pagination pagination = pushService.listPush(params);
      model.addAttribute("page", pagination);
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/html;charset=UTF-8");
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      LOGGER.error("[method: listPush()] Get Push list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }
  
  /**
   * 用户安装应用
   * @param params
   * @param model
   * @return
   */
  @RequestMapping(value = "/listApp", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listApp(@RequestBody
  Map<String, Object> params, ModelMap model) {
    try {
      Pagination pagination = pushService.listApp(params);
      model.addAttribute("page", pagination);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      LOGGER.error("[method: listPush()] Get Push list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }
  
  @RequestMapping(value = "/localPush", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap localPush(@RequestBody
  Map<String, Object> params, ModelMap model,HttpServletResponse response) {
    try {
      params.put("pushType", "AUTO_LOCAL");
      Pagination pagination = pushService.listPush(params);
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/html;charset=UTF-8");
      model.addAttribute("page", pagination);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      LOGGER.error("[method: listPush()] Get Push list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }
  
  
  /**
   * 删除的版本渠道标签 17173app
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/deleteVandC", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap deleteVandC(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(params.get("token").toString());
      if(xingeToken == null) {
        model.put("message", new ArrayList());
      }
      else {
        Push p = new Push();
        pushTagXinGe173APPApi.initClientType(xingeToken.getPlatForm());
        p.setClientType(pushTagXinGe173APPApi.getClientType());
        p.setAppId(Integer.parseInt(systemConfigService.getByKey("sys_173app_id")));
        JSONObject obj = xingePush.queryUserTag(xingeToken.getXgToken(), p);
        JSONArray arr=obj.getJSONObject("result").getJSONArray("tags");
        List<String> result=new ArrayList<String>();
        for(int i = 0; i < arr.length(); i++) {
          String tag=arr.getString(i);
          if(tag.indexOf("V_")>=0||tag.indexOf("C_")>=0){
            result.add(tag);
          }
        }
        model.put("message", result);
      }
    }
    catch(Exception e) {
      model.put("message", "manual updateLogInfo exception " + e.getMessage());
//      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return model;
  }

  
  /**
   * 删除的版本渠道标签  最强攻略
   * 
   * @param params
   * @param psw
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/deleteVandCByAppId", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap deleteVandCByAppId(@RequestBody
  Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      String appId=params.get("appId").toString();
      UserTokenBindXinge xingeToken = pushTagXinGe173APPApi.getXGToken(params.get("token").toString());
      if(xingeToken == null) {
        model.put("message", new ArrayList());
      }
      else {
        Push p = new Push();
        pushTagXinGe173APPApi.initClientType(xingeToken.getPlatForm());
        p.setClientType(pushTagXinGe173APPApi.getClientType());
        p.setAppId(Integer.parseInt(appId));
        JSONObject obj = xingePush.queryUserTag(xingeToken.getXgToken(), p);
        if(obj == null) {
          model.put("message", new ArrayList());
          return model;
        }
        JSONArray arr=obj.getJSONObject("result").getJSONArray("tags");
        List<String> result=new ArrayList<String>();
        for(int i = 0; i < arr.length(); i++) {
          String tag=arr.getString(i);
          if(tag.indexOf("V_")>=0||tag.indexOf("C_")>=0){
            result.add(tag);
          }
        }
        model.put("message", result);
      }
    }
    catch(Exception e) {
      model.put("message", new ArrayList());
//      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_OK);
    }
    return model;
  }
  
  /**
   *返回android秀场诱导参数
   */
  @RequestMapping(value = "/showDownLoadAndroid", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap showDownLoadAndroid( HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
        String s = systemConfigService.getByKey("show_download_android");
        HashMap<String, String> data = new HashMap<String, String>();  
        // 将json字符串转换成jsonObject  
        JSONObject jsonObject =new  JSONObject(s);  
        Iterator it = jsonObject.keys();  
        // 遍历jsonObject数据，添加到Map对象  
        while (it.hasNext())  
        {  
            String key = String.valueOf(it.next());  
            String value = (String) jsonObject.get(key);  
            data.put(key, value);  
        }  
        model.put("message", data);
    }
    catch(Exception e) {
      model.put("message", new ArrayList());
//      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_OK);
    }
    return model;
  }
}
