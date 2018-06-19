package com.zlframework.rpc.registry.zookeeper.config;

import java.util.Stack;

/**
 * zlrpc com.zlframework.rpc.registry.zookeeper.config
 *
 * @author Lichaojie
 * @version 2018/4/26 18:25
 */
public class ZkConfigHelper {

	public static final String APP_KEY = "appkey";
	public static final String GROUP = "zlrpc";
	//ZK服务地址
	private static String zkAddress = "";
	//ZK session超时时间
	private static int zkSessionTimeout;
	//ZK connection超时时间
	private static int zkConnectionTimeout = 500;

	public static String getZkAddress() {
		return zkAddress;
	}

	public static void setZkAddress(String zkAddress) {
		ZkConfigHelper.zkAddress = zkAddress;
	}

	public static int getZkSessionTimeout() {
		return zkSessionTimeout;
	}

	public static void setZkSessionTimeout(int zkSessionTimeout) {
		ZkConfigHelper.zkSessionTimeout = zkSessionTimeout;
	}

	public static int getZkConnectionTimeout() {
		return zkConnectionTimeout;
	}

	public static void setZkConnectionTimeout(int zkConnectionTimeout) {
		ZkConfigHelper.zkConnectionTimeout = zkConnectionTimeout;
	}
}
