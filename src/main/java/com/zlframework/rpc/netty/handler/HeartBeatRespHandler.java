package com.zlframework.rpc.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/23 21:50
 */
@ChannelHandler.Sharable
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + " ---> Server : " + msg.toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.READER_IDLE) {
				//throw new Exception("idle exception");
			}
		} else {
			ctx.fireUserEventTriggered(evt);
		}
	}

}
