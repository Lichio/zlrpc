package com.zlframework.rpc.serialize.hessian;

import com.zlframework.rpc.serialize.RpcEncoder;
import com.zlframework.rpc.serialize.RpcSerialize;

/**
 * zlrpc com.zlframework.rpc.serialize.hessian
 *
 * @author Lichaojie
 * @version 2018/5/4 12:09
 */
public class HessianEncoder extends RpcEncoder {

	public HessianEncoder(){
		super(new HessianSerializer());
	}
}
