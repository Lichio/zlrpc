package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.serialize.protocolbuffer.ProtoBufDecoder;
import com.zlframework.rpc.serialize.protocolbuffer.ProtoBufEncoder;
import io.netty.channel.ChannelPipeline;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/5/4 15:28
 */
public class ProtoBufSendHandler implements RpcHandler {
	@Override
	public void handle(ChannelPipeline pipeline) {
		pipeline.addLast(new ProtoBufEncoder());
		pipeline.addLast(new ProtoBufDecoder(RespMessage.class));
		pipeline.addLast(new MessageSendHandler());
	}
}
