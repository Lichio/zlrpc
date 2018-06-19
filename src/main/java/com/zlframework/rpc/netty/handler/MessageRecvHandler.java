package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.model.ReqMessage;
import com.zlframework.rpc.netty.provider.MessageRecvExecutor;
import com.zlframework.rpc.netty.provider.ServerTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/27 9:44
 */
public class MessageRecvHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ReqMessage request = (ReqMessage)msg;
		ServerTask task = new ServerTask(request);
		MessageRecvExecutor.submit(task,ctx);
	}
}
