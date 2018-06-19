package com.zlframework.rpc.serialize.marshalling;

import com.zlframework.rpc.serialize.RpcEncoder;

/**
 * zlrpc com.zlframework.rpc.serialize.marshalling
 *
 * @author Lichaojie
 * @version 2018/5/4 15:42
 */
public class MarshallingEncoder extends RpcEncoder {
	public MarshallingEncoder(){
		super(new MarshallingSerializer());
	}
}
