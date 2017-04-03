package com.cyou.video.mobile.server.cms.service.push;

import java.util.List;

import org.json.JSONObject;

import com.cyou.video.mobile.server.cms.model.push.Push;
import com.tencent.xinge.TagTokenPair;

public interface PushInterface {
  public Push pushAll(Push push);

  public Push pushTag(Push push) throws Exception;

  public void setTag(String uid, String tag) throws Exception;

  public void deleteTag(String uid, String tag) throws Exception;

  public void deleteUserTag(String uid) throws Exception;

  // public List<TagInfo> queryUserTag(String uid) throws
  // ChannelClientException, ChannelServerException;

  public Push pushOne(Push push) throws Exception;

  /**
   * xinge set tag
   * 
   * @param pairs
   * @param cType
   * @throws Exception
   */
  void setTagByXinge(List<TagTokenPair> pairs, Push p);

  /**
   * 信鸽批量删除
   * 
   * @param pairs
   * @param cType
   * @throws Exception
   */
  void delTagByXinge(List<TagTokenPair> pairs, Push p);

  /**
   * 查询tag
   * @param token
   * @param push
   * @return
   * @throws Exception
   */
  JSONObject queryUserTag(String token, Push push) throws Exception;
}
