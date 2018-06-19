package com.zlframework.rpc.model;

import java.io.Serializable;

/**
 * zlrpc com.zlframework.rpc.model
 *
 * @author Lichaojie
 * @version 2018/4/23 21:37
 */
public class ReqMessage implements Serializable{
	private String messageId;
	private String className;
	private String methodName;
	private Class<?>[] parametersType;
	private Object[] parametersVal;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParametersType() {
		return parametersType;
	}

	public void setParametersType(Class<?>[] parametersType) {
		this.parametersType = parametersType;
	}

	public Object[] getParametersVal() {
		return parametersVal;
	}

	public void setParametersVal(Object[] parametersVal) {
		this.parametersVal = parametersVal;
	}
}
