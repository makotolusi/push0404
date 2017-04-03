package com.cyou.video.mobile.server.cms.service.push.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.cyou.video.mobile.server.cms.common.Consts.PUSH_PLATFORM_TYPE;
import com.cyou.video.mobile.server.cms.model.Pagination;
import com.cyou.video.mobile.server.cms.model.push.PushApp;
import com.cyou.video.mobile.server.cms.model.security.Operation;
import com.cyou.video.mobile.server.cms.service.push.AppSelectService;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;
import com.cyou.video.mobile.server.common.utils.HttpUtil;

/**
 * 意见反馈业务实现
 * 
 * @author jyz
 */
@Service("appSelectService")
public class AppSelectServiceImpl implements AppSelectService {

  private Logger logger = LoggerFactory.getLogger(AppSelectServiceImpl.class);

  @Autowired
  private MongoOperations mongoTemplate;

  @Autowired
  private SystemConfigService systemConfigService;

  @Override
  public Pagination listApp(Map<String, Object> params) {
    Query query = new Query();
    if(params.get("appIds")!=null){
      query.addCriteria(Criteria.where("appId").in((List<Integer>)params.get("appIds")));
    }
    if(!StringUtils.isEmpty((String)params.get("name"))) {
      Pattern pattern = Pattern.compile("^.*" + params.get("name").toString() + ".*$");
      query.addCriteria(Criteria.where("name").regex(pattern));
    }
    Pagination pagination = new Pagination();
    int curPage = Integer.parseInt(params.get("start").toString());
    int pageSize =Integer.parseInt(params.get("limit").toString());
    query.limit(pageSize);
    query.skip(curPage);
    query.with(new Sort(Sort.Direction.DESC, "appId"));
    List<PushApp> list = mongoTemplate.find(query, PushApp.class);
    pagination.setRowCount((int) mongoTemplate.count(query, PushApp.class));
    pagination.setContent(list);
    return pagination;
  }

  @Override
  public void syncApp() {
    try {
      mongoTemplate.dropCollection(PushApp.class);
      List<PushApp> apps = new ArrayList<PushApp>();
      Map<String, String> p = new HashMap<String, String>();
      String str = HttpUtil.syncGet(systemConfigService.getByKey("appList"), p, null, null);
      if(!StringUtils.isEmpty(str)) {
        JSONArray array = new JSONObject(str).getJSONArray("data");
        for(int i = 0; i < array.length(); i++) {
          PushApp app = new PushApp();
          JSONObject obj = array.getJSONObject(i);
//          if(obj.getString("status") == null||"checking".equals(obj.getString("status") )){
//            continue;
//          }
          if(obj.has("appName"))
            app.setName(obj.getString("appName") == null ? null : obj.getString("appName").toString());
          if(obj.has("pushWay"))
          {
            if(obj.getInt("pushWay")==1)
              app.setPushPlatForm(PUSH_PLATFORM_TYPE.BAIDU);
            else
              app.setPushPlatForm(PUSH_PLATFORM_TYPE.XINGE);
          }
          if(obj.has("apiKey"))
            app.setAppKey(obj.getString("apiKey") == null ? null : obj.getString("apiKey").toString());
          if(obj.has("secritKey"))
            app.setSecretKey(obj.getString("secritKey") == null ? null : obj.getString("secritKey").toString());
          if(obj.has("iosApiKey"))
            app.setAppKey_ios(obj.getString("iosApiKey") == null ? null : obj.getString("iosApiKey").toString());
          if(obj.has("iosSecritKey"))
            app.setSecretKey_ios(obj.getString("iosSecritKey") == null ? null : obj.getString("iosSecritKey").toString());
          if(obj.has("appId"))
            app.setAppId(obj.getInt("appId"));
          apps.add(app);
        }
        mongoTemplate.insertAll(apps);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      logger.error("sync failed!!!");
    }
  }
  
  @Override
  public PushApp getAppById(Integer id) {
    Query query = new Query();
    query.addCriteria(new Criteria("appId").is(id));
    return mongoTemplate.findOne(query, PushApp.class);
  }
  
  @Override
  public List<PushApp> listAppOfRole(List<Integer> appIds, int out) throws Exception {
    Query q = null;
    if(out == 1)
      q = new Query(new Criteria("appId").nin(appIds));
    else if(out == 0) {
      if(appIds==null||appIds.isEmpty())
        return new ArrayList<PushApp>();
      else
        q = new Query(new Criteria("appId").in(appIds));
    }
    else if(out == 3) q = new Query();
    return mongoTemplate.find(q, PushApp.class);
  }

  @Override
  public List<PushApp> listAll() throws Exception {
    return mongoTemplate.findAll(PushApp.class);
  }

}
