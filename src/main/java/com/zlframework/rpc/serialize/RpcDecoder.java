package com.zlframework.rpc.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * zlrpc com.zlframework.rpc.serialize
 *
 * @author Lichaojie
 * @version 2018/5/4 12:04
 */
public class RpcDecoder extends ByteToMessageDecoder {

	//解码对象class
	private Class<?> clazz;

	private RpcSerialize serializer;

	public RpcDecoder(Class<?> clazz,RpcSerialize serializer) {
		this.clazz = clazz;
		this.serializer = serializer;
	}

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//获取消息头所标识的消息体字节数组长度
		if (in.readableBytes() < RpcSerialize.MESSAGE_LENGTH) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (dataLength < 0) {
			ctx.close();
		}
		//若当前可以获取到的字节数小于实际长度,则直接返回,直到当前可以获取到的字节数等于实际长度
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}
		//读取完整的消息体字节数组
		byte[] data = new byte[dataLength];
		in.readBytes(data);

		//将字节数组反序列化为java对象(SerializerEngine参考序列化与反序列化章节)
		Object obj = serializer.deserialize(data, clazz);
		out.add(obj);
	}

}
