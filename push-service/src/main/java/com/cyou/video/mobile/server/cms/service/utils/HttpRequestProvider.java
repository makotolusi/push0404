package com.cyou.video.mobile.server.cms.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cyou.video.mobile.server.cms.model.user.UserToken;
import com.cyou.video.mobile.server.cms.service.push.impl.DataNotFoundException;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.common.utils.HttpUtil;

@Component("httpRequestProvider")
public class HttpRequestProvider {

  @Autowired
  private SystemConfigService systemConfigService;

  public UserToken getToken(String token) throws DataNotFoundException {
    Map<String, String> params = new HashMap<String, String>();
    String str = HttpUtils.post(systemConfigService.getByKey("cms_token_uri") + "?token=" + token, params);
    if(StringUtils.isEmpty(str)) return null;
    JSONObject obj = new JSONObject(str).getJSONObject("token");
    UserToken t = new UserToken();
    t.setId(obj.getInt("id"));
    t.setPlat(obj.getInt("plat"));
    t.setToken(obj.getString("token"));
    t.setChannel(obj.getString("channel"));
    t.setCurUsedVersion(obj.getString("curUsedVersion"));
    return t;
  }

  public boolean isForbidenGameCode(String gameCode) {
    try {
      Map<String, String> params = new HashMap<String, String>();
      // String
      // s=HttpUtil.syncGet("http://10.6.196.178:8080/cms/rest/ops/gameBusiness/recommend?platCode=99",
      // params, "UTF-8", "UTF-8");
      String str = HttpUtils.get(systemConfigService.getByKey("forbiden_gamecode_uri")
          + "/rest/ops/gameBusiness/recommend?platCode=99", params);// systemConfigService.getByKey("gamecode_forbiden_url"),params);
      if(StringUtils.isEmpty(str)) return false;
      JSONArray arr = new JSONObject(str).getJSONArray("result");
      List<String> gameCodes = new ArrayList<String>();
      for(int i = 0; i < arr.length(); i++) {
        gameCodes.add(arr.get(i).toString());
      }
      if(gameCodes.contains(gameCode))
        return true;
      else
        return false;
    }
    catch(Exception e) {
     return false;
    }
  }
}
