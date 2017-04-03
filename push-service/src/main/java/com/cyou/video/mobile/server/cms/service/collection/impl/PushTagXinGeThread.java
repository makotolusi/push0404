package com.cyou.video.mobile.server.cms.service.collection.impl;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyou.video.mobile.server.cms.common.Consts.COLLECTION_PUSH_TAG_JOB_NAME;
import com.cyou.video.mobile.server.cms.service.push.PushTagXinGe173APPService;

public class PushTagXinGeThread extends PThread {
  private Logger logger = LoggerFactory.getLogger(PushTagXinGeThread.class);

  PushTagXinGe173APPService pushTagXinGe173APPService;

  Map<String, Object> params;

  private CountDownLatch latch;
  
  public PushTagXinGeThread(PushTagXinGe173APPService pushTagXinGe173APPService, Map<String, Object> params,CountDownLatch latch) {
    super();
    this.pushTagXinGe173APPService = pushTagXinGe173APPService;
    this.params = params;
    this.latch=latch;
  }

  @Override
  public void run() {
    try {
      getThreadService();
    }
    catch(Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally{
      latch.countDown();
    }
  }

  private void getThreadService() throws Exception {
    String threadName = String.valueOf(Thread.currentThread().getName());
    logger.info(" cur threadName  " + threadName+" start "+start+" "+end);
    int total = 0;
    PThread pThread = null;
    params.put("start", start);
    params.put("end", end);
    switch((COLLECTION_PUSH_TAG_JOB_NAME) params.get("jobType")) {
//      case WALKTHROUGH_APP_GAME_TAG :
//        total = pushTagXinGe173APPService.sendBestWalkThroughInstalledGameTags(params,(int)start,(int)end);
//        break;
      case USER_REDUCE_TAG :
        total = pushTagXinGe173APPService.sendPushTags(params,(int)start,(int)end);
        break;
      case TEMP :
        total = pushTagXinGe173APPService.sendPushTagsOld(params,(int)start,(int)end);
        break;
      default :
        break;
    }
    logger.info(" this tread "+threadName+" end =========================================================================");
    synchronized(pushTagXinGe173APPService) {
      pushTagXinGe173APPService.successLogEnd(name,params.get("appId").toString(), total);
    }
  }
}
