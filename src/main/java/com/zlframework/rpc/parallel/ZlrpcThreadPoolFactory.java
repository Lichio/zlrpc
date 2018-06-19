package com.zlframework.rpc.parallel;

import com.zlframework.rpc.core.SystemConfig;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * zlrpc com.zlframework.rpc.parallel
 *
 * @author Lichaojie
 * @version 2018/4/28 10:39
 */
public class ZlrpcThreadPoolFactory {

	public static ThreadPoolExecutor getExecutor(int threads,int queues){
		System.out.println("ThreadPool Core[threads:" + threads + ", queues:" + queues + "]");
		String name = "ZlThreadPool";
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
				BlockingQueueType.getBlockingQueue(SystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_TYPE,queues),
				new ZlrpcThreadFactory(name, true),
				RejectedPolicyType.getPolicy(SystemConfig.SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY));
		return executor;
	}
}
