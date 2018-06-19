package com.zlframework.rpc.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * zlrpc com.zlframework.rpc.serialize
 *
 * @author Lichaojie
 * @version 2018/5/4 12:07
 */
public class RpcEncoder  extends MessageToByteEncoder {
	//序列化类型
	private RpcSerialize serializer;

	public RpcEncoder(RpcSerialize serializer) {
		this.serializer = serializer;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
		//将对象序列化为字节数组
		byte[] data = serializer.serialize(in);
		//将字节数组(消息体)的长度作为消息头写入,解决半包/粘包问题
		out.writeInt(data.length);
		//写入序列化后得到的字节数组
		out.writeBytes(data);
	}
}
