package com.zlframework.rpc.netty.consumer;

import com.zlframework.rpc.serialize.RpcSendSerializeFrame;
import com.zlframework.rpc.serialize.RpcSerializeProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * zlrpc com.zlframework.rpc.netty.consumer
 *
 * @author Lichaojie
 * @version 2018/4/28 16:25
 */
public class SendChannelInitializer extends ChannelInitializer<SocketChannel> {

	private String serialize;

	public SendChannelInitializer(String serialize){
			this.serialize = serialize;
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
			ChannelPipeline pipeline = socketChannel.pipeline();
			RpcSendSerializeFrame.select(serialize).handle(pipeline);
	}
}
