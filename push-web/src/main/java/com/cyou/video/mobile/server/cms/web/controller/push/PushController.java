package com.cyou.video.mobile.server.cms.web.controller.push;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_ITEM_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.CONTENT_SOURCE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_JOB_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_STATE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_TYPE;
import com.cyou.video.mobile.server.cms.common.Consts.PUSH_USER_SCOPE;
import com.cyou.video.mobile.server.cms.model.Pagination;
import com.cyou.video.mobile.server.cms.model.push.Push;
import com.cyou.video.mobile.server.cms.model.security.Manager;
import com.cyou.video.mobile.server.cms.model.sys.ContentType;
import com.cyou.video.mobile.server.cms.model.sys.ContentType.CONTENT_TYPE_CATE;
import com.cyou.video.mobile.server.cms.service.push.AutoPushService;
import com.cyou.video.mobile.server.cms.service.push.PushService;
import com.cyou.video.mobile.server.cms.service.push.impl.PushServiceImpl;
import com.cyou.video.mobile.server.cms.service.sys.ContentTypeService;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.common.Constants;

/**
 * 
 * @author LUSI
 */
@Controller
@RequestMapping("/web/push")
public class PushController {

  private Logger logger = LoggerFactory.getLogger(PushServiceImpl.class);

  @Autowired
  PushService pushService;

  @Autowired
  AutoPushService autoPushServiceImpl;

  @Autowired
  private SystemConfigService systemConfigService;

