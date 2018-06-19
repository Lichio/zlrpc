package com.zlframework.rpc.registry.zookeeper.model;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * zlrpc com.zlframework.rpc.registry.zookeeper.model
 *
 * @author Lichaojie
 * @version 2018/4/26 17:56
 */
public class ConsumerInfo implements Serializable{
	private String service;
	private Method method;
	private String serialize;
	private String ip;
	private int port;
	private int timeout;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getSerialize() {
		return serialize;
	}

	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
