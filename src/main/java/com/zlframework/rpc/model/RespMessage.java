package com.zlframework.rpc.model;

import java.io.Serializable;

/**
 * zlrpc com.zlframework.rpc.model
 *
 * @author Lichaojie
 * @version 2018/4/23 21:38
 */
public class RespMessage implements Serializable{

	private String messageId;
	private String error;
	private Object result;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
