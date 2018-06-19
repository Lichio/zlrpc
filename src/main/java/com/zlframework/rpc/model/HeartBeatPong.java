package com.zlframework.rpc.model;

/**
 * zlrpc com.zlframework.rpc.model
 *
 * @author Lichaojie
 * @version 2018/4/23 21:31
 */
public class HeartBeatPong {
	private String msg;

	public HeartBeatPong(){
		msg = "pong";
	}

	public String getMsg(){
		return msg;
	}
}
