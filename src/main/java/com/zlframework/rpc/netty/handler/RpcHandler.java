package com.zlframework.rpc.netty.handler;

import io.netty.channel.ChannelPipeline;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/26 20:50
 */
public interface RpcHandler {
	public void handle(ChannelPipeline pipeline);
}
