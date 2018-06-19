package com.zlframework.rpc.serialize.marshalling;

import com.zlframework.rpc.serialize.RpcDecoder;

/**
 * zlrpc com.zlframework.rpc.serialize.marshalling
 *
 * @author Lichaojie
 * @version 2018/5/4 15:42
 */
public class MarshallingDecoder extends RpcDecoder {
	public MarshallingDecoder(Class<?> clazz){
		super(clazz,new MarshallingSerializer());
	}
}
