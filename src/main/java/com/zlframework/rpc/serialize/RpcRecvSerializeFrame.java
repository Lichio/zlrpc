package com.zlframework.rpc.serialize;

import com.zlframework.rpc.netty.handler.HessianRecvHandler;
import com.zlframework.rpc.netty.handler.JdkRecvHandler;
import com.zlframework.rpc.netty.handler.ProtoBufRecvHandler;
import com.zlframework.rpc.netty.handler.RpcHandler;

/**
 * zlrpc com.zlframework.rpc.serialize
 *
 * @author Lichaojie
 * @version 2018/4/26 21:05
 */
public class RpcRecvSerializeFrame {

	public static RpcHandler select(String protocol){
		switch (RpcSerializeProtocol.getSerialize(protocol)){
			case HESSIANSERIALIZE:
				return new HessianRecvHandler();
			case PROTOBUFSERIALIZE:
				return new ProtoBufRecvHandler();
			case JDKSERIALIZE:
			default:
				return new JdkRecvHandler();
		}
	}
}
