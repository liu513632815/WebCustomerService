package com.hg.vo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hg.socket.Consumer;
import com.hg.socket.Customer;
import com.hg.socket.Manager;
import com.hg.socket.Setting;
import com.hg.util.DateFormat;

/**
 * 监控页信息对应的实体类
 */
public class IndexPageInfo {

	/**
	 * 系统启动时间
	 */
	private String starupTime;

	/**
	 * 系统运行时长
	 */
	private String survivalTime;

	/**
	 * 累计在线客服
	 */
	private Integer totalCustomer;

	/**
	 * 累计在线客户
	 */
	private Integer totalConsumer;

	/**
	 * 在线客服数
	 */
	private Integer onlineCustomer;

	/**
	 * 在线客户数
	 */
	private Integer onlineConsumer;

	private Boolean isAutoReply;

	private Boolean isAdReply;

	private List<Consumer> consumerList;
	private List<Customer> customerList;

	public IndexPageInfo() {
		this.starupTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Manager.starupTime);
		this.survivalTime = getDurationTime(new Date());
		this.totalConsumer = Manager.getTotalConsumerNum();
		this.totalCustomer = Manager.getTotalCustomerNum();
		this.onlineConsumer = Manager.getOnlineConsumerNum();
		this.onlineCustomer = Manager.getOnlineCustomerNum();
		this.isAutoReply = Setting.isAutoReply;
		this.isAdReply = Setting.isAdReply;
	}

	public String getStarupTime() {
		return starupTime;
	}

	public void setStarupTime(String starupTime) {
		this.starupTime = starupTime;
	}

	public String getSurvivalTime() {
		return survivalTime;
	}

	public void setSurvivalTime(String survivalTime) {
		this.survivalTime = survivalTime;
	}

	public Integer getTotalCustomer() {
		return totalCustomer;
	}

	public void setTotalCustomer(Integer totalCustomer) {
		this.totalCustomer = totalCustomer;
	}

	public Integer getTotalConsumer() {
		return totalConsumer;
	}

	public void setTotalConsumer(Integer totalConsumer) {
		this.totalConsumer = totalConsumer;
	}

	public Integer getOnlineCustomer() {
		return onlineCustomer;
	}

	public void setOnlineCustomer(Integer onlineCustomer) {
		this.onlineCustomer = onlineCustomer;
	}

	public Integer getOnlineConsumer() {
		return onlineConsumer;
	}

	public void setOnlineConsumer(Integer onlineConsumer) {
		this.onlineConsumer = onlineConsumer;
	}

	public List<Consumer> getConsumerList() {
		return consumerList;
	}

	public void setConsumerList(List<Consumer> consumerList) {
		this.consumerList = consumerList;
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}

	private String getDurationTime(Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long c = 0;
		try {
			c = (date.getTime()) - sf.parse(this.starupTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormat.getHMS(c);
	}

	public Boolean getIsAutoReply() {
		return isAutoReply;
	}

	public void setIsAutoReply(Boolean isAutoReply) {
		this.isAutoReply = isAutoReply;
	}

	public Boolean getIsAdReply() {
		return isAdReply;
	}

	public void setIsAdReply(Boolean isAdReply) {
		this.isAdReply = isAdReply;
	}

}
