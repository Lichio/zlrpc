package com.zlframework.rpc.serialize.protocolbuffer;

import com.zlframework.rpc.serialize.RpcDecoder;

/**
 * zlrpc com.zlframework.rpc.serialize.protocolbuffer
 *
 * @author Lichaojie
 * @version 2018/5/4 15:13
 */
public class ProtoBufDecoder extends RpcDecoder {
	public ProtoBufDecoder(Class<?> clazz){
		super(clazz,new ProtoBufSerializer());
	}
}
