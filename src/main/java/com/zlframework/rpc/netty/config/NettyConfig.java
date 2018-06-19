package com.zlframework.rpc.netty.config;

import jdk.nashorn.internal.runtime.linker.Bootstrap;

/**
 * zlrpc com.zlframework.rpc.netty.config
 *
 * @author Lichaojie
 * @version 2018/4/24 9:41
 */
public class NettyConfig {
	public static final boolean HEARTBEAT_STATE = Boolean.getBoolean("netty.heartbeat.state"); //0-��ʹ��������1-ʹ������
	public static final int RECONNECT_ATTEMPTS = Integer.getInteger("netty.client.reconnect_attempts",10); //������������
	public static final boolean RECONNECT_STATE = Boolean.getBoolean("netty.client.reconnect_state"); //�Ƿ���ж�������
	public static final int IO_THREAD_NUMBER = Integer.getInteger("netty.server.io.thread.number",15);


	private static int serviceTimeout = 5000;

	public static void setTimeout(int timeout){
		serviceTimeout = timeout;
	}

	public static int getTimeout(){
		return serviceTimeout;
	}
}
