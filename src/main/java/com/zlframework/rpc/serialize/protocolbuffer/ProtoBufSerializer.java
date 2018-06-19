package com.zlframework.rpc.serialize.protocolbuffer;

import com.google.protobuf.GeneratedMessageV3;
import com.zlframework.rpc.serialize.RpcSerialize;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * zlrpc com.zlframework.rpc.serialize.protobuff
 *
 * @author Lichaojie
 * @version 2018/5/4 15:00
 */
public class ProtoBufSerializer implements RpcSerialize{

	@Override
	public <T> byte[] serialize(T obj) {
		try {
			if(!(obj instanceof GeneratedMessageV3)){
				throw new UnsupportedOperationException("not supported obj type.");
			}
			return (byte[]) MethodUtils.invokeMethod(obj, "toByteArray");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> cls) {
		try {
			if(!GeneratedMessageV3.class.isAssignableFrom(cls)){
				throw new UnsupportedOperationException("not supported obj type.");
			}
			Object o = MethodUtils.invokeStaticMethod(cls, "getDefaultInstance");
			return (T) MethodUtils.invokeMethod(o, "parseFrom", new Object[]{data});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
