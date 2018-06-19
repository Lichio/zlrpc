package com.zlframework.rpc.serialize;

import com.zlframework.rpc.netty.handler.*;

/**
 * zlrpc com.zlframework.rpc.serialize
 *
 * @author Lichaojie
 * @version 2018/4/28 16:26
 */
public class RpcSendSerializeFrame {
	public static RpcHandler select(String protocol){
		switch (RpcSerializeProtocol.getSerialize(protocol)){
			case HESSIANSERIALIZE:
				return new HessianSendHandler();
			case PROTOBUFSERIALIZE:
				return new ProtoBufSendHandler();
			case JDKSERIALIZE:
			default:
				return new JdkSendHandler();

		}
	}
}
