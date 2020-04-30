package com.hg.socket;

public class Message {

	/**
	 * 0：自动答复 1：普通消息 2：下线通知消息 3：广告消息
	 */
	private String messageType;
	private String sessionId;
	private String message;

	public Message() {

	}

	public Message(String messageType) {
		this.messageType = messageType;
	}

	public Message(String messageType, String sessionId, String message) {
		this.messageType = messageType;
		this.sessionId = sessionId;
		this.message = message;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
