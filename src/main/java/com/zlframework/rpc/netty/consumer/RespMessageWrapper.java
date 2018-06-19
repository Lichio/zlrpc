package com.zlframework.rpc.netty.consumer;

import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.netty.config.NettyConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * zlrpc com.zlframework.rpc.model
 *
 * @author Lichaojie
 * @version 2018/4/28 17:28
 */
public class RespMessageWrapper {
	//存储返回结果的阻塞队列
	private BlockingQueue<RespMessage> responseQueue = new ArrayBlockingQueue<RespMessage>(1);
	//结果返回时间
	private long responseTime;

	/**
	 * 计算该返回结果是否已经过期
	 *
	 * @return
	 */
	public boolean isExpire() {
		RespMessage response = responseQueue.peek();
		if (response == null) {
			return false;
		}

		long timeout = NettyConfig.getTimeout();
		if ((System.currentTimeMillis() - responseTime) > timeout) {
			return true;
		}
		return false;
	}

	public static RespMessageWrapper of() {
		return new RespMessageWrapper();
	}

	public BlockingQueue<RespMessage> getResponseQueue() {
		return responseQueue;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
}
