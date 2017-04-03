package com.cyou.video.mobile.server.cms.service.sys.impl;

import java.util.List;
import java.util.Map;

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
import com.cyou.video.mobile.server.cms.model.collection.SystemConfig;
import com.cyou.video.mobile.server.cms.service.push.impl.DataNotFoundException;
import com.cyou.video.mobile.server.cms.service.sys.SystemConfigService;

@Service("systemConfigService")
public class SystemConfigServiceImpl implements SystemConfigService{


  @Autowired
  private MongoOperations mongoTemplate;


  @Override
  public Pagination list(Map<String, Object> params) throws Exception {
    Pagination pagination = new Pagination();
    int curPage = Integer.parseInt(params.get("start").toString());
    int pageSize = Integer.parseInt(params.get("limit").toString());
    params.put("curPage", curPage);
    params.put("pageSize", pageSize);
    Query q = new Query();
    q.with(new Sort(Direction.ASC, "index"));
    pagination.setRowCount((int) mongoTemplate.count(new Query(), SystemConfig.class));
    List<SystemConfig> systemConfigs = mongoTemplate.find(q, SystemConfig.class);
    pagination.setContent(systemConfigs);
    return pagination;
  }
  
  @Override
  @Cacheable(value = "videoMobileCMSCache", key = "'PUSH_SYS_PROPERTY_'+#configKey")
  public String getByKey(String configKey) throws DataNotFoundException {
      SystemConfig systemConfig = mongoTemplate.findOne(new Query().addCriteria(new Criteria("key").is(configKey)), SystemConfig.class);
      if(systemConfig != null){
        return systemConfig.getValue();
      }
      throw new DataNotFoundException("没有找到"+configKey+"对应的系统配置信息");
  }

  @Override
  @CacheEvict(value = "videoMobileCMSCache", key = "'PUSH_SYS_PROPERTY_'+#systemConfig.key")
  public void save(SystemConfig systemConfig) {
     mongoTemplate.save(systemConfig);
  }

  @Override
  @CacheEvict(value = "videoMobileCMSCache", key = "'PUSH_SYS_PROPERTY_'+#systemConfig.key")
  public void delete(SystemConfig systemConfig) {
     mongoTemplate.remove(systemConfig);
  }
}