  @Autowired
  ContentTypeService contentTypeService;

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView pushListPage(HttpServletRequest reques, ModelMap model) {
    try {
      model.put("source", CONTENT_SOURCE.values());
      model.put("appId", reques.getParameter("appId"));
      if(systemConfigService.getByKey("sys_wangyoubaobei_app_id").equals(reques.getParameter("appId")))// 网游宝贝
      {
        COLLECTION_ITEM_TYPE[] t = new COLLECTION_ITEM_TYPE[]{COLLECTION_ITEM_TYPE.NEWS,
            COLLECTION_ITEM_TYPE.NEWS_LIST_PAGE, COLLECTION_ITEM_TYPE.GAME, COLLECTION_ITEM_TYPE.GIFT,
            COLLECTION_ITEM_TYPE.GIFT_QIANG_PAGE, COLLECTION_ITEM_TYPE.GIFT_TAO_PAGE, COLLECTION_ITEM_TYPE.OTHER};
        model.put("itemType", t);
      }
      else {
        COLLECTION_ITEM_TYPE[] t = new COLLECTION_ITEM_TYPE[]{COLLECTION_ITEM_TYPE.LIVE,
            COLLECTION_ITEM_TYPE.WALKTHROUGH, COLLECTION_ITEM_TYPE.GIFT, COLLECTION_ITEM_TYPE.VIDEO,
            COLLECTION_ITEM_TYPE.NEWS, COLLECTION_ITEM_TYPE.JIONG};
        model.put("itemType", t);
      }
      model.put("pushPlatForm", reques.getParameter("pushPlatForm"));
      model.put("appKey", reques.getParameter("appKey"));
      model.put("secretKey", reques.getParameter("secretKey"));
      model.put("appKey_ios", reques.getParameter("appKey_ios"));
      model.put("secretKey_ios", reques.getParameter("secretKey_ios"));
      return new ModelAndView("/activity/pushlist", "type", model);
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @RequestMapping(value = "/pushJob", method = RequestMethod.GET)
  public ModelAndView pushJob() {
    return new ModelAndView("/push/pushJob");
  }

  @RequestMapping(value = "/listPushAuto", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listPushAuto(@RequestBody
  Map<String, Object> params, ModelMap model) {
    try {
      params.put("pageSize", Pagination.PAGESIZE);
      Pagination pagination = new Pagination();
      pagination.setContent(autoPushServiceImpl.listAutoPush());
      model.addAttribute("page", pagination);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: listPush()] Get Push list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }

    return model;
  }

  @RequestMapping(value = "/condition", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap createPush(@RequestBody
  Push push, HttpServletResponse response, ModelMap model, HttpServletRequest request) throws Exception {
    try {
      Manager manager = (Manager) request.getSession().getAttribute(Consts.SESSION_MANAGER);
      if(manager == null) {
        model.put("message", "您尚未登录系统，请重新登录！");
        return model;
      }
      else {
        push.setAppId(manager.getAppId());
        push.setManager(manager);
      }
      // 多选推送客户端
      List<CLIENT_TYPE> clientType = new ArrayList<CLIENT_TYPE>();
      CLIENT_TYPE ct = CLIENT_TYPE.ALL;
      if(push.getClientType() == CLIENT_TYPE.ALL) {
        clientType.add(CLIENT_TYPE.ANDROID);
        clientType.add(CLIENT_TYPE.IOS);
      }
      else
        clientType.add(push.getClientType());
      int time = 0;
      for(Iterator iterator = clientType.iterator(); iterator.hasNext();) {
        CLIENT_TYPE client_TYPE = (CLIENT_TYPE) iterator.next();
        push.setClientType(client_TYPE);
        if(push.getKeyValue() != null&&manager.getAppId()!=null) {
          ContentType contentType = contentTypeService.getByIndexAndType(push.getKeyValue().get("p"),CONTENT_TYPE_CATE.SERVICE.name());
          if(contentType != null) {
            ContentType c = new ContentType();
            c.setDesc(contentType.getDesc());
            c.setIndex(contentType.getIndex());
            c.setName(contentType.getName());
            if(time!=1)
              pushService.gameSwitch(push, manager.getAppId(), contentType);
            push.setContentType(c);
          }
        } 
        // 推送一次开始
        push.setJobState(PUSH_JOB_STATE.ENABLE);
        if(!push.getTags().isEmpty()) {
          push.setUserScope(PUSH_USER_SCOPE.TAG);
        }
        push.setSendState(PUSH_SEND_STATE.FAIL);
        switch(push.getPushType()) {
          case IMMEDIATE :
            push.setId(null);
            pushService.createPush(push);
            push = pushService.pushInfo(push);
            if(push.getSendState() == PUSH_SEND_STATE.FAIL)
              model.put("message", Constants.CUSTOM_ERROR_CODE.FAILED.toString() + " msg : " + push.getSentLogs());
            else {
              model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
            }
            break;
          case TIMING :
            JSONObject obj = null;
            if(time==1)
              push.setId(null);
            if(StringUtils.isEmpty(push.getId())) {// insert
              pushService.savePush(push);
              // 调用cms-job创建新quartz任务
//              push.setCronExp("0 29 15 25 09 ? 2014");
              obj = pushService.postNewJob(push.getId(), push.getCronExp());
            }
            else {
              obj = pushService.modifyJob(push.getId(), push.getCronExp());
            }
            if(obj.getBoolean("success")) {
              obj = (JSONObject) obj.getJSONArray("triggers").get(0);
              push.setJobState(PUSH_JOB_STATE.ENABLE);
              push.setNextFireTime(obj.getString("nextFireTime"));
              push.setStartTime(obj.getString("startTime"));
              push.setPreviousFireTime(obj.getString("previousFireTime"));
              response.setStatus(HttpServletResponse.SC_OK);
              model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
            }
            else {
              push.setJobState(PUSH_JOB_STATE.DISABLE);
              response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              model.put("message", obj.getString("msg"));
            }
            push.setSendState(PUSH_SEND_STATE.WAIT);
            break;
          case AUTO :
            if(ct == CLIENT_TYPE.ALL) {
              push.setClientType(CLIENT_TYPE.ALL);
            }
             Push p=pushService.getPushById(push.getId());
             push.setContentType(p.getContentType());
            break;
          default :
            break;
        }
        pushService.savePush(push);
        time++;
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("push  failed!!");
      push.setJobState(PUSH_JOB_STATE.DISABLE);
      pushService.savePush(push);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return model;
    }
    return model;
  }



  @RequestMapping(value = "/list", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap list(@RequestBody
  Map<String, Object> params, ModelMap model, HttpServletRequest request) {
    try {
      Manager manager = (Manager) request.getSession().getAttribute(Consts.SESSION_MANAGER);
      if(manager == null) {
        model.put("message", "您尚未登录系统，请重新登录！");
      }
      else {
//        params.put("appId", manager.getAppId());
      }
      Pagination pagination = pushService.listPush(params);
      model.addAttribute("page", pagination);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("[method: listPush()] Get Push list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  @RequestMapping(value = "/listAutoPush", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listAutoPush(@RequestBody
  Map<String, Object> params, ModelMap model, HttpServletRequest request) {
    try {
      Pagination pagination = pushService.listPush(params);
      model.addAttribute("page", pagination);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("[method: listPush()] Get Push list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  /**
   * 根据推送编号获取推送信息
   * 
   * @param id
   *          推送编号
   * @param model
   * @return
   */
  @RequestMapping(value = "/get", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap getPushById(HttpServletRequest reques, ModelMap model) {
    try {
      Push push = pushService.getPushById(reques.getParameter("id"));
      model.addAttribute("message", "SUCCESS");
      model.addAttribute("push", push);
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("[method: getPushById()] : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }
    return model;
  }

  // /**
  // * 再次发送(默认类型是立即发送的推送任务)
  // *
  // * @param push
  // * 推送对象实例
  // * @param model
  // * @return
  // */
  // @RequestMapping(value = "sendAgain", method = RequestMethod.POST)
  // @ResponseBody
  // public ModelMap sendAgain(@RequestBody
  // Push push, ModelMap model) {
  // if(push.getContentTy() != null)
  // push.setContentType(COLLECTION_ITEM_TYPE.valueOf(push.getContentTy()));
  // if(!push.getTags().isEmpty()) {
  // push.setUserScope(PUSH_USER_SCOPE.TAG);
  // }
  // // 立即推送
  // if(push.getPushType() == PUSH_TYPE.AUTO) {
  // pushService.updatePush(push);
  // model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
  // return model;
  // }
  // else {
  // push = pushService.pushInfo(push);
  // try {
  // push.setId(null);
  // String pushId = pushService.createPush(push);
  // push.setId(pushId);
  // if(push.getSendState() == PUSH_SEND_STATE.FAIL) {
  // model.put("message", push.getSentLogs());
  // }
  // else {
  // model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
  // }
  // return model;
  // }
  // catch(Exception e) {
  // e.printStackTrace();
  // logger.error("insert push object failed!!");
  // model.put("message", Constants.CUSTOM_ERROR_CODE.FAILED.toString() + " " +
  // e.getMessage());
  // return model;
  // }
  // }
  //
  // }

  /**
   * 修改定时推送任务信息
   * 
   * @param push
   *          推送对象实例
   * @param model
   * @return
   */
  @RequestMapping(value = "update", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updatePush(@RequestBody
  Push push, ModelMap model) {
    try {
      if(!push.getTags().isEmpty()) {
        push.setUserScope(PUSH_USER_SCOPE.TAG);
      }
      push.setPushType(PUSH_TYPE.TIMING);
      Calendar c = Calendar.getInstance();
      push.setCronExp(push.getCronExp() + " " + c.get(Calendar.YEAR));
      // pushService.modifyPush(push);
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("[method: getPushById()] : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }
    return model;
  }

  @RequestMapping(value = "saveAutoLocal", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap saveAutoLocal(@RequestBody
      Map<String,String> params, ModelMap model,HttpServletRequest request) {
    try {
      Push p = pushService.getPushById(params.get("id"));
      p.setTitle(params.get("title"));
      p.setContent(params.get("content"));
      p.setContentColor(params.get("contentColor"));
      p.setTitleColor(params.get("titleColor"));
      Map<String, String> keyValue=new HashMap<String, String>();
      keyValue.put("time", params.get("time"));
      p.setKeyValue(keyValue);
      pushService.savePush(p);
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("[method: getPushById()] : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }
    return model;
  }
  
  /**
   * 修改推送任务状态
   * 
   * @param id
   *          推送任务编号
   * @param state
   *          任务状态
   * @param model
   * @return
   */
  @RequestMapping(value = "{id}/status", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updatePushJobStatus(@PathVariable("id")
  String id, @RequestParam("status")
  PUSH_JOB_STATE state, ModelMap model) {
    try {
      pushService.modifyStateById(id, state);
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      logger.error("[method: updatePushJobStatus()] : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  /**
   * 自动推送启停
   * 
   * @param id
   *          推送任务编号
   * @param state
   *          任务状态
   * @param model
   * @return
   */
  @RequestMapping(value = "/autoPushStatus", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap autoPushStatus(@RequestParam("id")
  String id, @RequestParam("status")
  String state, ModelMap model) {
    try {
      PUSH_JOB_STATE c = null;
      if(StringUtils.isEmpty(state)) {
        c = PUSH_JOB_STATE.ENABLE;
      }
      else
        c = PUSH_JOB_STATE.valueOf(state);
      pushService.modifyAutoPushStateById(id, c);
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      logger.error("[method: updatePushJobStatus()] : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  /**
   * 修改推送任务状态
   * 
   * @param id
   *          推送任务编号
   * @param state
   *          任务状态
   * @param model
   * @return
   */
  @RequestMapping(value = "/auto/{id}/status", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateAutoPushJobStatus(@PathVariable("id")
  String id, @RequestParam("status")
  PUSH_JOB_STATE state, ModelMap model) {
    try {
      pushService.modifyAutoPushStateById(id, state);
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      logger.error("[method: updatePushJobStatus()] : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

  /**
   * 删除推送任务
   * 
   * @param id
   *          推送任务编号
   * @param model
   * @return
   */
  @RequestMapping(value = "/{id}/{type}/delete", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap deletePush(@PathVariable("id")
  String id, @PathVariable("type")
  PUSH_TYPE type, ModelMap model, HttpServletResponse response) {
    try {
      if(type == PUSH_TYPE.TIMING) {
        JSONObject obj = pushService.deleteJob(id);
        if(obj.getBoolean("success")) {
          pushService.deletePush(id);
          response.setStatus(HttpServletResponse.SC_OK);
          model.put("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
        }
        else {
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          model.put("message", obj.getString("msg"));
        }
      }
      else {
        pushService.deletePush(id);
        response.setStatus(HttpServletResponse.SC_OK);
      }
      model.addAttribute("message", "SUCCESS");
    }
    catch(Exception e) {
      logger.debug("delete job failed " + e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      model.addAttribute("message", e.getMessage());
    }

    return model;
  }

}
