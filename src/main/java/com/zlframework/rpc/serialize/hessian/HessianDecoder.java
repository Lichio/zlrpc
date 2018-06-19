package com.zlframework.rpc.serialize.hessian;

import com.zlframework.rpc.serialize.RpcDecoder;

/**
 * zlrpc com.zlframework.rpc.serialize.hessian
 *
 * @author Lichaojie
 * @version 2018/5/4 12:19
 */
public class HessianDecoder extends RpcDecoder {
	public HessianDecoder(Class<?> clazz){
		super(clazz,new HessianSerializer());
	}
}
