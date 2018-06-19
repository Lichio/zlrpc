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
		System.out.println("��ǰ��·�Ѿ������ˣ��������Դ���������Ϊ0");
		attempts = 0;
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("���ӹر�");
		if(NettyConfig.RECONNECT_STATE){
			System.out.println("���ӹرգ�����������");
			if (attempts < NettyConfig.RECONNECT_ATTEMPTS) {
				attempts++;
			}           //�����ļ��ʱ���Խ��Խ��
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
				//�������ʧ�ܣ������ChannelInactive�������ٴγ��������¼���һֱ����12�Σ����ʧ����������
				if (!succeed) {
					System.out.println("����ʧ��");
					f.channel().pipeline().fireChannelInactive();
				}else{
					System.out.println("�����ɹ�");
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
