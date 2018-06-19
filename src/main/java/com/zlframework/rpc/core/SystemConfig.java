package com.zlframework.rpc.core;

/**
 * zlrpc com.zlframework.rpc.core
 *
 * @author Lichaojie
 * @version 2018/4/28 10:53
 */
public class SystemConfig {

	public static final String SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY = "netty.parallel.policy";
	public static final String SYSTEM_PROPERTY_THREADPOOL_QUEUE_TYPE  = "netty.parallel.queue";

	public static final int SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS = Integer.getInteger("zlrpc.netty.parallel.threads",16);
	public static final int SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS = Integer.getInteger("zlrpc.netty.parallel.queues",100);

	public static final int SYSTEM_PROPERTY_PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());

}
