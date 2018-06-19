package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.model.ReqMessage;
import com.zlframework.rpc.serialize.marshalling.MarshallingDecoder;
import com.zlframework.rpc.serialize.marshalling.MarshallingEncoder;
import io.netty.channel.ChannelPipeline;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/5/4 15:43
 */
public class MarshallingRecvHandler implements RpcHandler {
	@Override
	public void handle(ChannelPipeline pipeline) {
		pipeline.addLast(new MarshallingDecoder(ReqMessage.class));
		pipeline.addLast(new MarshallingEncoder());
		pipeline.addLast(new MessageRecvHandler());
	}
}
