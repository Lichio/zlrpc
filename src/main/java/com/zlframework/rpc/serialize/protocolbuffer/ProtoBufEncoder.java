package com.zlframework.rpc.serialize.protocolbuffer;

import com.zlframework.rpc.serialize.RpcEncoder;

/**
 * zlrpc com.zlframework.rpc.serialize.protocolbuffer
 *
 * @author Lichaojie
 * @version 2018/5/4 15:13
 */
public class ProtoBufEncoder extends RpcEncoder{
	public ProtoBufEncoder(){
		super(new ProtoBufSerializer());
	}
}
