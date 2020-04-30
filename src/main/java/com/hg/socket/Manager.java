package com.hg.socket;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;

/**
 * 
 * @author Administrator
 *
 */
public class Manager {

	/**
	 * 系统启动时间
	 */
	public static Date starupTime = new Date();

	/**
	 * 累计客服数
	 */
	public static AtomicInteger totalCustomer = new AtomicInteger(0);

	/**
	 * 累计客户数
	 */
	public static AtomicInteger totalConsumer = new AtomicInteger(0);

	// 客服队列
	public static LinkedBlockingQueue<Customer> customerQuenen = new LinkedBlockingQueue<Customer>();

	// 用户消息队列
	public static LinkedBlockingQueue<Consumer> consumerQuenen = new LinkedBlockingQueue<Consumer>();

	// 客户-客服关系维护map
	public static ConcurrentHashMap<Consumer, Customer> ccmap = new ConcurrentHashMap<>();

	private static AtomicInteger concurrentIndex = new AtomicInteger(0);

	public static void add(Customer c) throws Exception {
		if (null == c) {
			throw new Exception("Customer:" + c + "must nut be null");
		}
		c.setOnlineTime(new Date());
		totalCustomer.incrementAndGet();
		customerQuenen.put(c);
	}

	public static void add(Consumer c) throws Exception {
		if (null == c) {
			throw new Exception("Consumer:" + c + "must nut be null");
		}
		c.setOnlineTime(new Date());
		totalConsumer.incrementAndGet();
		consumerQuenen.put(c);
	}

	// 下线时删除信息
	public void remove(String sessionId) {
		if (!removeConsumerQuenen(sessionId)) {
			removeCustomerQuenen(sessionId);
		}
	}

	private Boolean removeConsumerQuenen(String sessionId) {
		Consumer c = null;
		Boolean removeResult = false;
		Iterator<Consumer> iterator = consumerQuenen.iterator();
		while (iterator.hasNext()) {
			Consumer cu = (Consumer) iterator.next();
			if (sessionId.equals(cu.getSession().getId())) {
				c = cu;
				removeResult = true;
				break;
			}
		}
		if (null != c) {
			consumerQuenen.remove(c);
			ConsumerRemoveRelevance(c);
		}
		return removeResult;
	}

	private void removeCustomerQuenen(String sessionId) {
		Customer c = null;
		Iterator<Customer> iterator = customerQuenen.iterator();
		while (iterator.hasNext()) {
			c = (Customer) iterator.next();
			if (sessionId.equals(c.getSession().getId())) {
				break;
			}
		}
		if (null != c) {
			customerQuenen.remove(c);
			try {
				CustomerRemoveRelevance(c);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 客户下线,发送消息给客服并解除客户-客服映射关系
	// 存在一种情况：客户上线没有对接到客服，直接下线。此时没有对应关系不用解除
	private void ConsumerRemoveRelevance(Consumer c) {
		if (ccmap.get(c) != null) {
			sendOfflineMessage(ccmap.get(c), c);// 发送下线消息
			ccmap.get(c).delOne();// 当前对接客户数减一
			ccmap.remove(c);// 移除对应关系
		}
	}

	/**
	 * 客服下线，移除ccmap存在的对应关系,将新的客服分配到客户
	 * 
	 * @throws InterruptedException
	 */
	private void CustomerRemoveRelevance(Customer c) throws InterruptedException {
		Customer newCustomer = takeCustomer();
		ccmap.forEach((k, v) -> {
			if (v == c) {
				// ccmap.remove(k);
				k.setCustomerSessionId(newCustomer.getSession().getId());
				ccmap.replace(k, c, newCustomer);
			}
		});
	}

	// 客户下线发送下线消息给客服
	private void sendOfflineMessage(Customer customer, Consumer consumer) {
		try {
			customer.getSession().getBasicRemote()
					.sendText(new Gson().toJson(new Message("2", consumer.getSession().getId(), null)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Integer getOnlineNum() {
		return customerQuenen.size() + consumerQuenen.size();
	}

	public static Integer getOnlineCustomerNum() {
		return customerQuenen.size();
	}

	public static Integer getOnlineConsumerNum() {
		return consumerQuenen.size();
	}

	public static Integer getTotalConsumerNum() {
		return totalConsumer.get();
	}

	public static Integer getTotalCustomerNum() {
		return totalCustomer.get();
	}

	// 根据sessionId获取consumer
	public static Consumer getConsumerBySessionId(String sessionId) throws Exception {
		Iterator<Consumer> iterator = consumerQuenen.iterator();
		while (iterator.hasNext()) {
			Consumer c = iterator.next();
			if (sessionId.equals(c.getSession().getId())) {
				return c;
			}
		}
		return null;
	}

	// 从队列中轮流获取客服
	public static Customer takeCustomer() throws InterruptedException {
		if (customerQuenen.size() == 0) {
			return null;
		}
		if (!(concurrentIndex.get() < customerQuenen.size())) {
			concurrentIndex.set(0);
		}
		return (Customer) customerQuenen.toArray()[concurrentIndex.getAndIncrement()];
	}

}
