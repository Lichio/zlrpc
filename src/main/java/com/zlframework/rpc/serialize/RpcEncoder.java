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
	//���л�����
	private RpcSerialize serializer;

	public RpcEncoder(RpcSerialize serializer) {
		this.serializer = serializer;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
		//���������л�Ϊ�ֽ�����
		byte[] data = serializer.serialize(in);
		//���ֽ�����(��Ϣ��)�ĳ�����Ϊ��Ϣͷд��,������/ճ������
		out.writeInt(data.length);
		//д�����л���õ����ֽ�����
		out.writeBytes(data);
	}
}
