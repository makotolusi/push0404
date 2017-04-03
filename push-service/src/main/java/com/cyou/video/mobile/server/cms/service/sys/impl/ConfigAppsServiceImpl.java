package com.cyou.video.mobile.server.cms.service.sys.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.cyou.video.mobile.server.cms.model.Pagination;
import com.cyou.video.mobile.server.cms.model.sys.ConfigApps;
import com.cyou.video.mobile.server.cms.model.sys.ContentType;
import com.cyou.video.mobile.server.cms.service.common.RedisTemplate;
import com.cyou.video.mobile.server.cms.service.sys.ConfigAppsService;
import com.cyou.video.mobile.server.cms.service.sys.ContentTypeService;

/**
 * 推送类型维护
 * 
 * @author zs
 */
@Service("configAppsService")
public class ConfigAppsServiceImpl implements ConfigAppsService {

  @Autowired
  private MongoOperations mongoTemplate;
  
  @Resource(name = "redisTemplate")
  private RedisTemplate redisTemplate;

  @Override
  public Pagination listContentType(Map<String, Object> params) throws Exception {
    Pagination pagination = new Pagination();
    int curPage = Integer.parseInt(params.get("start").toString());
    int pageSize = Integer.parseInt(params.get("limit").toString());
    params.put("curPage", curPage);
    params.put("pageSize", pageSize);
    Query q=new Query();
    q.with(new Sort(Direction.ASC, "index"));
    pagination.setRowCount((int) mongoTemplate.count(new Query(), ContentType.class));
    List<ConfigApps> conTypes = mongoTemplate.find(q,ConfigApps.class);
    pagination.setContent(conTypes);
    return pagination;
  }

  @Override
  @Cacheable(value = "videoMobileCMSCache", key = "'PUSH_CONFIG_APPS_'+#id")
  public ConfigApps findById(String id) throws Exception {
    return mongoTemplate.findOne(new Query(new Criteria("_id").is(new ObjectId(id))),ConfigApps.class);
  }
  
  @Override
  public ConfigApps findConfigIdByAppid(Integer appId) throws Exception {
    Query q=new Query(new Criteria("appIds").is(appId));
    q.fields().include("_id");
    return mongoTemplate.findOne(q,ConfigApps.class);
  }
  
  @Override
  @CacheEvict(value = "videoMobileCMSCache", key = "'PUSH_CONFIG_APPS_'+#configApps.id")
  public void createContentType(ConfigApps configApps) throws Exception {
    mongoTemplate.save(configApps);
  }
  @Override
  @CacheEvict(value = "videoMobileCMSCache", key = "'PUSH_CONFIG_APPS_'+#configApps.id")
  public void deleteContentType(ConfigApps configApps) throws Exception {
    mongoTemplate.remove(configApps);
  }
  @Override
  @CacheEvict(value = "videoMobileCMSCache", key = "'PUSH_CONFIG_APPS_'+#configApps.id")
  public void updateContentType(ConfigApps configApps) throws Exception {
    mongoTemplate.save(configApps);
  }

}
