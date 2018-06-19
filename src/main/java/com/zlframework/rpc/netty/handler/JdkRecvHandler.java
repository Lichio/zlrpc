package com.zlframework.rpc.netty.handler;

import com.zlframework.rpc.serialize.RpcSerialize;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * zlrpc com.zlframework.rpc.netty.handler
 *
 * @author Lichaojie
 * @version 2018/4/26 20:53
 */
public class JdkRecvHandler implements RpcHandler {

	@Override
	public void handle(ChannelPipeline pipeline) {
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, RpcSerialize.MESSAGE_LENGTH, 0, RpcSerialize.MESSAGE_LENGTH));
		pipeline.addLast(new LengthFieldPrepender(RpcSerialize.MESSAGE_LENGTH));
		pipeline.addLast(new ObjectEncoder());
		pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
		pipeline.addLast(new MessageRecvHandler());
	}
}
