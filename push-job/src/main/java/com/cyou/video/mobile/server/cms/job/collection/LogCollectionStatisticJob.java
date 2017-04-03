//package com.cyou.video.mobile.server.cms.job.collection;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.SchedulerException;
//import org.quartz.StatefulJob;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//import org.springframework.stereotype.Component;
//import org.springframework.ui.ModelMap;
//
//import com.cyou.video.mobile.server.cms.service.collection.MultiThreadExcuteXinGeService;
//
///**
// * 数据收集后进行统计job
// * 
// * @author lusi
// */
//@Component("logCollectionStatisticJob")
//public class LogCollectionStatisticJob extends QuartzJobBean implements
//		StatefulJob {
//
//	@Autowired
//  MultiThreadExcuteXinGeService multiThreadExcuteXinGeService;
//	
//	private Logger LOGGER = LoggerFactory.getLogger(LogCollectionStatisticJob.class);
//
//	@Override
//	protected void executeInternal(JobExecutionContext context)
//			throws JobExecutionException {
//		LOGGER.info("updateLogInfo job start ==================");
//		Map<String, Object> params = new HashMap<String, Object>();
//		ModelMap model = new ModelMap();
//		try {
//		  multiThreadExcuteXinGeService = (MultiThreadExcuteXinGeService) context
//					.getScheduler().getContext()
//					.get("multiThreadExcuteXinGeService");
////		  multiThreadExcuteXinGeService.syncApp();
//		  multiThreadExcuteXinGeService.sendPushTags(params, model);
////		  multiThreadExcuteXinGeService.sendBestWalkThrough(params, model);
//		} catch (SchedulerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			LOGGER.error("updateLogInfo job exception ================== is "
//					+ e.getMessage());
//		}
//
//	}
//}
//
