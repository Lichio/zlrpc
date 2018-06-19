package com.zlframework.rpc.netty.provider;

import com.google.common.util.concurrent.*;
import com.zlframework.rpc.core.SystemConfig;
import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.parallel.ZlrpcThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.*;

/**
 * zlrpc com.zlframework.rpc.netty
 *
 * @author Lichaojie
 * @version 2018/4/28 9:50
 */
public class MessageRecvExecutor {

	private static int threadNums = SystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
	private static int queueNums = SystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;
	private static volatile ListeningExecutorService threadPoolExecutor;


	public static void submit(Callable<RespMessage> task, final ChannelHandlerContext ctx){
		if (threadPoolExecutor == null) {
			synchronized (MessageRecvExecutor.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = MoreExecutors.listeningDecorator(ZlrpcThreadPoolFactory.getExecutor(threadNums, queueNums));
				}
			}
		}

		ListenableFuture<RespMessage> listenableFuture = threadPoolExecutor.submit(task);
		Futures.addCallback(listenableFuture, new FutureCallback<RespMessage>() {
			@Override
			public void onSuccess(final RespMessage response) {
				ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture channelFuture) throws Exception {
						System.out.println("RPC Server Send message-id respone:" + response.getMessageId());
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		}, threadPoolExecutor);

	}
}
