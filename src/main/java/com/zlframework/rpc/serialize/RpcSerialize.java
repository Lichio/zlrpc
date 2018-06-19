package com.zlframework.rpc.serialize;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * zlrpc com.zlframework.rpc.serialize
 *
 * @author Lichaojie
 * @version 2018/4/26 20:58
 */
public interface RpcSerialize {
	final static int MESSAGE_LENGTH = 4;

	/**
	 * 序列化
	 *
	 * @param obj
	 * @param <T>
	 * @return
	 */
	public <T> byte[] serialize(T obj);


	/**
	 * 反序列化
	 *
	 * @param data
	 * @param <T>
	 * @return
	 */
	public <T> T deserialize(byte[] data,Class<T> clazz);
}
