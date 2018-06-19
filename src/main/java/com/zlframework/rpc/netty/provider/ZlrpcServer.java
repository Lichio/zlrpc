package com.zlframework.rpc.netty.provider;

import com.zlframework.rpc.netty.config.NettyConfig;
import com.zlframework.rpc.utils.IpUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Date;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/24 20:08
 */
public class ZlrpcServer {

	private static volatile ZlrpcServer instance ;

	private int port;
	private String serialize;

	private EventLoopGroup boss ;
	private EventLoopGroup worker ;
	private Channel channel;

	private ZlrpcServer(){
	}

	public static ZlrpcServer getInstance(){
		if(instance == null){
			synchronized (ZlrpcServer.class){
				if(instance == null){
					instance = new ZlrpcServer();
				}
			}
		}
		return instance;
	}

	/**
	 * 启动Netty服务
	 *
	 */
	public void start() {

		synchronized (ZlrpcServer.class) {

			boss = new NioEventLoopGroup();
			worker = new NioEventLoopGroup(NettyConfig.IO_THREAD_NUMBER);

			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new RecvChannelInitializer(serialize));
			try {
				//channel = serverBootstrap.bind(port).sync().channel();
				ChannelFuture future = bootstrap.bind(port).sync();
				channel = future.channel();
				//final ChannelFuture future = bootstrap.bind(port).sync();
				//future.channel().closeFuture().sync();
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture channelFuture) throws Exception {
						if (channelFuture.isSuccess()) {
							System.out.printf("ZLRPC Server start success!\nip:%s\nport:%d\nprotocol:%s\nstart-time:%s\n\n", IpUtils.localIp(), port, serialize, new Date());
						}else {
							System.out.println("ZLRPC Server start failed!");
							channelFuture.channel().close();
						}
					}
				});

			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 停止Netty服务
	 */
	public void stop() {
		if (null == channel) {
			throw new RuntimeException("Netty Server Stoped");
		}
		boss.shutdownGracefully();
		worker.shutdownGracefully();
		channel.closeFuture().syncUninterruptibly();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSerialize() {
		return serialize;
	}

	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}
}
