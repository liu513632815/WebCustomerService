package com.hg.socket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.Session;

import com.hg.util.DateFormat;

/**
 * 客服
 * 
 * @author Administrator
 *
 */
public class Customer {

	private String name;

	private Session session;

	/**
	 * 上线时间
	 */
	private String onlineTime;

	/**
	 * 累计时长
	 */
	private String durationTime;

	private Integer int_totalNumber;

	/**
	 * 总对接客户数
	 */
	private AtomicInteger totalNumber = new AtomicInteger(0);

	private Integer int_currentNumber;
	/**
	 * 当前正在对接的
	 */
	private AtomicInteger currentNumber = new AtomicInteger(0);

	Customer() {

	}

	Customer(Session session) {
		this.session = session;
	}

	Customer(Session session, String name) {
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

	/**
	 * 新分配一个上线客户
	 */
	public void addOne() {
		totalNumber.incrementAndGet();
		currentNumber.incrementAndGet();
	}

	/**
	 * 下线一个客户
	 */
	public void delOne() {
		currentNumber.decrementAndGet();
	}

	public Integer getInt_totalNumber() {
		return totalNumber.get();
	}

	public Integer getInt_currentNumber() {
		return currentNumber.get();
	}

}
