package com.zlframework.rpc.serialize;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * zlrpc com.zlframework.rpc.serialize
 *
 * @author Lichaojie
 * @version 2018/4/26 20:33
 */
public enum  RpcSerializeProtocol {
	JDKSERIALIZE("jdknative"), KRYOSERIALIZE("kryo"), HESSIANSERIALIZE("hessian"), PROTOBUFSERIALIZE("protobuf");

	private String serializeProtocol;

	RpcSerializeProtocol(String serializeProtocol) {
		this.serializeProtocol = serializeProtocol;
	}

	@Override
	public String toString() {
		ReflectionToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		return ReflectionToStringBuilder.toString(this);
	}

	public String getProtocol() {
		return serializeProtocol;
	}

	public static RpcSerializeProtocol getSerialize(String protocol){
		switch (protocol){
			case "hessian" :
				return HESSIANSERIALIZE;
			case "protobuf":
				return PROTOBUFSERIALIZE;
			case "jdknative" :
			default:
				return JDKSERIALIZE;
		}
	}

	public static void main(String[] args){
		System.out.println(RpcSerializeProtocol.HESSIANSERIALIZE);
	}
}
