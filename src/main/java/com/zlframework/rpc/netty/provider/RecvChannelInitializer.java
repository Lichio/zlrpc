package com.zlframework.rpc.netty.provider;

import com.zlframework.rpc.serialize.RpcRecvSerializeFrame;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/26 20:41
 */
public class RecvChannelInitializer extends ChannelInitializer<SocketChannel> {

	private String serialize;

	public RecvChannelInitializer(String serialize){
		this.serialize = serialize;
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		RpcRecvSerializeFrame.select(serialize).handle(pipeline);
	}
}
