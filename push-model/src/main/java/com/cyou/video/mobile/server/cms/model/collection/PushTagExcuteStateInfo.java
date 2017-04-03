package com.cyou.video.mobile.server.cms.model.collection;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cyou.video.mobile.server.cms.common.Consts.PUSH_SEND_TAG_STATE;
import com.cyou.video.mobile.server.common.adapter.JaxbDatetimeAdapter;

@Document(collection = "PushTagExcuteStateInfo")
public class PushTagExcuteStateInfo{

	@Id
	private String id; // ID

	private String name; // 统计job的名字
	
	private String threadName; // 统计job的名字
	
	private Date start; // 该任务最后更新时间

	private Date end; // 该任务最后更新时间

	private long excuteTime;

	private int threadNum; // 该任务最后更新

	private int finishThreadNum; // 结束线程数

	private Date lastUpdate = new Date();

	private String exceptionMsg;

	private String description;// 描述

	private long size;// 描述

	private PUSH_SEND_TAG_STATE state;

	public PushTagExcuteStateInfo(String name, Date start, int threadNum) {
		super();
		this.name = name;
		this.start = start;
		this.threadNum = threadNum;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	
	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	@XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getExcuteTime() {
		if (end != null && start != null)
			excuteTime = (end.getTime() - start.getTime()) / 1000;
		return excuteTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setExcuteTime(long excuteTime) {
		this.excuteTime = excuteTime;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public PUSH_SEND_TAG_STATE getState() {
		if (finishThreadNum >= threadNum)
			state = PUSH_SEND_TAG_STATE.WAITING;
		return state;
	}

	public void setState(PUSH_SEND_TAG_STATE state) {
		this.state = state;
	}

	public int getFinishThreadNum() {
		return finishThreadNum;
	}

	public void setFinishThreadNum(int finishThreadNum) {
		this.finishThreadNum = finishThreadNum;
	}

	


}
