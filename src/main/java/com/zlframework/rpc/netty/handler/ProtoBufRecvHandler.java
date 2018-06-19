package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.model.ReqMessage;
import com.zlframework.rpc.serialize.protocolbuffer.ProtoBufDecoder;
import io.netty.channel.ChannelPipeline;
import com.zlframework.rpc.serialize.protocolbuffer.ProtoBufEncoder;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/5/4 15:24
 */
public class ProtoBufRecvHandler implements RpcHandler {
	@Override
	public void handle(ChannelPipeline pipeline) {
		pipeline.addLast(new ProtoBufEncoder());
		pipeline.addLast(new ProtoBufDecoder(ReqMessage.class));
		pipeline.addLast(new MessageRecvHandler());
	}
}
