package com.cyou.video.mobile.server.cms.service.push;

import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;

public interface PushTagXinGe173APPService {

  /**
   * 173app发送标签
   */
  int sendPushTags(Map<String, Object> params,int start,int end);

  /**
   * 每个线程结束时记录日志
   * 
   * @param c
   * @param total
   */
  void successLogEnd(String c, String appId,int total);

  /**
   * 最强攻略
   * @param s
   * @param end
   * @param query
   * @param name
   * @return
   * @throws Exception
   */
//  int sendBestWalkThroughInstalledGameTags(Map<String, Object> params,int start,int end) throws Exception;

  int sendPushTagsOld(Map<String, Object> params,int start,int end);

}
