package com.zlframework.rpc.parallel;

import com.zlframework.rpc.core.SystemConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * zlrpc com.zlframework.rpc.parallel
 *
 * @author Lichaojie
 * @version 2018/4/28 11:28
 */
public enum BlockingQueueType {
	LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"),
	ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"),
	SYNCHRONOUS_QUEUE("SynchronousQueue");

	private String value;

	private BlockingQueueType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static BlockingQueueType fromString(String value) {
		for (BlockingQueueType type : BlockingQueueType.values()) {
			if (type.getValue().equalsIgnoreCase(value.trim())) {
				return type;
			}
		}

		throw new IllegalArgumentException("Mismatched type with value=" + value);
	}

	public static BlockingQueue<Runnable> getBlockingQueue(String queue,int queues){
		switch (fromString(System.getProperty(queue,"LinkedBlockingQueue"))) {
			case ARRAY_BLOCKING_QUEUE:
				return new ArrayBlockingQueue<Runnable>(SystemConfig.SYSTEM_PROPERTY_PARALLEL * queues);
			case SYNCHRONOUS_QUEUE:
				return new SynchronousQueue<Runnable>();
			case LINKED_BLOCKING_QUEUE:
			default:
				return new LinkedBlockingQueue<Runnable>(queues);

		}
	}

	@Override
	public String toString() {
		return value;
	}
}
