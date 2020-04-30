package com.hg.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@ServerEndpoint(value = "/websocket/{userType}/{userName}")
@Component
public class SocketServer extends Manager {

	private static final String CUSTOMER = "customer";
	private static final String CONSUMER = "consumer";

	@OnOpen
	public void onOpen(Session session, @PathParam("userType") String userType, @PathParam("userName") String userName)
			throws Exception {
		register(session, userType, userName);
	}

	@OnClose
	public void onClose(Session session) {
		remove(session.getId());
	}

	@OnMessage
	@SuppressWarnings("unchecked")
	public void onMessage(String mapString, Session session, @PathParam("userType") String userType) throws Exception {
		if (mapString != "" && mapString != null) {
			if (CUSTOMER.equals(userType)) {// 客服发送消息
				customerSendMsg(new Gson().fromJson(mapString, HashMap.class));
			} else {// 客户发送消息
				consumerSendMsg(session, mapString);
			}
		}
	}

	private void customerSendMsg(HashMap<String, String> map) throws Exception {
		sendMessage(getConsumerBySessionId(map.get("sessionId")).getSession(), map.get("message"));
	}

	private void consumerSendMsg(Session session, String message) throws Exception {
		Customer c = ccmap.get(getConsumerBySessionId(session.getId()));
		if (c == null) {// 客户发送消息时，客服下线
			if (!tryAllocationCustomer(session, message))
				allocationRobot(session);
		} else {
			sendMessageWithSessionId(session, c.getSession(), message);
		}
	}

	/**
	 * 尝试重新分配客服
	 * 
	 * @param session
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private Boolean tryAllocationCustomer(Session session, String message) throws Exception {
		Boolean rs = true;
		Customer customer = takeCustomer();
		if (customer != null) {
			ccmap.put(getConsumerBySessionId(session.getId()), customer);
			sendMessageWithSessionId(session, customer.getSession(), message);
		} else {
			rs = false;
		}
		return rs;
	}

	/**
	 * 注册
	 * 
	 * @param session
	 * @param userType
	 * @throws Exception
	 */
	private void register(Session session, String userType, String userName) throws Exception {
		if (CUSTOMER.equals(userType)) {
			add(new Customer(session, userName));
		} else {
			add(new Consumer(session, userName));
			sendAd(session);
			allocation(session);
		}
	}

	/**
	 * 给客户分配客服
	 * 
	 * @param session
	 * @throws Exception
	 */
	private void allocation(Session session) throws Exception {
		Customer customer = takeCustomer();
		Consumer consumer = getConsumerBySessionId(session.getId());
		if (customer == null) {// 给客户分配客服时，无客服在线
			allocationRobot(session);
		} else {
			ccmap.put(consumer, customer);
			consumer.setCustomerSessionId(customer.getSession().getId());
			customer.addOne();// 成功分配，客服对接客户数加一
		}
	}

	/**
	 * 回复自动内容
	 * 
	 * @param session
	 * @throws IOException
	 */
	private void allocationRobot(Session session) throws IOException {
		if (Setting.isAutoReply) {
			session.getBasicRemote().sendText(new Gson().toJson(new Message("0", session.getId(), Setting.autoReply)));
		}
	}

	/**
	 * 回复广告
	 * 
	 * @param session
	 * @throws IOException
	 */
	private void sendAd(Session session) throws IOException {
		if (Setting.isAdReply) {
			session.getBasicRemote().sendText(new Gson().toJson(new Message("3", session.getId(), Setting.adReply)));
		}
	}

	/**
	 * 客户向客服发送消息
	 * 
	 * @param send_session：客户session
	 * @param session：客服session
	 * @param message：信息
	 * @throws Exception
	 */
	private void sendMessageWithSessionId(Session send_session, Session session, String message) throws Exception {
		session.getBasicRemote().sendText(new Gson().toJson(new Message("1", send_session.getId(), message)));
	}

	/**
	 * 客服向客户发送消息
	 * 
	 * @param session：客户session
	 * @param message：信息
	 * @throws IOException
	 */
	private void sendMessage(Session session, String message) throws IOException {
		session.getBasicRemote().sendText(new Gson().toJson(new Message("1", session.getId(), message)));
	}

}
