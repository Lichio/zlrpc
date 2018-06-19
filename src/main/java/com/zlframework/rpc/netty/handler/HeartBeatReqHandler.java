package com.zlframework.rpc.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Date;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/23 21:18
 */

@ChannelHandler.Sharable
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("激活时间是："+ new Date());
		System.out.println("HeartBeatReqHandler channelActive");
		ctx.fireChannelActive();
	}

//	@Override
//	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("停止时间是："+ new Date());
//		System.out.println("HeartBeatClientHandler channelInactive");
//
//		System.out.println("channelInactive : starting to reconnect ......");
//		ctx.channel().eventLoop().schedule(new Runnable() {
//			@Override
//			public void run() {
//				client.doConnect();
//			}
//		},1,TimeUnit.SECONDS);
//
//		ctx.fireChannelInactive();
//	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.WRITER_IDLE) {
				// write heartbeat to server
				ctx.channel().writeAndFlush("client-ping.");
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}


}

