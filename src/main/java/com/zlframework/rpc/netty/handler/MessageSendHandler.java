package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.netty.consumer.ClientResultHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/28 16:16
 */
public class MessageSendHandler extends SimpleChannelInboundHandler<RespMessage> {
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, RespMessage response) throws Exception {
		//将Netty异步返回的结果存入阻塞队列,以便调用端同步获取
		ClientResultHolder.putResultValue(response);
	}
}
