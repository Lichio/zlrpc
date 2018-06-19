package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.serialize.marshalling.MarshallingDecoder;
import com.zlframework.rpc.serialize.marshalling.MarshallingEncoder;
import io.netty.channel.ChannelPipeline;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/5/4 15:45
 */
public class MarshallingSendHandler implements RpcHandler {
	@Override
	public void handle(ChannelPipeline pipeline) {
		pipeline.addLast(new MarshallingEncoder());
		pipeline.addLast(new MarshallingDecoder(RespMessage.class));
		pipeline.addLast(new MessageSendHandler());
	}
}
