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
	//�洢���ؽ������������
	private BlockingQueue<RespMessage> responseQueue = new ArrayBlockingQueue<RespMessage>(1);
	//�������ʱ��
	private long responseTime;

	/**
	 * ����÷��ؽ���Ƿ��Ѿ�����
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
