package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.netty.config.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/24 11:03
 */
@ChannelHandler.Sharable
public abstract class ReconnectionHandler extends ChannelInboundHandlerAdapter implements TimerTask{

	private Timer timer;
	private int attempts;
	private Bootstrap bootstrap;
	private InetSocketAddress socketAddress;
	private ArrayList<ChannelHandler> handlers;

	public ReconnectionHandler(Bootstrap bootstrap,InetSocketAddress socketAddress,Timer timer){
		this.bootstrap = bootstrap;
		this.socketAddress = socketAddress;
		this.timer = timer;
		handlers = new ArrayList<>();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("当前链路已经激活了，重连尝试次数重新置为0");
		attempts = 0;
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("链接关闭");
		if(NettyConfig.RECONNECT_STATE){
			System.out.println("链接关闭，将进行重连");
			if (attempts < NettyConfig.RECONNECT_ATTEMPTS) {
				attempts++;
			}           //重连的间隔时间会越来越长
			int timeout = 2 << attempts;
			timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
		}
		ctx.fireChannelInactive();
	}

	@Override
	public void run(Timeout timeout) throws Exception {
		ChannelFuture future;
		synchronized (bootstrap){
			bootstrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					//ch.pipeline().addLast(handlers());
				}
			});
		  	future = bootstrap.connect(socketAddress);
		}
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				boolean succeed = f.isSuccess();
				//如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
				if (!succeed) {
					System.out.println("重连失败");
					f.channel().pipeline().fireChannelInactive();
				}else{
					System.out.println("重连成功");
				}
			}
		});
	}

//	@Override
//	public ChannelHandler[] handlers() {
//		ChannelHandler[] channelHandlers = new ChannelHandler[handlers.size()];
//		handlers.toArray(channelHandlers);
//		return channelHandlers;
//	}
//
//	public ReconnectionHandler add(ChannelHandler handler){
//		handlers.add(handler);
//		return this;
//	}
}
