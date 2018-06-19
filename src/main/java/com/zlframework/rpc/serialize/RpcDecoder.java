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

	//�������class
	private Class<?> clazz;

	private RpcSerialize serializer;

	public RpcDecoder(Class<?> clazz,RpcSerialize serializer) {
		this.clazz = clazz;
		this.serializer = serializer;
	}

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//��ȡ��Ϣͷ����ʶ����Ϣ���ֽ����鳤��
		if (in.readableBytes() < RpcSerialize.MESSAGE_LENGTH) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (dataLength < 0) {
			ctx.close();
		}
		//����ǰ���Ի�ȡ�����ֽ���С��ʵ�ʳ���,��ֱ�ӷ���,ֱ����ǰ���Ի�ȡ�����ֽ�������ʵ�ʳ���
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}
		//��ȡ��������Ϣ���ֽ�����
		byte[] data = new byte[dataLength];
		in.readBytes(data);

		//���ֽ����鷴���л�Ϊjava����(SerializerEngine�ο����л��뷴���л��½�)
		Object obj = serializer.deserialize(data, clazz);
		out.add(obj);
	}

}
