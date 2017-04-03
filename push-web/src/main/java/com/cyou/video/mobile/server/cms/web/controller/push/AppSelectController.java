package com.cyou.video.mobile.server.cms.web.controller.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.model.security.Manager;
import com.cyou.video.mobile.server.cms.model.security.Role;
import com.cyou.video.mobile.server.cms.service.push.AppSelectService;
import com.cyou.video.mobile.server.cms.service.security.RoleService;
import com.cyou.video.mobile.server.common.Constants;

/**
 * 
 * 
 * @author LUSI
 */
@Controller
@RequestMapping("/web/app")
public class AppSelectController {

  private Logger logger = LoggerFactory.getLogger(AppSelectController.class);

  @Autowired
  private RoleService roleService;

  @Autowired
  private AppSelectService appSelectService;

  @RequestMapping(value = "/list", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listApp(@RequestBody
  Map<String, Object> params, ModelMap model, HttpServletRequest request) {
    try {
      Manager manager = (Manager) request.getSession().getAttribute(Consts.SESSION_MANAGER);
      if(manager == null) {
        model.put("message", "您尚未登录系统，请重新登录！");
      }
      else {
        List<Role> roles= manager.getRoles();
        List<Integer> appIds=new ArrayList<Integer>();
        for(Iterator iterator = roles.iterator(); iterator.hasNext();) {
          Role role = (Role) iterator.next();
          if(role.getAppIds()!=null&&!role.getAppIds().isEmpty())
            appIds.addAll(role.getAppIds());
        }
//        params.put("appIds", appIds);
        model.put("page", appSelectService.listApp(params));
        model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
      }
    }
    catch(Exception e) {
      logger.error("[method: addApp()]  : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "/curAppInfo", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap curAppInfo(ModelMap model, HttpServletRequest request) {
    try {
      Manager manager = (Manager) request.getSession().getAttribute(Consts.SESSION_MANAGER);
      if(manager == null) {
        model.put("message", "您尚未登录系统，请重新登录！");
      }
      else {
        model.put("data", appSelectService.getAppById(manager.getAppId()));
        model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
      }
    }
    catch(Exception e) {
      logger.error("[method: addApp()]  : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "/listAppOfRole", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listAppOfRole(@RequestParam
  String roleId, @RequestParam
  Integer out, ModelMap model, HttpServletRequest request) {
    try {
      Role roles = roleService.getRoleById(roleId);
      if(roles == null) {
        model.put("data", appSelectService.listAppOfRole(null, out));
      }
      else {
        model.put("data", appSelectService.listAppOfRole(roles.getAppIds(), out));
      }
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());

    }
    catch(Exception e) {
      logger.error("[method: addApp()]  : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }
}
