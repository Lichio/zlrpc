package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.model.ReqMessage;
import com.zlframework.rpc.serialize.hessian.HessianDecoder;
import com.zlframework.rpc.serialize.hessian.HessianEncoder;
import io.netty.channel.ChannelPipeline;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/5/4 14:48
 */
public class HessianRecvHandler implements RpcHandler {
	@Override
	public void handle(ChannelPipeline pipeline) {
		pipeline.addLast(new HessianDecoder(ReqMessage.class));
		pipeline.addLast(new HessianEncoder());
		pipeline.addLast(new MessageRecvHandler());
	}
}
