package com.cyou.video.mobile.server.cms.service.push;

import java.util.List;

import com.cyou.video.mobile.server.cms.common.Consts.CLIENT_TYPE;
import com.cyou.video.mobile.server.cms.model.push.PushAuto;

public interface AutoPushService {
  /**
   * 攻略自动推送
   * 
   * @param gameCode
   * @param id
   *          推送内容id
   * @param title
   *          推送内容标题
   * @param COLLECTION_ITEM_TYPE
   *          推送内容类型
   * @return
   */
  public boolean autoPush(String tag, String id, String title, String anchor, String itemType, CLIENT_TYPE ct,Integer appId);

  /**
   * 意见反馈
   * 
   * @param token
   * @param title
   * @param content
   * @return
   */
  public boolean pushFeedBack(String token, String title, String content, String type, String appId) throws Exception;

  /**
   * 自动推送列表
   * 
   * @return
   */
  public List<PushAuto> listAutoPush();
}
