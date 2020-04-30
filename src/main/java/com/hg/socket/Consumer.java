package com.hg.socket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.Session;

import com.hg.util.DateFormat;

/**
 * 用户
 * 
 * @author Administrator
 *
 */
public class Consumer {

	private String name;

	// 消息类型：1:普通String类型 2：图片类型 3：文件类型
	private String messageType;

	// 信息体
	private Object message;

	private Session session;

	/**
	 * 上线时间
	 */
	private String onlineTime;

	/**
	 * 累计时长
	 */
	private String durationTime;

	/**
	 * 客服sessionId
	 */
	private String customerSessionId;

	Consumer() {

	}

	public Consumer(Session session) {
		this.session = session;
	}

	public Consumer(Session session, String name) {
		this.session = session;
		this.name = name.equals("null") ? session.getId() : name;
	}

	public Session getSession() {
		return session;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.onlineTime = sf.format(date);
	}

	public String getDurationTime() {
		return this.durationTime;
	}

	public void setDurationTime(Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long c = 0;
		try {
			c = (date.getTime()) - sf.parse(onlineTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.durationTime = DateFormat.getHMS(c);
	}

	public void setCustomerSessionId(String sessionId) {
		this.customerSessionId = sessionId;
	}

	public String getCustomerSessionId() {
		return this.customerSessionId;
	}
}
