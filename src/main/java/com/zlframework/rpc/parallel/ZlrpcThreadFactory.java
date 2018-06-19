package com.zlframework.rpc.parallel;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * zlrpc com.zlframework.rpc.parallel
 *
 * @author Lichaojie
 * @version 2018/4/28 10:44
 *
 * 自定义线程工厂
 */
public class ZlrpcThreadFactory implements ThreadFactory {
	private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);

	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	private final String prefix;

	private final boolean daemoThread;

	private final ThreadGroup threadGroup;

	public ZlrpcThreadFactory() {
		this("rpcserver-threadpool-" + THREAD_NUMBER.getAndIncrement(), false);
	}

	public ZlrpcThreadFactory(String prefix) {
		this(prefix, false);
	}

	public ZlrpcThreadFactory(String prefix, boolean daemo) {
		this.prefix = StringUtils.isNotEmpty(prefix) ? prefix + "-thread-" : "";
		daemoThread = daemo;
		SecurityManager s = System.getSecurityManager();
		threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
		System.out.println("Thread create finished !");
	}

	@Override
	public Thread newThread(Runnable runnable) {
		String name = prefix + mThreadNum.getAndIncrement();
		Thread ret = new Thread(threadGroup, runnable, name, 0);
		ret.setDaemon(daemoThread);
		return ret;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}
}
