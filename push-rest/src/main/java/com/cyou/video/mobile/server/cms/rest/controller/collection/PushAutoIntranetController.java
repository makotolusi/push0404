package com.cyou.video.mobile.server.cms.rest.controller.collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.service.push.AutoPushService;
import com.cyou.video.mobile.server.cms.service.push.PushService;
import com.cyou.video.mobile.server.cms.service.push.impl.PushTagXinGe173APPApi;

/**
 * 数据收集 and pv
 * 
 * @author lusi
 * 
 */
@Controller
@RequestMapping("/rest/intranet/push")
public class PushAutoIntranetController {

  private Logger LOGGER = LoggerFactory.getLogger(PushAutoIntranetController.class);

  @Autowired
  AutoPushService autoPushServiceImpl;

  @Autowired
  PushTagXinGe173APPApi pushTagXinGe173APPApi;

  @Autowired
  PushService pushService;
  

  
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
//      autoPushServiceImpl.autoPush(tag, id,title, itemType, ct);
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
  public ModelMap pushOne(
      @RequestParam(required = false, defaultValue = "") String title,
      @RequestParam(required = false, defaultValue = "") String content,
      @RequestParam(required = false, defaultValue = "") String itemType,
      @RequestParam(required = false, defaultValue = "") String appId,
      @RequestParam(required = false, defaultValue = "") String token,
      HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    try {
      if(StringUtils.isEmpty(token)) {
        LOGGER.info("token is null bind baidu id failed!!!!! " + token);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return model;
      }
      else {
        LOGGER.info("单播推送调用");
        autoPushServiceImpl.pushFeedBack(token,title,content,itemType,appId);
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


}
