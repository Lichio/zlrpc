package com.zlframework.rpc.model;

/**
 * zlrpc com.zlframework.rpc.model
 *
 * @author Lichaojie
 * @version 2018/4/23 21:27
 */
public class HeartBeatPing {
	private String msg;

	public HeartBeatPing(){
		msg = "ping";
	}

	public String getMsg() {
		return msg;
	}

}
