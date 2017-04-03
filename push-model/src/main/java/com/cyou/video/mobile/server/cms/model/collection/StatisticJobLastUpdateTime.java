package com.cyou.video.mobile.server.cms.model.collection;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.cyou.video.mobile.server.common.adapter.JaxbDatetimeAdapter;

@Document(collection = "StatisticJobLastUpdateTime")
public class StatisticJobLastUpdateTime  {

	@Id
	private String id; // ID

	private String statisicJobName; // 统计job的名字

	private Date lastUpdateTime; // 该任务最后更新时间

	public StatisticJobLastUpdateTime(String statisicJobName,
			Date lastUpdateTime) {
		super();
		this.statisicJobName = statisicJobName;
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatisicJobName() {
		return statisicJobName;
	}

	public void setStatisicJobName(String statisicJobName) {
		this.statisicJobName = statisicJobName;
	}

	@XmlJavaTypeAdapter(JaxbDatetimeAdapter.class)
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}



}
